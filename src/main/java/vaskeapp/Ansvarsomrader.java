package vaskeapp;

import java.time.LocalDate;

public interface Ansvarsomrader {
    Person getAnsvarlig();
    void setAnsvarlig(Person person);
    LocalDate getSisteVask();
    LocalDate getFristForNesteVask();
    void registerVask(LocalDate vaskDato);

}
