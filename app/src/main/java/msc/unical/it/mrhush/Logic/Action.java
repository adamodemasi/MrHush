package msc.unical.it.mrhush.Logic;

import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import java.io.Serializable;

/**
 * Created by Utente on 28/03/2018.
 */

public class Action implements Serializable {

    String kindOfAction = null;
    transient AudioManager audioManager;
    transient NotificationManager notificationManager;

    public Action() {


    }


    public void setProfile() {
        Log.i("ACTION", "set profile");
        audioManager = (AudioManager) MrHushService.c.getSystemService(Context.AUDIO_SERVICE);
        notificationManager = (NotificationManager) MrHushService.c.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public String toString() {
        return super.toString();
    }

}
