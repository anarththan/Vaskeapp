package vaskeapp;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    @FXML private TextField navnField;
    @FXML private ChoiceBox<Person> personChoiceOppvask;
    @FXML private ChoiceBox<Ansvarsomrader> ansvarChoice;
    @FXML private TextArea outputArea;

    // Data i minnet
    private List<Person> personListe = new ArrayList<>();
    private List<Ansvarsomrader> omrader = new ArrayList<>();  // Badet, Kjøkken etc.
    private ScoreBoard scoreboard = new ScoreBoard();
    
    @FXML
    public void initialize() {
        // Opprett 4 default ansvarsområder (badet, kjøkken, gangen, stua) – uten ansvarlig
        omrader.add(new Badet(null, LocalDate.now()));
        omrader.add(new Kjokken(null, LocalDate.now()));
        omrader.add(new Gangen(null, LocalDate.now()));
        omrader.add(new Stua(null, LocalDate.now()));
        
        // Oppdater ChoiceBox for ansvarsområder
        ansvarChoice.getItems().addAll(omrader);
        ansvarChoice.setConverter(new javafx.util.StringConverter<Ansvarsomrader>() {
            @Override
            public String toString(Ansvarsomrader obj) {
                if (obj == null) return "";
                return obj.getClass().getSimpleName() + 
                       " (ansvarlig: " + (obj.getAnsvarlig() != null ? obj.getAnsvarlig().getName() : "Ingen") + ")";
            }
            @Override
            public Ansvarsomrader fromString(String string) {return null;}
        });
    }
    
    @FXML
    private void handleLeggTilPerson() {
        String navn = navnField.getText().trim();
        if (navn.isEmpty()) {
            showAlert("Feil", "Navn kan ikke være tomt");
            return;
        }
        Person p = new Person(navn);
        personListe.add(p);
        personChoiceOppvask.getItems().add(p);
        navnField.clear();
    }
    
    @FXML
    private void handleRegistrerOppvask() {
        Person p = personChoiceOppvask.getValue();
        if (p == null) {
            showAlert("Feil", "Velg en person for oppvask");
            return;
        }
        // Oppvask = 1 poeng
        scoreboard.leggTilPoeng(p, 1);
        outputArea.appendText("Oppvask utført av " + p.getName() + ". +1 poeng\n");
    }
    
    @FXML
    private void handleRegistrerVask() {
        Ansvarsomrader ans = ansvarChoice.getValue();
        if (ans == null) {
            showAlert("Feil", "Velg et ansvarsområde");
            return;
        }
        // Sjekk om det har en ansvarlig person
        Person ansvarlig = ans.getAnsvarlig();
        if (ansvarlig == null) {
            showAlert("Feil", "Ingen ansvarlig person er satt for dette området.\n" + 
                              "Sett ansvarlig manuelt i koden eller endre designen for å velge ansvarlig person.");
            return;
        }
        // Registrer vask
        ans.registerVask(LocalDate.now());
        // Gi 5 poeng
        scoreboard.leggTilPoeng(ansvarlig, 5);
        outputArea.appendText("Vask registrert på " + ans.getClass().getSimpleName() 
                              + " av " + ansvarlig.getName() + ". +5 poeng\n");
    }
    
    @FXML
    private void handleVisScoreboard() {
        StringBuilder sb = new StringBuilder();
        for (Person p : personListe) {
            int poeng = scoreboard.getPoeng(p);
            sb.append(p.getName())
              .append(" -> Poeng: ").append(poeng)
              .append(", Prikker: ").append(p.getAntallPrikker())
              .append("\n");
        }
        outputArea.setText(sb.toString());
    }
    
    @FXML
    private void handleLagre() {
        // Skriv prikk-fil, poeng-fil, siste-vask-fil
        FileHandler.skrivPrikker(personListe);
        FileHandler.skrivScoreboard(scoreboard, personListe);
        FileHandler.skrivSisteVask(omrader);
        showAlert("Info", "Data lagret til filer.");
    }
    
    @FXML
    private void handleLast() {
        // Les prikk-fil, poeng-fil, siste-vask-fil
        FileHandler.lesPrikker(personListe);
        FileHandler.lesScoreboard(scoreboard, personListe);
        FileHandler.lesSisteVask(omrader, personListe);
        
        // Oppdater ChoiceBox (i tilfelle nye personer dukket opp fra fil)
        personChoiceOppvask.getItems().setAll(personListe);
        
        showAlert("Info", "Data lastet fra filer.");
    }
    
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
