package vaskeapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class FileHandler {
    private static final String BASE_DIR = "data/";
    private static final String PRIKK_FIL = BASE_DIR + "prikker.txt";
    private static final String POENG_FIL = BASE_DIR + "poeng.txt";
    private static final String VASK_FIL = BASE_DIR + "sistevask.txt";
    
    // ----------------------------------------------------
    // PRIKKER - LAGRING
    // ----------------------------------------------------
    public static void skrivPrikker(List<Person> personListe) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRIKK_FIL))) {
            for (Person p : personListe) {
                String linje = p.getName() + ";" + p.getAntallPrikker();
                writer.write(linje);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Eksempel på å lese *bare* antall prikker, men ikke deres årsaker/datoer
    public static void lesPrikker(List<Person> personListe) {
        File f = new File(PRIKK_FIL);
        if (!f.exists()) {
            System.out.println("Ingen " + PRIKK_FIL + " funnet, hopper over...");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String linje;
            while ((linje = reader.readLine()) != null) {
                // Format: navn;antallPrikker
                String[] deler = linje.split(";");
                if (deler.length < 2) continue;
                String navn = deler[0];
                int ant = Integer.parseInt(deler[1]);
                
                Person person = finnPerson(navn, personListe);
                if (person != null) {
                    // Legg til "ant" prikker
                    for (int i = 0; i < ant; i++) {
                        person.addPrikker("Lastet fra fil");
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
    
    // ----------------------------------------------------
    // POENG / SCOREBOARD
    // ----------------------------------------------------
    public static void skrivScoreboard(ScoreBoard scoreboard, List<Person> personListe) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(POENG_FIL))) {
            for (Person p : personListe) {
                int poeng = scoreboard.getPoeng(p);
                if (poeng > 0) {
                    String linje = p.getName() + ";" + poeng;
                    writer.write(linje);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void lesScoreboard(ScoreBoard scoreboard, List<Person> personListe) {
        File f = new File(POENG_FIL);
        if (!f.exists()) {
            System.out.println("Ingen " + POENG_FIL + " funnet, hopper over...");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String linje;
            while ((linje = reader.readLine()) != null) {
                // Format: navn;poeng
                String[] deler = linje.split(";");
                if (deler.length < 2) continue;
                String navn = deler[0];
                int poeng = Integer.parseInt(deler[1]);
                
                Person person = finnPerson(navn, personListe);
                if (person != null) {
                    scoreboard.leggTilPoeng(person, poeng);
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
    
    // ----------------------------------------------------
    // SISTE VASK - ANSVARSOMRÅDER
    // ----------------------------------------------------
    public static void skrivSisteVask(List<Ansvarsomrader> omrader) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(VASK_FIL))) {
            for (Ansvarsomrader o : omrader) {
                String type = o.getClass().getSimpleName();
                String ansvarlig = o.getAnsvarlig().getName();
                String siste = o.getSisteVask().toString();
                String frist = o.getFristForNesteVask().toString();
                
                String linje = type + ";" + ansvarlig + ";" + siste + ";" + frist;
                writer.write(linje);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void lesSisteVask(List<Ansvarsomrader> omrader, List<Person> personListe) {
        File f = new File(VASK_FIL);
        if (!f.exists()) {
            System.out.println("Ingen " + VASK_FIL + " funnet, hopper over...");
            return;
        }
        // NB: For en "full" løsning trenger vi å mappe type -> constructor
        // Her må du enten ha en "fabrikk" for Badet, Kjøkken etc.
        // Vi viser *et* eksempel på å gjenopprette data.
        
        // For enkelhet henter vi kun ut data og oppdaterer eksisterende omrader.
        // (dvs. at du *allerede* har opprettet 4 omrader i minne)
        
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String linje;
            while ((linje = reader.readLine()) != null) {
                // Format: type;navn;SisteVask;Frist
                String[] deler = linje.split(";");
                if (deler.length < 4) continue;
                String type = deler[0];
                String navn = deler[1];
                String sisteStr = deler[2];
                String fristStr = deler[3];
                
                // Finn person
                Person p = finnPerson(navn, personListe);
                if (p == null) {
                    p = new Person(navn);
                    personListe.add(p);
                }
                
                // Finn omr. i "omrader" av riktig type
                // (Helt forenklet: tar *første* vi finner av gitt type)
                for (Ansvarsomrader område : omrader) {
                    if (område.getClass().getSimpleName().equals(type)) {
                        // Oppdater
                        område.setAnsvarlig(p);
                        // Oppdater sisteVask/frist
                        // Men i AbstractAnsvarsOmrade er feltene beskyttet 
                        // => cast:
                        if (område instanceof AbstractAnsvarsOmrade ao) {
                            ao.sisteVask = LocalDate.parse(sisteStr);
                            ao.fristForNesteVask = LocalDate.parse(fristStr);
                        }
                        break; // fant en, går videre
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // ----------------------------------------------------
    // HJELPEMETODE
    // ----------------------------------------------------
    private static Person finnPerson(String navn, List<Person> liste) {
        for (Person p : liste) {
            if (p.getName().equalsIgnoreCase(navn)) {
                return p;
            }
        }
        return null;
    }
}
