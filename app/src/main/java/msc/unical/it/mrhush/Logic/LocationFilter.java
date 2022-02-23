package msc.unical.it.mrhush.Logic;

import android.location.Location;
import com.google.android.gms.maps.model.LatLng;
import java.io.Serializable;

public class LocationFilter extends Filter implements Serializable {

    private String targetLocation = "";
    private LatLng targetLatLon;

    public LocationFilter() {
        super();
        targetLatLon = new LatLng(0, 0);
    }

    @Override
    public String toString() {

        if (isActivatedFilter())
            return "Location: " + targetLocation;
        else
            return "";
    }

    @Override
    public boolean checkStartCondition(Object o) {

        Location l = new Location("");
        l.setLatitude(targetLatLon.latitude);
        l.setLongitude(targetLatLon.longitude);

        Location act = (Location) o;

        if (act.distanceTo(l) <= 25)
            return true;
        else
            return false;

    }

    public LatLng getTargetLatLon() {
        return targetLatLon;
    }

    public void setTargetLatLon(LatLng targetLatLon) {
        this.targetLatLon = targetLatLon;
    }

    public String getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(String targetLocation) {
        this.targetLocation = targetLocation;
    }
}