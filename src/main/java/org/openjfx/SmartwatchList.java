package org.openjfx;

import java.util.ArrayList;
import java.util.List;

/**
 * Class holding a list of {@link Smartwatch}
 */
class SmartwatchList {

    /**
     * List of smartwatches connected {@link Smartwatch}
     */
    private static List<Smartwatch> watches = new ArrayList<>();


    /**
     * Add to the list
     */
    void add(Smartwatch watch){ watches.add(watch); }


    /**
     * Get from the list
     * @param i Index
     */
    Smartwatch get(int i){ return watches.get(i); }


    /**
     * Get from list
     * @param ID Watch ID
     */
    Smartwatch getFromID(int ID){
        for(Smartwatch watch : watches){
            if(watch.getWatchID() == ID){
               // System.out.println("Watch's data set has size " + watch.getSensorData("HRM").size());
                return watch;
            }
        }
        return null;
    }


    /**
     * Size of the list
     * @return Size as Integer
     */
    int size(){ return watches.size(); }


    /**
     * Remove watch from list
     * @param ID Watch ID
     */
    void remove(int ID){
        for(int i = 0; i < size(); i++){
            if(get(i).getWatchID() == ID){
                watches.remove(i);
                return;
            }
        }
    }
}
