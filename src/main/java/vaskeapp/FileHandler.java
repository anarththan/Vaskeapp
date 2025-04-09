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
    private static final String OPPVASK_FIL = BASE_DIR + "oppvask.txt";
    private static final String PRIKK_FIL = BASE_DIR + "prikker.txt";
    private static final String POENG_FIL = BASE_DIR + "poeng.txt";
    private static final String VASK_FIL = BASE_DIR + "sistevask.txt";

    // ----------------------------------------------------
    // OPPVASK
    // ----------------------------------------------------
    public static void skrivOppvask(List<Oppvask> oppvaskListe) {
        lagDir();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OPPVASK_FIL))) {
            for (Oppvask opp : oppvaskListe) {
                Person p = opp.getPerson();
                if (p == null) continue;
                List<LocalDate> datoer = opp.getOppvaskdatoer();
                StringBuilder sb = new StringBuilder();
                sb.append(p.getName()).append(";").append(datoer.size());
                for (LocalDate d : datoer) {
                    sb.append(";").append(d.toString());
                }
                writer.write(sb.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void lesOppvask(List<Oppvask> oppvaskListe, List<Person> personListe) {
        oppvaskListe.clear();
        File f = new File(OPPVASK_FIL);
        if (!f.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String linje;
            while ((linje = reader.readLine()) != null) {
                String[] deler = linje.split(";");
                if (deler.length < 2) continue;
                String personNavn = deler[0];
                int antDatoer = Integer.parseInt(deler[1]);
                Person p = finnPerson(personNavn, personListe);
                if (p == null) {
                    p = new Person(personNavn);
                    personListe.add(p);
                }
                Oppvask opp = new Oppvask(p);
                int idx = 2;
                for (int i = 0; i < antDatoer; i++) {
                    LocalDate d = LocalDate.parse(deler[idx]);
                    opp.leggTilOppvaskDato(d);
                    idx++;
                }
                oppvaskListe.add(opp);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------
    // PRIKKER
    // ----------------------------------------------------
    public static void skrivPrikker(List<Person> personListe) {
        lagDir();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRIKK_FIL))) {
            for (Person p : personListe) {
                // Lagre kun antall "gyldige" prikker
                writer.write(p.getName() + ";" + p.getAntallPrikker());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void lesPrikker(List<Person> personListe) {
        File f = new File(PRIKK_FIL);
        if (!f.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String linje;
            while ((linje = reader.readLine()) != null) {
                String[] deler = linje.split(";");
                if (deler.length < 2) continue;
                String navn = deler[0];
                int ant = Integer.parseInt(deler[1]);
                Person p = finnPerson(navn, personListe);
                if (p == null) {
                    p = new Person(navn);
                    personListe.add(p);
                }
                // Legg til "ant" prikker (uten spesifikk årsak)
                for (int i = 0; i < ant; i++) {
                    p.addPrikker("Lastet fra fil");
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------
    // SCOREBOARD
    // ----------------------------------------------------
    public static void skrivScoreboard(ScoreBoard sb, List<Person> personListe) {
        lagDir();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(POENG_FIL))) {
            for (Person p : personListe) {
                int poeng = sb.getPoeng(p);
                if (poeng > 0) {
                    writer.write(p.getName() + ";" + poeng);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void lesScoreboard(ScoreBoard sb, List<Person> personListe) {
        File f = new File(POENG_FIL);
        if (!f.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String linje;
            while ((linje = reader.readLine()) != null) {
                String[] deler = linje.split(";");
                if (deler.length < 2) continue;
                String navn = deler[0];
                int pts = Integer.parseInt(deler[1]);
                Person p = finnPerson(navn, personListe);
                if (p == null) {
                    p = new Person(navn);
                    personListe.add(p);
                }
                sb.leggTilPoeng(p, pts);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------
    // SISTE VASK
    // ----------------------------------------------------
    public static void skrivSisteVask(List<Ansvarsomrader> omrader) {
        lagDir();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(VASK_FIL))) {
            for (Ansvarsomrader ao : omrader) {
                String type = ao.getClass().getSimpleName();
                String navn = ao.getAnsvarlig().getName();
                String siste = (ao.getSisteVask() == null) ? "null" : ao.getSisteVask().toString();
                String frist = (ao.getFristForNesteVask() == null) ? "null" : ao.getFristForNesteVask().toString();
                writer.write(type + ";" + navn + ";" + siste + ";" + frist);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void lesSisteVask(List<Ansvarsomrader> omrader, List<Person> personListe) {
        File f = new File(VASK_FIL);
        if (!f.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String linje;
            while ((linje = reader.readLine()) != null) {
                String[] deler = linje.split(";");
                if (deler.length < 4) continue;
                String type = deler[0];
                String navn = deler[1];
                String sisteStr = deler[2];
                String fristStr = deler[3];
                Person p = finnPerson(navn, personListe);
                if (p == null) {
                    p = new Person(navn);
                    personListe.add(p);
                }
                for (Ansvarsomrader ao : omrader) {
                    if (ao.getClass().getSimpleName().equals(type)) {
                        ao.setAnsvarlig(p);
                        if (!sisteStr.equals("null")) {
                            LocalDate sd = LocalDate.parse(sisteStr);
                            // Cast:
                            if (ao instanceof AbstractAnsvarsOmrade abs) {
                                abs.sisteVask = sd;
                            }
                        }
                        if (!fristStr.equals("null")) {
                            LocalDate fd = LocalDate.parse(fristStr);
                            if (ao instanceof AbstractAnsvarsOmrade abs) {
                                abs.fristForNesteVask = fd;
                            }
                        }
                        break;
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------
    // HJELPEMETODER
    // ----------------------------------------------------
    private static Person finnPerson(String navn, List<Person> pliste) {
        for (Person p : pliste) {
            if (p.getName().equalsIgnoreCase(navn)) {
                return p;
            }
        }
        return null;
    }

    private static void lagDir() {
        File dir = new File(BASE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
