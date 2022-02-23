package msc.unical.it.mrhush.Logic;

import java.io.Serializable;

/**
 * Created by ADAMONE on 26/03/2018.
 */

public class Filter implements Serializable {

    transient boolean condition = false;
    private boolean activatedFilter = false;

    public Filter() {
    }

    public boolean isActivatedFilter() {
        return activatedFilter;
    }

    public void setActivatedFilter() {
        this.activatedFilter = true;
    }

    public String toString() {
        return super.toString();
    }

    public boolean checkStartCondition(Object o) {
        return false;
    }

    public boolean checkEndCondition() {
        return false;
    }
}
