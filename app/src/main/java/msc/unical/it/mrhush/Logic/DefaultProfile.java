package msc.unical.it.mrhush.Logic;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import msc.unical.it.mrhush.Handlers.HabitsHandler;

public class DefaultProfile extends Action {

    private boolean isConf = false;
    private boolean isActive = false;
    private int ringermode;
    private int streamVolumeRing;
    private int streamVolumeMusic;
    private int streamVolumeNotification;
    private int streamVolumeAlarm;

    public DefaultProfile() { }

    public boolean isActive() {
        return isActive;
    }

    public void setIsactive() {
        isActive = false;
    }

    public boolean isConf() {

        return isConf;
    }

    public void setConf(boolean conf) {
        isConf = conf;
    }

    public void getActDeviceProfile() {


        isConf = true;
        audioManager = (AudioManager) MrHushService.c.getSystemService(Context.AUDIO_SERVICE);

        ringermode = audioManager.getRingerMode();
        streamVolumeRing = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        streamVolumeMusic = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        streamVolumeNotification = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        streamVolumeAlarm = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
    }

    @Override
    public void setProfile() {
        super.setProfile();


        audioManager.setRingerMode(ringermode);

        audioManager.setStreamVolume(AudioManager.STREAM_RING, streamVolumeRing, 0);
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, streamVolumeAlarm, 0);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, streamVolumeMusic, 0);
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, streamVolumeNotification, 0);
        HabitsHandler.getInstance().saveData();
        isActive = true;
    }

    public int getStreamVolumeRing() {
        return streamVolumeRing;
    }

    public void setStreamVolumeRing(int streamVolumeRing) {
        this.streamVolumeRing = streamVolumeRing;
    }

    public int getStreamVolumeMusic() {
        return streamVolumeMusic;
    }

    public void setStreamVolumeMusic(int streamVolumeMusic) {
        this.streamVolumeMusic = streamVolumeMusic;
    }

    public int getStreamVolumeNotification() {
        return streamVolumeNotification;
    }

    public void setStreamVolumeNotification(int streamVolumeNotification) {
        this.streamVolumeNotification = streamVolumeNotification;
    }

    public int getStreamVolumeAlarm() {
        return streamVolumeAlarm;
    }

    public void setStreamVolumeAlarm(int streamVolumeAlarm) {
        this.streamVolumeAlarm = streamVolumeAlarm;
    }

    public void setRingermode(int ringermode) {
        this.ringermode = ringermode;
    }
}
