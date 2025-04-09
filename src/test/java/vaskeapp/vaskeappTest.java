package vaskeapp;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class vaskeappTest {

    private Person person;
    private Person person2;
    private ScoreBoard scoreboard;
    private Oppvask oppvask;
    private Badet badet;

    @BeforeEach
    void setup() {
        person = new Person("Ola Nordmann");
        person2 = new Person("Kari Nordmann");
        scoreboard = new ScoreBoard();
        badet = new Badet(person, LocalDate.now());
        oppvask = new Oppvask(person);
    }

    @Test
    void testPersonPrikk() {
        assertEquals(0, person.getAntallPrikker(), 
            "Person skal starte med 0 prikker");
        person.addPrikker("Test årsak");
        assertEquals(1, person.getAntallPrikker(), 
            "Person skal ha 1 prikk etter addPrikker");
        
        for (int i = 0; i < 4; i++) {
            person.addPrikker("Ekstra prikk " + i);
        }
        assertEquals(5, person.getAntallPrikker(),
            "Nå skal personen ha 5 prikker");
    }
    @Test
    void testOppvaskLeggTilDato() {
        assertEquals(0, oppvask.getAntallOppvask());
        oppvask.leggTilOppvaskDato(LocalDate.of(2025, 3, 1));
        oppvask.leggTilOppvaskDato(LocalDate.of(2025, 3, 2));

        assertEquals(2, oppvask.getAntallOppvask());
        assertEquals(person, oppvask.getPerson(),
            "Oppvask skal være knyttet til 'person'");
    }

    @Test
    void testScoreBoard() {
        assertEquals(0, scoreboard.getPoeng(person));
        assertEquals(0, scoreboard.getPoeng(person2));

        scoreboard.leggTilPoeng(person, 1);
        scoreboard.leggTilPoeng(person2, 5);

        assertEquals(1, scoreboard.getPoeng(person),
            "Person skal ha 1 poeng");
        assertEquals(5, scoreboard.getPoeng(person2),
            "Person2 skal ha 5 poeng");
        scoreboard.leggTilPoeng(person, 4);
        assertEquals(5, scoreboard.getPoeng(person),
            "Nå skal Person ha totalt 5 poeng");
    }

    @Test
    void testRegisterVaskForBadet() {
        LocalDate now = LocalDate.now();
        assertEquals(now, badet.getSisteVask(),
            "Siste vask settes lik 'now' i konstruktør");
        LocalDate frist = badet.getFristForNesteVask();
        assertTrue(frist.isAfter(now),
            "Frist skal normalt være 2 uker etter now");

        LocalDate vaskDato = now.plusDays(10);
        badet.registerVask(vaskDato);
        assertEquals(vaskDato, badet.getSisteVask());
        assertEquals(0, person.getAntallPrikker(), 
            "Skal ikke få prikk hvis innen frist");
    }

    @Test
    void testFileHandler() {
        Person testPers = new Person("FilTest");
        testPers.addPrikker("TestPrikk1");
        testPers.addPrikker("TestPrikk2");
        Person testPers2 = new Person("FilTest");

        List<Person> persListe = new java.util.ArrayList<>();
        persListe.add(testPers);
        persListe.add(testPers2);

        ScoreBoard sb = new ScoreBoard();
        sb.leggTilPoeng(testPers, 10);
        sb.leggTilPoeng(testPers2, 2);

        List<Oppvask> oppvaskListe = new java.util.ArrayList<>();
        Oppvask o = new Oppvask(testPers);
        o.leggTilOppvaskDato(LocalDate.of(2025, 1, 1));
        oppvaskListe.add(o);

        Badet bad = new Badet(testPers, LocalDate.of(2025,1,5));
        Kjokken kj = new Kjokken(testPers2, LocalDate.of(2025,1,10));
        List<Ansvarsomrader> omrader = new java.util.ArrayList<>();
        omrader.add(bad);
        omrader.add(kj);

        FileHandler.skrivPrikker(persListe);
        FileHandler.skrivScoreboard(sb, persListe);
        FileHandler.skrivOppvask(oppvaskListe);
        FileHandler.skrivSisteVask(omrader);

        List<Person> persListe2 = new java.util.ArrayList<>();
        ScoreBoard sb2 = new ScoreBoard();
        List<Oppvask> oppvaskListe2 = new java.util.ArrayList<>();
        List<Ansvarsomrader> omrader2 = new java.util.ArrayList<>();
        omrader2.add(new Badet(null, LocalDate.now()));
        omrader2.add(new Kjokken(null, LocalDate.now()));

        FileHandler.lesPrikker(persListe2);
        FileHandler.lesScoreboard(sb2, persListe2);
        FileHandler.lesOppvask(oppvaskListe2, persListe2);
        FileHandler.lesSisteVask(omrader2, persListe2);

        
        Person found = finnPerson("FilTest", persListe2);
        assertNotNull(found, "Bør finne FilTest i persListe2");
        assertEquals(2, found.getAntallPrikker(),
            "FilTest bør ha 2 prikker lastet fra fil");


    
        assertEquals(1, oppvaskListe2.size());
        Oppvask op2 = oppvaskListe2.get(0);
        assertEquals(1, op2.getAntallOppvask());
        assertEquals(found, op2.getPerson());

        
        Ansvarsomrader a1 = omrader2.get(0);
        Person ansvarlig1 = a1.getAnsvarlig();
        assertNotNull(ansvarlig1);
        assertEquals("FilTest", ansvarlig1.getName());

        
    }

    @Test
    void testMultipleActionsScenario() {
        scoreboard.leggTilPoeng(person, 1);
        badet.registerVask(LocalDate.now().plusDays(15)); 
        scoreboard.leggTilPoeng(person, 5);

        assertEquals(6, scoreboard.getPoeng(person));

    }

    
    private Person finnPerson(String navn, List<Person> pliste) {
        for (Person p : pliste) {
            if (p.getName().equalsIgnoreCase(navn)) {
                return p;
            }
        }
        return null;
    }
}
