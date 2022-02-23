package msc.unical.it.mrhush.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TabHost;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import msc.unical.it.mrhush.Handlers.HabitsHandler;
import msc.unical.it.mrhush.Logic.DateFilter;
import msc.unical.it.mrhush.Logic.Habit;
import msc.unical.it.mrhush.R;

public class DateSettingActivity extends AppCompatActivity {

    TimePicker startTimePicker;
    TimePicker endTimePicker;
    DatePicker targetDateS;
    DatePicker targetDateE;
    Button saveButton;
    DateFilter filter = new DateFilter();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_setting_filter);
        startTimePicker = (TimePicker) findViewById(R.id.startTimePicker);
        endTimePicker = (TimePicker) findViewById(R.id.endTimePicker);
        targetDateS = (DatePicker) findViewById(R.id.calendarViewS);
        targetDateE = (DatePicker) findViewById(R.id.calendarViewE);

        saveButton = (Button) findViewById(R.id.saveB);

        TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("start");
        spec.setIndicator("Start Date");
        spec.setContent(R.id.tab1);
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("end");
        spec.setContent(R.id.tab2);
        spec.setIndicator("End Date");
        tabHost.addTab(spec);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date d1 = new Date(targetDateS.getYear() - 1900, targetDateS.getMonth(), targetDateS.getDayOfMonth(), startTimePicker.getHour(), startTimePicker.getMinute());
                Date d2 = new Date(targetDateE.getYear() - 1900, targetDateE.getMonth(), targetDateE.getDayOfMonth(), endTimePicker.getHour(), endTimePicker.getMinute());

                if (d1.compareTo(Calendar.getInstance().getTime()) > 0) {
                    if (d2.compareTo(d1) > 0) {
                        filter.setTargetDateStart(targetDateS.getDayOfMonth(), targetDateS.getMonth(), targetDateS.getYear() - 1900, startTimePicker.getHour(), startTimePicker.getMinute());
                        filter.setTargetDateEnd(targetDateE.getDayOfMonth(), targetDateE.getMonth(), targetDateE.getYear() - 1900, endTimePicker.getHour(), endTimePicker.getMinute());
                        Habit h = HabitsHandler.getInstance().checkFilterExists(filter);
                        if (h == null) {
                            HabitsHandler.getInstance().getTmp().setD(filter);
                            Intent intent = new Intent(DateSettingActivity.this, HabitCreatorActivity.class);
                            startActivity(intent);
                        } else
                            Toast.makeText(getApplicationContext(), "An Habit with this filter alteady exists and is in use (" + h.getMyHabitName() + ")", Toast.LENGTH_LONG).show();

                    } else
                        Toast.makeText(getApplicationContext(), "end time can't be previous than start time", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "Start time can't be previous than actual time", Toast.LENGTH_SHORT).show();

            }
        });
        setDefaultTime(startTimePicker);
        setDefaultTime(endTimePicker);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStart() {
        super.onStart();

        if (HabitsHandler.getInstance().getTmp().getD().isActivatedFilter())

        {


            Calendar c = Calendar.getInstance();
            c.setTime(HabitsHandler.getInstance().getTmp().getD().getTargetDateStart());

            targetDateS.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));


            startTimePicker.setHour(c.get(Calendar.HOUR_OF_DAY));
            startTimePicker.setMinute(c.get(Calendar.MINUTE));

            c.setTime(HabitsHandler.getInstance().getTmp().getD().getTargetDateEnd());

            targetDateE.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));


            endTimePicker.setHour(c.get(Calendar.HOUR_OF_DAY));
            endTimePicker.setMinute(c.get(Calendar.MINUTE));


        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setDefaultTime(TimePicker d) {
        d.setIs24HourView(true);
        d.setHour(0);
        d.setMinute(0);
    }
}
