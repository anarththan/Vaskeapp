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
        return navn != null && !navn.isEmpty() && navn.matches("[\\p{L}\\s]+");
    }

    public void addPrikker(String årsak){
        prikker.add(new Prikk(årsak));
        int prikkCount = getAntallPrikker();
        if (prikkCount > 5) {
            System.out.println(navn + " har fått over 5 prikker -> 500 kr bot!");
        }
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
    public void removeExpiredPrikker() {
        prikker.removeIf(Prikk::erUtløpt);
    }    

}
