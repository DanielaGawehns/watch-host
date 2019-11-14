package org.openjfx;

import java.util.ArrayList;
import java.util.List;

class SmartwatchList {

    /**
     * List of smartwatches connected {@link Smartwatch}
     */
    private static List<Smartwatch> watches = new ArrayList<>();

    void add(Smartwatch watch){ watches.add(watch); }

    Smartwatch get(int i){ return watches.get(i); }

    Smartwatch getFromID(int ID){
        for(Smartwatch watch : watches){
            if(watch.getWatchID() == ID){
               // System.out.println("Watch's data set has size " + watch.getSensorData("HRM").size());
                return watch;
            }
        }
        return null;
    }

    int size(){ return watches.size(); }

    void remove(int ID){
        for(int i = 0; i < size(); i++){
            if(get(i).getWatchID() == ID){
                watches.remove(i);
                return;
            }
        }
    }
}
