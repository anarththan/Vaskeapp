package vaskeapp;

import java.time.LocalDate;

public abstract class AbstractAnsvarsOmrade implements Ansvarsomrader{
    protected Person ansvarlig;
    protected LocalDate sisteVask;
    protected LocalDate fristForNesteVask;

    public AbstractAnsvarsOmrade(Person ansvarlig, LocalDate sisteVask){
        this.ansvarlig = ansvarlig;
        this.sisteVask = sisteVask;
        this.fristForNesteVask = sisteVask.plusWeeks(2);
    }
    @Override
    public Person getAnsvarlig(){
        return ansvarlig;
        }

    @Override    
    public void setAnsvarlig(Person person){
        this.ansvarlig = person;
    }
    @Override
    public LocalDate getSisteVask(){
        return sisteVask;
    }
    @Override
    public LocalDate getFristForNesteVask(){
        return fristForNesteVask;
    }
    @Override
    public void registerVask(LocalDate vaskDato){
        this.sisteVask = vaskDato;
        this.fristForNesteVask = sisteVask.plusWeeks(2);
    }

    protected boolean erFristBrutt(){
        return LocalDate.now().isAfter(fristForNesteVask);
    }

}
