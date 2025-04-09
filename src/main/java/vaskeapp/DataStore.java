package vaskeapp;

import java.util.ArrayList;
import java.util.List;

public class DataStore {
    public static List<Person> personListe = new ArrayList<>();
    public static List<Ansvarsomrader> omrader = new ArrayList<>();
    public static ScoreBoard scoreboard = new ScoreBoard();

    public static List<Oppvask> oppvaskListe = new ArrayList<>();

    public static void clearAll() {
        personListe.clear();
        omrader.clear();
        oppvaskListe.clear();
        scoreboard = new ScoreBoard();
    }
}
