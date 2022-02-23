package msc.unical.it.mrhush.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import msc.unical.it.mrhush.Handlers.HabitsHandler;
import msc.unical.it.mrhush.Logic.BluetoothFilter;
import msc.unical.it.mrhush.Logic.Habit;
import msc.unical.it.mrhush.Logic.Mute;
import msc.unical.it.mrhush.Logic.Sound;
import msc.unical.it.mrhush.Logic.Vibrate;
import msc.unical.it.mrhush.Logic.WiFiFilter;
import msc.unical.it.mrhush.R;

public class HabitCreatorActivity extends AppCompatActivity {

    private boolean isActionSelected = false;
    private EditText editText;
    private ArrayList<String> availableFilters = new ArrayList<>();
    private ArrayList<String> availableActions = new ArrayList<>();
    private ArrayList<Integer> priority = new ArrayList<>();
    private Spinner spinnerFilter;
    private Spinner spinnerAction;
    private Spinner spinnerPriority;
    private ListView selectedFilter;
    private TextView showSelected;
    private TextView savedName;
    private int habitPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_habit_creator);
        populateFilters();
        populateActions();

        int maxPriority;

        if (HabitsHandler.getInstance().getTmp().isModify())
            maxPriority = HabitsHandler.getInstance().getInUseHabits().size() - 1;
        else
            maxPriority = HabitsHandler.getInstance().getInUseHabits().size();

        for (int i = 0; i <= maxPriority; i++)
            priority.add(i + 1);

        habitPriority = 0;
    }

    @Override
    protected void onStart() {
        super.onStart();

        editText = (EditText) findViewById(R.id.habitName);
        savedName = (TextView) findViewById(R.id.savedHabitName);

        if (HabitsHandler.getInstance().getTmp().isModify() || HabitsHandler.getInstance().getTmp().isAutoCreated()) {
            editText.setVisibility(View.GONE);
            savedName.setVisibility(View.VISIBLE);
            savedName.setText(HabitsHandler.getInstance().getTmp().getMyHabitName());
        }

        selectedFilter = (ListView) findViewById(R.id.selectedFilter);

        showSelected = (TextView) findViewById(R.id.listOfFilters);

        ArrayAdapter<String> selectedFiltersAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getFiltersList());

        selectedFilter.setAdapter(selectedFiltersAdapter);

        Button saveButton = (Button) findViewById(R.id.confirmHabitButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HabitsHandler.getInstance().getTmp().isModify()) {
                    if (isActionSelected && habitPriority != 0) {

                        updateHabit(habitPriority);
                        Intent goBackToHomeActivity =
                                new Intent(HabitCreatorActivity.this, HomeActivity.class);
                        startActivity(goBackToHomeActivity);
                    }

                } else if (setHabitName() && isActionSelected && habitPriority != 0) {
                    createHabit(habitPriority);

                    HabitsHandler.getInstance().saveData();
                    HabitsHandler.getInstance().clearTmp();
                    Intent goBackToHomeActivity = new Intent(HabitCreatorActivity.this, HomeActivity.class);
                    startActivity(goBackToHomeActivity);

                } else if (!isActionSelected)
                    Toast.makeText(getApplicationContext(), "Select an Action, please", Toast.LENGTH_LONG).show();
                else if (habitPriority == 0)
                    Toast.makeText(getApplicationContext(), "Select Priority, please", Toast.LENGTH_LONG).show();
            }
        });

        spinnerFilter = (Spinner) findViewById(R.id.spinnerFilter);
        spinnerAction = (Spinner) findViewById(R.id.spinnerAction);
        spinnerPriority = (Spinner) findViewById(R.id.spinnerPriority);

        ArrayAdapter<String> filterArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, availableFilters);
        ArrayAdapter<String> actionArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, availableActions);
        ArrayAdapter<Integer> priorityArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, priority);

        spinnerFilter.setAdapter(filterArrayAdapter);
        spinnerAction.setAdapter(actionArrayAdapter);
        spinnerPriority.setAdapter(priorityArrayAdapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switchToCreateFilter(spinnerFilter.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                setHabitAction(spinnerAction.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                habitPriority = (Integer) spinnerPriority.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (!(HabitsHandler.getInstance().getTmp().getMyHabitName().equals("")))
            editText.setText(HabitsHandler.getInstance().getTmp().getMyHabitName());

        if (HabitsHandler.getInstance().getTmp().getAction() instanceof Sound)
            spinnerAction.setSelection(actionArrayAdapter.getPosition("Sound"));
        else if (HabitsHandler.getInstance().getTmp().getAction() instanceof Mute)
            spinnerAction.setSelection(actionArrayAdapter.getPosition(("Mute")));
        else if (HabitsHandler.getInstance().getTmp().getAction() instanceof Vibrate)
            spinnerAction.setSelection(actionArrayAdapter.getPosition("Vibrate"));
        else
            spinnerAction.setSelection(0);
    }

    private void switchToCreateFilter(String selected) {

        HabitsHandler.getInstance().getTmp().setMyHabitName(editText.getText().toString());
        Intent intent;

        switch (selected) {
            case "Bluetooth":
                if (!new BluetoothFilter().checkState())
                    Toast.makeText(getApplicationContext(), "Turn on Bluetooth, please", Toast.LENGTH_LONG).show();

                else {

                    intent = new Intent(HabitCreatorActivity.this, BluetoothSettingActivity.class);
                    startActivity(intent);
                }

                spinnerFilter.setSelection(0);
                break;

            case "Wifi":

                if (!new WiFiFilter().checkState())
                    Toast.makeText(getApplicationContext(), "Turn on WiFi, please", Toast.LENGTH_LONG).show();

                else {

                    intent = new Intent(HabitCreatorActivity.this, WiFiSettingActivity.class);
                    startActivity(intent);
                }

                spinnerFilter.setSelection(0);
                break;

            case "Date":

                intent = new Intent(HabitCreatorActivity.this, DateSettingActivity.class);
                startActivity(intent);
                break;

            case "Location":
                intent = new Intent(HabitCreatorActivity.this, LocationActivity.class);
                startActivity(intent);
                break;

/*            case "Time":
                intent = new Intent(HabitCreatorActivity.this, TimeSettingActivity.class);
                startActivity(intent);
                break;
*/
        }

    }

    private void populateActions() {

        availableActions.add("Choose an action");
        availableActions.add("Mute");
        availableActions.add("Sound");
        availableActions.add("Vibrate");
    }

    private void populateFilters() {

        availableFilters.add("Choose a filter");
        availableFilters.add("Bluetooth");
        availableFilters.add("Date");
        availableFilters.add("Location");
//        availableFilters.add("Time");
        availableFilters.add("Wifi");
    }

    private void createHabit(int priority) {

        HabitsHandler.getInstance().addHabit(priority);
    }

    private void updateHabit(int priority) {

        for (Habit habit : HabitsHandler.getInstance().getInUseHabits())

            if (habit.getMyHabitName().equals(HabitsHandler.getInstance().getTmp().getMyHabitName())) {
                HabitsHandler.getInstance().getTmp().setModify(false);
                HabitsHandler.getInstance().getInUseHabits().remove(habit);
                HabitsHandler.getInstance().addHabit(priority);
                HabitsHandler.getInstance().clearTmp();
                HabitsHandler.getInstance().saveData();
            }

    }

    private void setHabitAction(String s) {
        switch (s) {
            case "Mute":
                HabitsHandler.getInstance().getTmp().setKindOfAction("Mute");
                isActionSelected = true;
                break;

            case "Sound":

                HabitsHandler.getInstance().getTmp().setKindOfAction("Sound");
                isActionSelected = true;
                break;

            case "Vibrate":

                HabitsHandler.getInstance().getTmp().setKindOfAction("Vibrate");
                isActionSelected = true;
                break;

            default:
                isActionSelected = false;
                break;
        }
    }

    private boolean setHabitName() {

        String newHabitName = ((EditText) findViewById(R.id.habitName)).getText().toString();
        Boolean alreadyUsedName = false;

        if (!newHabitName.equals("")) {
            for (Habit habit : HabitsHandler.getInstance().getInUseHabits()) {
                if (habit.getMyHabitName().equals(newHabitName)) {
                    alreadyUsedName = true;
                    break;
                }
            }

            for (Habit habit : HabitsHandler.getInstance().getDismissedHabits()) {
                if (habit.getMyHabitName().equals(newHabitName) || alreadyUsedName) {
                    alreadyUsedName = true;
                    break;
                }
            }

            if (alreadyUsedName) {

                Toast.makeText(this, "Name already exists", Toast.LENGTH_LONG).show();
                return false;
            } else {

                HabitsHandler.getInstance().getTmp().setMyHabitName(newHabitName);
                return true;
            }

        } else {
            Toast.makeText(this, "Choose a name, please", Toast.LENGTH_LONG).show();
            return false;
        }

    }

    private ArrayList<String> getFiltersList() {
        ArrayList<String> filters = new ArrayList<>();

        if (HabitsHandler.getInstance().getTmp().getB().isActivatedFilter())
            filters.add(HabitsHandler.getInstance().getTmp().getB().toString());
        if (HabitsHandler.getInstance().getTmp().getD().isActivatedFilter())
            filters.add(HabitsHandler.getInstance().getTmp().getD().toString());
        if (HabitsHandler.getInstance().getTmp().getW().isActivatedFilter())
            filters.add(HabitsHandler.getInstance().getTmp().getW().toString());
        if (HabitsHandler.getInstance().getTmp().getL().isActivatedFilter())
            filters.add(HabitsHandler.getInstance().getTmp().getL().toString());

        if (!filters.isEmpty())
            showSelected.setVisibility(View.VISIBLE);


        return filters;
    }
}