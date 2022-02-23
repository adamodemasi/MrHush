package msc.unical.it.mrhush.Handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Status {

    int audioState;
    String targetWifi;
    int eventCounter = 1;
    int lastcheck = 1;

    public Status() {

        targetWifi = null;
    }

    public int getLastcheck() {
        return lastcheck;
    }

    public void setLastcheck(int lastcheck) {
        this.lastcheck = lastcheck;
    }

    public int getEventCounter() {
        return eventCounter;
    }

    public void setEventCounter(int eventCounter) {
        this.eventCounter = eventCounter;
    }

    public void addOneOccurrence() {
        this.eventCounter++;
    }

    public int getAudioState() {
        return audioState;
    }

    public void setAudioState(int audioState) {
        this.audioState = audioState;
    }


    public String getTargetWifi() {
        return targetWifi;
    }

    public void setTargetWifi(String targetWifi) {
        this.targetWifi = targetWifi;
    }

    public String toJsonString() {

        Gson g = new Gson();

        ArrayList<String> status = new ArrayList<>();

        status.add(g.toJson(audioState, int.class));
        status.add(g.toJson(targetWifi, String.class));
        status.add(g.toJson(eventCounter, int.class));
        status.add(g.toJson(lastcheck, int.class));


        return g.toJson(status, ArrayList.class);

    }

    public Status fromJsonToStatus(String status) {
        Status st = new Status();
        Type s = new TypeToken<String>() {
        }.getType();
        Type i = new TypeToken<Integer>() {
        }.getType();
        Type array = new TypeToken<ArrayList>() {
        }.getType();

        Gson g = new Gson();
        ArrayList<String> mystatus = (g.fromJson(status, array));
        st.setAudioState((int) g.fromJson(mystatus.get(0), i));
        st.setTargetWifi((String) g.fromJson(mystatus.get(1), s));
        st.setEventCounter((int) g.fromJson(mystatus.get(2), i));
        st.setLastcheck((int) g.fromJson(mystatus.get(3), i));

        return st;

    }
}