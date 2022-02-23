package msc.unical.it.mrhush.Activities;

import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import msc.unical.it.mrhush.Handlers.HabitsHandler;
import msc.unical.it.mrhush.Logic.Habit;
import msc.unical.it.mrhush.Logic.WiFiFilter;
import msc.unical.it.mrhush.R;

/**
 * Created by ADAMONE on 09/04/2018.
 */

public class WiFiSettingActivity extends AppCompatActivity {

    ListView availableWiFi;
    WiFiFilter filter;
    ArrayAdapter arrayAdapter;

    @Override
    protected void onStart() {
        super.onStart();

        if (HabitsHandler.getInstance().getTmp().getW().isActivatedFilter()) {


            availableWiFi.setItemChecked(arrayAdapter.getPosition(HabitsHandler.getInstance().getTmp().getW().getTargetNetwork()), true);


        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_setting);
        if (!HabitsHandler.getInstance().getTmp().getW().getTargetNetwork().equals("") && !HabitsHandler.getInstance().getTmp().isModify()) {
            filter = HabitsHandler.getInstance().getTmp().getW();
        } else
            filter = new WiFiFilter();


        availableWiFi = (ListView) findViewById(R.id.availableWifi);

        //crea una lista con i NOMI dei WiFi configurati
        List<String> devicesWiFi = new ArrayList<>();
        for (WifiConfiguration dev : filter.getRes()) {
            String name = dev.SSID;
            devicesWiFi.add(name);
        }

        arrayAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, devicesWiFi);

        availableWiFi.setAdapter(arrayAdapter);
        availableWiFi.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        availableWiFi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arrayAdapter, final View view, int pos, long id) {

                filter.setTargetNetwork((String) arrayAdapter.getItemAtPosition(pos));
            }
        });

        Button button = (Button) findViewById(R.id.confirmWifiButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkSaveCondition()) {
                    HabitsHandler.getInstance().getTmp().setW(filter);
                    Intent intent = new Intent(WiFiSettingActivity.this, HabitCreatorActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean checkSaveCondition() {
        if (filter.getTargetNetwork().equals("")) {
            Toast.makeText(getApplicationContext(), "Select a network, please", Toast.LENGTH_LONG).show();
            return false;
        }

        Habit h = HabitsHandler.getInstance().checkFilterExists(filter);
        if (h != null) {
            Toast.makeText(getApplicationContext(), "An Habit with this filter already exists and is in use (" + h.getMyHabitName() + ")", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}