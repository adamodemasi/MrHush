package msc.unical.it.mrhush.Logic;

import android.app.NotificationManager;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

import java.io.Serializable;

/**
 * Created by Koko on 28/03/2018.
 */

public class Vibrate extends Action implements Serializable {


    public Vibrate() {
        super();
    }


    @Override
    public void setProfile() {

        super.setProfile();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
        }

        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

        audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0);
    }

    @Override
    public String toString() {
        return "Action: Vibrate";
    }
}
