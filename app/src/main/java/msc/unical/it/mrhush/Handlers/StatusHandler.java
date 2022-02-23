package msc.unical.it.mrhush.Handlers;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import msc.unical.it.mrhush.Logic.Habit;
import msc.unical.it.mrhush.Logic.MrHushService;
import msc.unical.it.mrhush.Logic.Mute;
import msc.unical.it.mrhush.Logic.Sound;
import msc.unical.it.mrhush.Logic.Vibrate;
import msc.unical.it.mrhush.Logic.WiFiFilter;

public class StatusHandler {
    public static String filePath;
    private static StatusHandler instance = null;
    AudioManager audioManager;
    WifiManager wifiManager;
    ArrayList<Status> listOfStatus = new ArrayList<>();

    private StatusHandler() {
        audioManager = (AudioManager) MrHushService.c.getSystemService(Context.AUDIO_SERVICE);
        wifiManager = (WifiManager) MrHushService.c.getSystemService(Context.WIFI_SERVICE);


    }

    public static StatusHandler getInstance() {
        if (instance == null)
            instance = new StatusHandler();

        return instance;

    }

    public ArrayList<Status> getListOfStatus() {
        return listOfStatus;
    }


    public void setListOfStatus(ArrayList<Status> listOfStatus) {
        this.listOfStatus = listOfStatus;
    }

    public void save() {

        Status s = new Status();

        s.setAudioState(audioManager.getRingerMode());

        WifiInfo i = wifiManager.getConnectionInfo();
        if (wifiManager.getConfiguredNetworks() != null)
            for (WifiConfiguration wifiConfiguration : wifiManager.getConfiguredNetworks()) {
                if (wifiConfiguration.SSID.equals(i.getSSID()))
                    s.setTargetWifi(i.getSSID());
            }
        if (s.getTargetWifi() != null) {

            WiFiFilter f = new WiFiFilter();
            f.setTargetNetwork(s.getTargetWifi());
            if (HabitsHandler.getInstance().checkFilterExists(f) == null)
                for (Status status : listOfStatus) {
                    if (status.getAudioState() == s.getAudioState() && status.getTargetWifi().equals(s.getTargetWifi())) {
                        status.addOneOccurrence();
                        if (status.getEventCounter() == 5) {
                            Intent notify = new Intent("NOTIFY");
                            Habit h = new Habit();

                            h.setW(new WiFiFilter());
                            h.getW().setTargetNetwork(status.getTargetWifi());

                            switch (status.getAudioState()) {
                                case 0:
                                    h.setKindOfAction("Mute");
                                    h.setAction(new Mute());
                                    h.setMyHabitName("Mute with " + h.getW().getTargetNetwork());
                                    break;
                                case 1:
                                    h.setKindOfAction("Vibrate");
                                    h.setMyHabitName("Vibrate with " + h.getW().getTargetNetwork());
                                    h.setAction(new Vibrate());
                                    break;
                                case 2:
                                    h.setKindOfAction("Sound");
                                    h.setMyHabitName("Sound with " + h.getW().getTargetNetwork());
                                    h.setAction(new Sound());
                                    break;
                            }

                            HabitsHandler.getInstance().setTmp(h);
                            HabitsHandler.getInstance().getTmp().setAutoCreated(true);
                            MrHushService.c.sendBroadcast(notify);
                            status.setEventCounter(0);
                        }
                        saveData();
                        return;

                    }
                }
            listOfStatus.add(s);
            saveData();

        }
    }

    public void saveData() {


        ArrayList<String> jsonList = new ArrayList<>();


        try {
            FileWriter f = new FileWriter(filePath + new PrefManager(MrHushService.c).isAuthenticatedUser());


            Gson g = new Gson();
            for (Status Status : listOfStatus) {
                jsonList.add(Status.toJsonString());
            }
            f.write(g.toJson(jsonList, ArrayList.class));

            f.flush();
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void readData() {
        File f = new File(filePath + new PrefManager(MrHushService.c).isAuthenticatedUser());
        try {
            if (f.length() != 0) {
                Gson g = new Gson();

                listOfStatus.clear();

                FileInputStream fis = new FileInputStream(filePath + new PrefManager(MrHushService.c).isAuthenticatedUser());
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                String line = br.readLine();

                Type k = new TypeToken<ArrayList>() {
                }.getType();

                ArrayList<String> myStrings = g.fromJson(line, k);

                Status status = new Status();

                for (String myString : myStrings) {
                    listOfStatus.add(status.fromJsonToStatus(myString));
                }


            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Status ofStatus : listOfStatus) {

        }

    }
}



