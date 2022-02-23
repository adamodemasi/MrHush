package msc.unical.it.mrhush.Activities;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
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
import msc.unical.it.mrhush.Logic.BluetoothFilter;
import msc.unical.it.mrhush.Logic.Habit;
import msc.unical.it.mrhush.R;

public class BluetoothSettingActivity extends AppCompatActivity {
    ListView availableBT;
    BluetoothFilter filter;
    ArrayAdapter arrayAdapter;

    @Override
    protected void onStart() {
        super.onStart();
        if (HabitsHandler.getInstance().getTmp().getB().isActivatedFilter()) {

            availableBT.setItemChecked(arrayAdapter.getPosition(HabitsHandler.getInstance()
                    .getTmp().getB().getTargetDevice()), true);
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_setting);
        if (!HabitsHandler.getInstance().getTmp().getB().getTargetDevice().equals("")
                && !HabitsHandler.getInstance().getTmp().isModify())
            filter = HabitsHandler.getInstance().getTmp().getB();
        else
            filter = new BluetoothFilter();

        availableBT = (ListView) findViewById(R.id.availableBluetooth);

        List<String> devicesBT = new ArrayList<>();

        for (BluetoothDevice devBT : filter.getRes()) {
            String name = devBT.getName();
            devicesBT.add(name);
        }

        arrayAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, devicesBT);
        availableBT.setAdapter(arrayAdapter);

        availableBT.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        availableBT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arrayAdapter, final View view, int pos, long id) {

                filter.setTargetDevice((String) arrayAdapter.getItemAtPosition(pos));
            }
        });

        Button button = (Button) findViewById(R.id.confirmBluetoothButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkSaveCondition()) {
                    HabitsHandler.getInstance().getTmp().setB(filter);
                    Intent intent = new Intent(BluetoothSettingActivity.this, HabitCreatorActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean checkSaveCondition() {
        if (filter.getTargetDevice().equals("")) {

            Toast.makeText(getApplicationContext(), "Select a target device, please", Toast.LENGTH_LONG).show();

            return false;
        }
        Habit h = HabitsHandler.getInstance().checkFilterExists(filter);
        if (h != null) {
            Toast.makeText(getApplicationContext(), "An Habit with this filter already exists" +
                    " and is in use (" + h.getMyHabitName() + ")", Toast.LENGTH_LONG).show();
            return false;
        } else
            return true;
    }

}
