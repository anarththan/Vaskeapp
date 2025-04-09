package vaskeapp;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class MainController {

    @FXML private Label navnLabel1, navnLabel2, navnLabel3, navnLabel4;
    @FXML private Label prikkLabel1, prikkLabel2, prikkLabel3, prikkLabel4;
    @FXML private Label sisteVaskLabel1, sisteVaskLabel2, sisteVaskLabel3, sisteVaskLabel4;
    @FXML private TextField datoField1, datoField2, datoField3, datoField4;
    @FXML private ChoiceBox<String> actionBox1, actionBox2, actionBox3, actionBox4;

    @FXML private TextArea scoreboardArea;

    // Data i minnet
    private List<Person> personListe;
    private List<Ansvarsomrader> omrader;  // (Badet, Kjøkken, Gangen, Stua)
    private ScoreBoard scoreboard;
    private List<Oppvask> oppvaskListe;

    @FXML
    public void initialize() {
        // Hent data fra DataStore
        this.personListe = DataStore.personListe;
        this.omrader = DataStore.omrader;
        this.scoreboard = DataStore.scoreboard;
        this.oppvaskListe = DataStore.oppvaskListe;

        // Sett opp ChoiceBoxes med "Oppvask" / "Ansvarsområde"
        List<String> actions = Arrays.asList("Oppvask", "Ansvarsområde");
        actionBox1.getItems().addAll(actions);
        actionBox2.getItems().addAll(actions);
        actionBox3.getItems().addAll(actions);
        actionBox4.getItems().addAll(actions);

        // Default "Oppvask"
        actionBox1.setValue("Oppvask");
        actionBox2.setValue("Oppvask");
        actionBox3.setValue("Oppvask");
        actionBox4.setValue("Oppvask");

        // Oppdater GUI labels
        if (personListe.size() >= 4 && omrader.size() >= 4) {
            navnLabel1.setText(personListe.get(0).getName());
            navnLabel2.setText(personListe.get(1).getName());
            navnLabel3.setText(personListe.get(2).getName());
            navnLabel4.setText(personListe.get(3).getName());

            updateRow(0, prikkLabel1, sisteVaskLabel1);
            updateRow(1, prikkLabel2, sisteVaskLabel2);
            updateRow(2, prikkLabel3, sisteVaskLabel3);
            updateRow(3, prikkLabel4, sisteVaskLabel4);
        }

        updateScoreboard();
    }

    @FXML
    private void handleBekreft1() {
        handleBekreft(0, actionBox1, datoField1, prikkLabel1, sisteVaskLabel1);
    }
    @FXML
    private void handleBekreft2() {
        handleBekreft(1, actionBox2, datoField2, prikkLabel2, sisteVaskLabel2);
    }
    @FXML
    private void handleBekreft3() {
        handleBekreft(2, actionBox3, datoField3, prikkLabel3, sisteVaskLabel3);
    }
    @FXML
    private void handleBekreft4() {
        handleBekreft(3, actionBox4, datoField4, prikkLabel4, sisteVaskLabel4);
    }

    private void handleBekreft(int index,
                               ChoiceBox<String> actionBox,
                               TextField datoField,
                               Label prikkLabel,
                               Label sisteVaskLabel) {
        if (index >= omrader.size()) return;

        String action = actionBox.getValue(); // "Oppvask" eller "Ansvarsområde"
        String datoStr = datoField.getText().trim();
        LocalDate ld;
        try {
            ld = LocalDate.parse(datoStr); // format "YYYY-MM-DD"
        } catch (DateTimeParseException e) {
            showAlert("Feil", "Ugyldig dato. Bruk YYYY-MM-DD");
            return;
        }

        Ansvarsomrader ao = omrader.get(index);
        Person p = ao.getAnsvarlig();

        if ("Oppvask".equals(action)) {
            // +1 poeng
            scoreboard.leggTilPoeng(p, 1);
            // Registrer Oppvask-hendelse
            Oppvask op = new Oppvask(p);
            op.leggTilOppvaskDato(ld);
            oppvaskListe.add(op);

        } else {
            // "Ansvarsområde" => registerVask(dato) => +5 poeng
            ao.registerVask(ld); // gir ev. prikk om for sent
            scoreboard.leggTilPoeng(p, 5);
        }

        // Oppdater rad
        updateRow(index, prikkLabel, sisteVaskLabel);
        updateScoreboard();
    }

    private void updateRow(int index, Label prikkLabel, Label sisteVaskLabel) {
        Ansvarsomrader ao = omrader.get(index);
        Person pers = ao.getAnsvarlig();

        int prikker = pers.getAntallPrikker();
        prikkLabel.setText("Prikker: " + prikker);

        // Hent siste vask fra ansvarsområdet
        if (ao.getSisteVask() != null) {
            sisteVaskLabel.setText("Siste: " + ao.getSisteVask().toString());
        } else {
            sisteVaskLabel.setText("Siste: -");
        }
    }

    private void updateScoreboard() {
        StringBuilder sb = new StringBuilder("--- SCOREBOARD ---\n");
        for (Person p : personListe) {
            sb.append(p.getName())
              .append(": ")
              .append(scoreboard.getPoeng(p))
              .append(" poeng\n");
        }
        scoreboardArea.setText(sb.toString());
    }

    // ----------------------------------------------------
    // Lagring/Lesing
    // ----------------------------------------------------
    @FXML
    private void handleLagre() {
        FileHandler.skrivPrikker(personListe);
        FileHandler.skrivScoreboard(scoreboard, personListe);
        FileHandler.skrivOppvask(oppvaskListe);
        FileHandler.skrivSisteVask(omrader);

        showAlert("Info", "Data lagret til filer.");
    }

    @FXML
    private void handleLast() {
        FileHandler.lesPrikker(personListe);
        FileHandler.lesScoreboard(scoreboard, personListe);
        FileHandler.lesOppvask(oppvaskListe, personListe);
        FileHandler.lesSisteVask(omrader, personListe);

        // Oppdater GUI
        updateRow(0, prikkLabel1, sisteVaskLabel1);
        updateRow(1, prikkLabel2, sisteVaskLabel2);
        updateRow(2, prikkLabel3, sisteVaskLabel3);
        updateRow(3, prikkLabel4, sisteVaskLabel4);
        updateScoreboard();

        showAlert("Info", "Data lastet fra filer.");
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
