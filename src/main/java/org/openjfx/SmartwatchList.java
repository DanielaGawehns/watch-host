package org.openjfx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Class holding a list of {@link Smartwatch}
 */
public class SmartwatchList extends ArrayList<Smartwatch> {

    public SmartwatchList() {
        super();
    }

    /**
     * Find the index of the watch per its ID.
     * @param id The ID of the watch to find.
     * @return -1 if the watch is not found, it's index in the list othewise.
     */
    private int findWithID(int id) {
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i).getWatchID() == id) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Get from list
     * @param id Watch ID
     */
    public Smartwatch getWithID(int id){
        int index = this.findWithID(id);
        if (index == -1) {
            return null;
        }

        return this.get(index);
    }

    /**
     * Removes the watch with the given ID from the list.
     * @param id The ID of the watch to remove.
     * @throws IllegalArgumentException Throws illegal argument exception when there is no watch found with the given ID.
     */
    public void removeWithID(int id) throws IllegalArgumentException {
        int index = this.findWithID(id);
        if (index == -1) {
            throw new IllegalArgumentException("no watch found with given id");
        }

        this.remove(index);
    }
}
