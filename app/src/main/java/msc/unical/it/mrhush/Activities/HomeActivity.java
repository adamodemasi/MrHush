package msc.unical.it.mrhush.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import msc.unical.it.mrhush.Handlers.HabitsHandler;
import msc.unical.it.mrhush.Logic.Habit;
import msc.unical.it.mrhush.R;

public class HomeActivity extends AppCompatActivity {

    private ImageView bt, date, loc, time, wifi, mute, sound, vibrate;
    private TextView btDet, dateDet, locDet, timeDet, wifiDet, muteDet, soundDet, vibrateDet, habName;
    private ImageButton close;

    @Override
    protected void onResume() {
        super.onResume();
        HabitsHandler.getInstance().clearTmp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        HabitsHandler.getInstance().readData();
        HabitsHandler.getInstance().clearTmp();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ListView activated = null;
        ListView deactivated = null;

        ArrayAdapter<Habit> activatedAdapter = null;
        ArrayAdapter<Habit> deactivatedAdapter = null;

        int idActivated = R.id.active_habits_list;
        int idDeactivated = R.id.nonactive_habits_list;

        List<Habit> activatedHabits = HabitsHandler.getInstance().getInUseHabits();
        List<Habit> deactivatedHabits = HabitsHandler.getInstance().getDismissedHabits();

        show(activated, activatedAdapter, idActivated, activatedHabits, "Deactivate");
        show(deactivated, deactivatedAdapter, idDeactivated, deactivatedHabits, "Activate");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newHabit = new Intent(HomeActivity.this, HabitCreatorActivity.class);
                startActivity(newHabit);
            }
        });

    }

    public void show(ListView listView, ArrayAdapter<Habit> arrayAdapter, int itemId,
                     final List<Habit> listOfHabits, final String button) {

        listView = (ListView) findViewById(itemId);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listOfHabits);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final Habit currentHabit = (Habit) parent.getItemAtPosition(position);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);

                LayoutInflater inflater = HomeActivity.this.getLayoutInflater();

                alertDialogBuilder.setView(inflater.inflate(R.layout.activity_custom_dialog_view, null));

                alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        createAndShowPopup(currentHabit, button);
                    }
                });

                if (button.equals("Deactivate"))
                    alertDialogBuilder.setNegativeButton("Modify", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            HabitsHandler.getInstance().setTmp(currentHabit);
                            HabitsHandler.getInstance().getTmp().setModify(true);
                            Intent modifyHabit = new Intent(HomeActivity.this, HabitCreatorActivity.class);
                            startActivity(modifyHabit);
                        }
                    });

                alertDialogBuilder.setNeutralButton(button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (button.equals("Activate")) {
                            String h = HabitsHandler.getInstance().checkOp(currentHabit);
                            if (h == null) {
                                HabitsHandler.getInstance().listSwitch(currentHabit, button);
                            } else
                                Toast.makeText(getApplicationContext(), "This Habit creates" +
                                        " conflicts with following in use habits: \n" +
                                        h + "\nPlease deactivate them before performing this operation",
                                        Toast.LENGTH_LONG).show();
                        } else {
                            HabitsHandler.getInstance().listSwitch(currentHabit, button);
                        }
                        recreate();
                    }
                });

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                close = (ImageButton) alertDialog.findViewById(R.id.imageB_close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                bt = (ImageView) alertDialog.findViewById(R.id.bluetooth_icon);
                date = (ImageView) alertDialog.findViewById(R.id.date_icon);
                loc = (ImageView) alertDialog.findViewById(R.id.location_icon);
              //time = (ImageView) alertDialog.findViewById(R.id.time_icon);
                wifi = (ImageView) alertDialog.findViewById(R.id.wifi_icon);
                mute = (ImageView) alertDialog.findViewById(R.id.mute_icon);
                sound = (ImageView) alertDialog.findViewById(R.id.sound_icon);
                vibrate = (ImageView) alertDialog.findViewById(R.id.vibrate_icon);

                btDet = (TextView) alertDialog.findViewById(R.id.btDetail);
                dateDet = (TextView) alertDialog.findViewById(R.id.dateDetail);
                locDet = (TextView) alertDialog.findViewById(R.id.locDetail);
              //timeDet = (TextView) alertDialog.findViewById(R.id.timeDetail);
                wifiDet = (TextView) alertDialog.findViewById(R.id.wifiDetail);
                muteDet = (TextView) alertDialog.findViewById(R.id.mute_action);
                soundDet = (TextView) alertDialog.findViewById(R.id.sound_action);
                vibrateDet = (TextView) alertDialog.findViewById(R.id.vibrate_action);
                habName = (TextView) alertDialog.findViewById(R.id.habit_name);
                habName.setText(currentHabit.getMyHabitName());

                if (currentHabit.getB().isActivatedFilter()) {
                    bt.setVisibility(View.VISIBLE);
                    btDet.setVisibility(View.VISIBLE);
                    btDet.setText(currentHabit.getB().toString());
                }
                if (currentHabit.getD().isActivatedFilter()) {
                    date.setVisibility(View.VISIBLE);
                    dateDet.setVisibility(View.VISIBLE);
                    dateDet.setText(currentHabit.getD().toString());
                }
                if (currentHabit.getL().isActivatedFilter()) {
                    loc.setVisibility(View.VISIBLE);
                    locDet.setVisibility(View.VISIBLE);
                    locDet.setText(currentHabit.getL().toString());
                }
                if (currentHabit.getW().isActivatedFilter()) {
                    wifi.setVisibility(View.VISIBLE);
                    wifiDet.setVisibility(View.VISIBLE);
                    wifiDet.setText(currentHabit.getW().toString());
                }

                switch (currentHabit.getKindOfAction()) {

                    case "Mute":
                        mute.setVisibility(View.VISIBLE);
                        muteDet.setVisibility(View.VISIBLE);
                        break;

                    case "Sound":
                        sound.setVisibility(View.VISIBLE);
                        soundDet.setVisibility(View.VISIBLE);
                        break;

                    case "Vibrate":
                        vibrate.setVisibility(View.VISIBLE);
                        vibrateDet.setVisibility(View.VISIBLE);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private void createAndShowPopup(final Habit currentHabit, final String a) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Habit deleting...");
        builder.setMessage("You are about to delete current habit. Do you really want to proceed?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (a.equals("Deactivate")) {
                    currentHabit.deactivateHabit();
                    HabitsHandler.getInstance().getInUseHabits().remove(currentHabit);
                } else
                    HabitsHandler.getInstance().getDismissedHabits().remove(currentHabit);

                HabitsHandler.getInstance().saveData();
                recreate();
            }

        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.show();
    }

}