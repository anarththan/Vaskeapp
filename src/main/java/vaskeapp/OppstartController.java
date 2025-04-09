package vaskeapp;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class OppstartController {

    @FXML private TextField navnField1, navnField2, navnField3, navnField4;
    @FXML private ComboBox<String> areaBox1, areaBox2, areaBox3, areaBox4;
    @FXML private Button nesteButton;

    private final List<String> muligOmrader = Arrays.asList("Badet", "Kjokken", "Gangen", "Stua");

    @FXML
    public void initialize() {
        // Fyll hver ComboBox med de 4 mulige rommene
        areaBox1.getItems().addAll(muligOmrader);
        areaBox2.getItems().addAll(muligOmrader);
        areaBox3.getItems().addAll(muligOmrader);
        areaBox4.getItems().addAll(muligOmrader);
    }

    @FXML
    private void handleNeste() {
        // 1) Hent input
        String navn1 = navnField1.getText().trim();
        String navn2 = navnField2.getText().trim();
        String navn3 = navnField3.getText().trim();
        String navn4 = navnField4.getText().trim();

        String area1 = areaBox1.getValue();
        String area2 = areaBox2.getValue();
        String area3 = areaBox3.getValue();
        String area4 = areaBox4.getValue();

        // 2) Valider
        if (navn1.isEmpty() || navn2.isEmpty() || navn3.isEmpty() || navn4.isEmpty()
            || area1 == null || area2 == null || area3 == null || area4 == null) {
            showAlert("Feil", "Vennligst fyll inn alle navn og velg alle ansvarsområder.");
            return;
        }
        Set<String> areaSet = new HashSet<>(Arrays.asList(area1, area2, area3, area4));
        if (areaSet.size() < 4) {
            showAlert("Feil", "To personer kan ikke ha samme ansvarsområde.");
            return;
        }

        // 3) Opprett Person-objekter
        Person p1, p2, p3, p4;
        try {
            p1 = new Person(navn1);
            p2 = new Person(navn2);
            p3 = new Person(navn3);
            p4 = new Person(navn4);
        } catch (IllegalArgumentException e) {
            showAlert("Feil", "Ugyldig navn: " + e.getMessage());
            return;
        }

        // 4) Opprett ansvarsområder
        Ansvarsomrader ao1 = createOmrade(area1, p1);
        Ansvarsomrader ao2 = createOmrade(area2, p2);
        Ansvarsomrader ao3 = createOmrade(area3, p3);
        Ansvarsomrader ao4 = createOmrade(area4, p4);

        // 5) Legg data i DataStore
        DataStore.clearAll();
        DataStore.personListe.addAll(Arrays.asList(p1, p2, p3, p4));
        DataStore.omrader.addAll(Arrays.asList(ao1, ao2, ao3, ao4));

        // 6) Bytt til MainView.fxml
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
            Scene mainScene = new Scene(loader.load());
            Stage stage = (Stage) nesteButton.getScene().getWindow();
            stage.setTitle("VaskeApp - Hovedscene");
            stage.setScene(mainScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Feil", "Kunne ikke laste MainView.fxml");
        }
    }

    private Ansvarsomrader createOmrade(String areaName, Person p) {
        switch (areaName) {
            case "Badet":
                return new Badet(p, java.time.LocalDate.now());
            case "Kjokken":
                return new Kjokken(p, java.time.LocalDate.now());
            case "Gangen":
                return new Gangen(p, java.time.LocalDate.now());
            case "Stua":
                return new Stua(p, java.time.LocalDate.now());
        }
        return null;
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
