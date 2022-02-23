package msc.unical.it.mrhush.Logic;

import android.app.NotificationManager;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

import java.io.Serializable;

/**
 * Created by Koko on 29/03/2018.
 */

public class Sound extends Action implements Serializable {

    public Sound() {
        super();
    }

    @Override
    public void setProfile() {
        super.setProfile();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
        }
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

        audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION), 0);
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);
    }

    @Override
    public String toString() {
        return "Action: Sound";
    }
}
