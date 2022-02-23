package msc.unical.it.mrhush.Logic;

import android.media.AudioManager;
import java.io.Serializable;

/**
 * Created by Koko on 29/03/2018.
 */

public class Mute extends Action implements Serializable {

    public Mute() {
    }

    @Override
    public void setProfile() {
        super.setProfile();

        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

        audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);

    }

    @Override
    public String toString() {
        return "Action: Mute";
    }
}
