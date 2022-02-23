package msc.unical.it.mrhush.Logic;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.io.File;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;
import msc.unical.it.mrhush.Activities.HabitCreatorActivity;
import msc.unical.it.mrhush.Handlers.HabitsHandler;
import msc.unical.it.mrhush.Handlers.StatusHandler;
import msc.unical.it.mrhush.R;

public class MrHushService extends Service {

    public static Context c;
    public static AlarmManager alarmManager;
    BroadcastReceiver receiver;
    NotificationManager notificationManager;
    Location actualLocation;
    Intent intent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void createDir(Context context) {
        String filename1 = "userHabits";
        String filename2 = "userdata";
        File root = new File(Environment.getExternalStorageDirectory(), "mrHushData");

        if (!root.exists())
            root.mkdirs();

        File gpxfile = new File(root, filename1);
        HabitsHandler.filePath = gpxfile.getPath();
        HabitsHandler.getInstance().readData();


        File gpxfile2 = new File(root, filename2);
        StatusHandler.filePath = gpxfile2.getPath();
        StatusHandler.getInstance().readData();

    }

    @Override
    public void onCreate() {
        c = getApplicationContext();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        super.onCreate();
        createDir(getApplicationContext());

        intent = new Intent();

        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle extras = intent.getExtras();
                if (intent.getAction().equals("android.net.wifi.STATE_CHANGE")) {
                    NetworkInfo ni = (NetworkInfo) extras.get("networkInfo");

                    if (ni.getState().equals(NetworkInfo.State.CONNECTED)) {

                        WifiInfo i = (WifiInfo) extras.get("wifiInfo");
                        HabitsHandler.getInstance().checkBroadcastCondition("WIFICONNECTED");

                    } else if (ni.getState().equals(NetworkInfo.State.DISCONNECTED)) {

                        HabitsHandler.getInstance().checkBroadcastCondition("WIFIDISCONNECTED");
                    }
                } else if (intent.getAction().equals("android.bluetooth.device.action.ACL_CONNECTED")) {

                    BluetoothDevice bd = (BluetoothDevice) extras.get("android.bluetooth.device.extra.DEVICE");
                    HabitsHandler.getInstance().checkBroadcastCondition("BLUETOOTHCONNECTED");

                } else if (intent.getAction().equals("android.bluetooth.device.action.ACL_DISCONNECTED")) {

                    HabitsHandler.getInstance().checkBroadcastCondition("BLUETOOTHDISCONNECTED");

                } else if (intent.getAction().equals("DATESTART")) {

                    HabitsHandler.getInstance().checkBroadcastCondition("DATESTART");

                } else if (intent.getAction().equals("DATEEND")) {

                    HabitsHandler.getInstance().checkBroadcastCondition("DATEEND");

                } else if (intent.getAction().equals("android.media.RINGER_MODE_CHANGED")) {

                    StatusHandler.getInstance().save();

                } else if (intent.getAction().equals("NOTIFY")) {

                    showNotification(c);
                }
            }
        };

        IntentFilter f = new IntentFilter();
        f.addAction("android.net.wifi.STATE_CHANGE");
        f.addAction("android.bluetooth.device.action.ACL_CONNECTED");
        f.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        f.addAction("android.intent.action.DATE_CHANGED");
        f.addAction("DATESTART");
        f.addAction("DATEEND");
        f.addAction("android.media.RINGER_MODE_CHANGED");
        f.addAction("NOTIFY");

        registerReceiver(receiver, f);

        SmartLocation.with(c).location().config((new LocationParams.Builder())
                .setAccuracy(LocationAccuracy.MEDIUM).setDistance(10.0F).setInterval(2500L).build()).start(new OnLocationUpdatedListener() {
            @Override
            public void onLocationUpdated(Location location) {

                actualLocation = location;

                HabitsHandler.getInstance().checkBroadcastCondition(location);
            }
        });


    }

    private void showNotification(Context c) {
        int id = 0;

        Intent intent = new Intent(c, HabitCreatorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(c, id, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(c)
                .setSmallIcon(R.mipmap.ic_mrh)
                .setContentTitle("Mr. Hush")
                .setContentText("I've got a new Habit for you!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{500, 1000, 1000})
                .setLights(0xffffffff, 1000, 1000)
                .setAutoCancel(true);

        notificationManager = (NotificationManager) getSystemService(c.NOTIFICATION_SERVICE);
        notificationManager.notify(id++, mBuilder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
        SmartLocation.with(c).location().stop();
    }

}