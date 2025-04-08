package vaskeapp;

import java.time.LocalDate;

public class Prikk {
    private LocalDate dato;
    private String årsak;
    
    public Prikk(String årsak) {
        this.dato = LocalDate.now();
        this.årsak = årsak;
    }
    
    public boolean erUtløpt() {
        LocalDate treMndEtter = dato.plusMonths(3);
        return LocalDate.now().isAfter(treMndEtter);
    }
    
}
