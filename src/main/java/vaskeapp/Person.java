package vaskeapp;

import java.util.ArrayList;
import java.util.List;

public class Person {
    private String navn;
    private List<Prikk> prikker;
    
    public Person(String navn){
        if (isValidNavn(navn)) {
            this.navn = navn;
            this.prikker = new ArrayList<>();
        } else {
            throw new IllegalArgumentException("Invalid name provided");
        }
    }

    public String getName(){
        return navn;
    }

    public void setName(String navn){
        if (isValidNavn(navn)) {
            this.navn = navn;
        } else {
            throw new IllegalArgumentException("Invalid name provided");
        }
    }

    private boolean isValidNavn(String navn){
        if (navn == null || navn.isEmpty()){
            return false;
        }
        return navn.matches("[\\p{L}\\s]+");
    }

    public void addPrikker(String årsak){
        prikker.add(new Prikk(årsak));
    }

    public int getAntallPrikker() {
        int i = 0;
        for (Prikk prikk : prikker) {
            if (!prikk.erUtløpt()) {
                i++;
            }
        }
        return i;
    }

}
