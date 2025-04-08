package vaskeapp;

import java.util.HashMap;
import java.util.Map;

public class ScoreBoard {
    private Map<Person, Integer> poengMap = new HashMap<>();
    
    public void leggTilPoeng(Person p, int poeng) {
        int old = poengMap.getOrDefault(p, 0);
        poengMap.put(p, old + poeng);
    }
    
    public int getPoeng(Person p) {
        return poengMap.getOrDefault(p, 0);
    }
}
