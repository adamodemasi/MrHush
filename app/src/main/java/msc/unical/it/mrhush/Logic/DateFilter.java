package msc.unical.it.mrhush.Logic;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Koko on 09/04/2018.
 */

public class DateFilter extends Filter implements Serializable {

    private Date targetDateStart;
    private Date targetDateEnd;

    public DateFilter() {

        super();
    }

    public Date getTargetDateStart() {
        return targetDateStart;
    }

    public Date getTargetDateEnd() {
        return targetDateEnd;
    }

    public void setTargetDateEnd(int d, int mo, int y, int h, int m) {

        targetDateEnd = new Date(y, mo, d, h, m);
    }

    public void setTargetDateStart(int d, int mo, int y, int h, int m) {

        targetDateStart = new Date(y, mo, d, h, m);
    }

    @Override
    public String toString() {
        if (isActivatedFilter())
            return "starts: " + targetDateStart.toLocaleString() + "\nends: " + targetDateEnd.toLocaleString();
        else
            return "";
    }

    @Override
    public boolean checkStartCondition(Object o) {

        if (Calendar.getInstance().getTime().compareTo(targetDateStart) >= 0)
            return true;
        else
            return false;
    }

    @Override
    public boolean checkEndCondition() {

        if (Calendar.getInstance().getTime().compareTo(targetDateEnd) >= 0)
            return true;
        else
            return false;
    }

}
