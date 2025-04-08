package vaskeapp;

import java.time.LocalDate;
import java.util.ArrayList;

public class Oppvask {
    protected Person person;
    protected ArrayList<LocalDate> oppvaskDatoer = new ArrayList<>();

    public Oppvask(Person person){
        this.person = person;
    }

    public void leggTilOppvaskDato(LocalDate dato){
        oppvaskDatoer.add(dato);
    }
    public void leggTilOppvaskDato(){
        oppvaskDatoer.add(LocalDate.now());
    }

    public Person getPerson(){
        return person;
    }

    public ArrayList<LocalDate> getOppvaskdatoer(){
        return oppvaskDatoer;
    }

    public int getAntallOppvask(){
        return oppvaskDatoer.size();
    }

}
