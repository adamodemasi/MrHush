package msc.unical.it.mrhush.Activities;

import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import msc.unical.it.mrhush.Handlers.HabitsHandler;
import msc.unical.it.mrhush.R;

public class SettingsActivity extends AppCompatActivity {

    AudioManager audioManager;

    private Button defaultProfileButton, personalProfileButton;
    private SeekBar ringSeekBar, musicSeekBar, notificationSeekBar, alarmSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        defaultProfileButton = (Button) findViewById(R.id.setDefaultProfile);
        defaultProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HabitsHandler.getInstance().setDefaultProfile();
                Toast.makeText(getApplicationContext(),"Default Profile Saved", Toast.LENGTH_SHORT).show();
            }
        });

        personalProfileButton = (Button) findViewById(R.id.saveProfile);
        personalProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HabitsHandler.getInstance().getDefaultProfile().setRingermode(1);
                HabitsHandler.getInstance().getDefaultProfile().setConf(true);
                HabitsHandler.getInstance().saveData();
                Toast.makeText(getApplicationContext(),"Personal Profile Saved", Toast.LENGTH_SHORT).show();
            }
        });

        ringSeekBar = (SeekBar) findViewById(R.id.seekBarRing);
        ringSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));

        ringSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                HabitsHandler.getInstance().getDefaultProfile().setStreamVolumeRing(progresValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }

        });

        musicSeekBar = (SeekBar) findViewById(R.id.seekBarMusic);
        musicSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                HabitsHandler.getInstance().getDefaultProfile().setStreamVolumeMusic(progresValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }

        });

        notificationSeekBar = (SeekBar) findViewById(R.id.seekBarNotification);
        notificationSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION));
        notificationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                HabitsHandler.getInstance().getDefaultProfile().setStreamVolumeNotification(progresValue);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }

        });

        alarmSeekBar = (SeekBar) findViewById(R.id.seekBarAlarm);
        alarmSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM));
        alarmSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                HabitsHandler.getInstance().getDefaultProfile().setStreamVolumeAlarm(progresValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }

        });

        if(HabitsHandler.getInstance().getDefaultProfile().isConf()){
            ringSeekBar.setProgress(HabitsHandler.getInstance().getDefaultProfile().getStreamVolumeRing());
            musicSeekBar.setProgress(HabitsHandler.getInstance().getDefaultProfile().getStreamVolumeMusic());
            notificationSeekBar.setProgress(HabitsHandler.getInstance().getDefaultProfile().getStreamVolumeNotification());
            alarmSeekBar.setProgress(HabitsHandler.getInstance().getDefaultProfile().getStreamVolumeAlarm());
        }
    }

}
