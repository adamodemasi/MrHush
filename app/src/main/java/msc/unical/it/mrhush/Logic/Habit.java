package msc.unical.it.mrhush.Logic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

import msc.unical.it.mrhush.Handlers.HabitsHandler;

/**
 * Created by Koko on 23/03/2018.
 */

public class Habit implements Serializable {

    private BluetoothFilter b = new BluetoothFilter();
    private DateFilter d = new DateFilter();
    private WiFiFilter w = new WiFiFilter();
    private LocationFilter l = new LocationFilter();
    private String myHabitName;
    private boolean inUse = true;
    private int activeFilters;
    private boolean active = false;

    public boolean isAutoCreated() {
        return autoCreated;
    }

    public void setAutoCreated(boolean autoCreated) {
        this.autoCreated = autoCreated;
    }

    private boolean autoCreated=false;

    transient private Action action;

    public boolean isModify() {
        return modify;
    }

    public void setModify(boolean modify) {
        this.modify = modify;
    }

    private boolean modify=false;

    public String getKindOfAction() {
        return kindOfAction;
    }

    private String kindOfAction;

    public Habit() {

        myHabitName = "";
        activeFilters = 0;
   }

    public void activateHabit() {

        action.setProfile();
        active = true;
        HabitsHandler.getInstance().saveData();
    }

    public void deactivateHabit() {
        active = false;
        HabitsHandler.getInstance().saveData();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String toGsonString() {

        Gson g = new Gson();

        ArrayList<String> myHabit = new ArrayList<>();

        myHabit.add(g.toJson(b, BluetoothFilter.class));
        myHabit.add(g.toJson(d, DateFilter.class));
        myHabit.add(g.toJson(w, WiFiFilter.class));
        myHabit.add(g.toJson(l, LocationFilter.class));
        myHabit.add(g.toJson(myHabitName, String.class));
        myHabit.add(g.toJson(kindOfAction, String.class));
        myHabit.add(g.toJson(inUse, Boolean.class));

        return g.toJson(myHabit, ArrayList.class);

    }

    public Habit toHabitFromString(String myHabit) {

        Type array = new TypeToken<ArrayList>() { }.getType();
        Type b = new TypeToken<BluetoothFilter>() { }.getType();
        Type d = new TypeToken<DateFilter>() { }.getType();
        Type w = new TypeToken<WiFiFilter>() { }.getType();
        Type l = new TypeToken<LocationFilter>() { }.getType();
        Type hn = new TypeToken<String>() { }.getType();
        Type a = new TypeToken<Action>() { }.getType();
        Type ac = new TypeToken<Boolean>() { }.getType();


        Gson g = new Gson();
        ArrayList<String> habitJsons = (g.fromJson(myHabit, array));

        Habit h = new Habit();

        if (((BluetoothFilter) g.fromJson(habitJsons.get(0), b)).isActivatedFilter())
            h.setB((BluetoothFilter) g.fromJson(habitJsons.get(0), b));
        if (((DateFilter) g.fromJson(habitJsons.get(1), d)).isActivatedFilter())
            h.setD((DateFilter) g.fromJson(habitJsons.get(1), d));
        if (((WiFiFilter) g.fromJson(habitJsons.get(2), w)).isActivatedFilter())
            h.setW((WiFiFilter) g.fromJson(habitJsons.get(2), w));
        if (((LocationFilter) g.fromJson(habitJsons.get(3), l)).isActivatedFilter())
            h.setL((LocationFilter) g.fromJson(habitJsons.get(3), l));

        h.setMyHabitName((String) g.fromJson(habitJsons.get(4), hn));
        h.setKindOfAction((String) g.fromJson(habitJsons.get(5), hn));
        h.setInUse((Boolean) g.fromJson(habitJsons.get(6), ac));


        return h;

    }

    public boolean isInUse() {
        return inUse;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    public BluetoothFilter getB() {
        return b;
    }

    public void setB(BluetoothFilter b) {
        this.b = b;
        b.setActivatedFilter();
        activeFilters++;
    }

    public DateFilter getD() {
        return d;
    }

    public void setD(DateFilter d) {
        this.d = d;
        d.setActivatedFilter();
        activeFilters++;
    }

    public WiFiFilter getW() {
        return w;
    }

    public void setW(WiFiFilter w) {
        this.w = w;
        w.setActivatedFilter();
        activeFilters++;
    }

    public LocationFilter getL() {
        return l;
    }

    public void setL(LocationFilter l) {
        this.l = l;
        l.setActivatedFilter();
        activeFilters++;
    }

    public int getActiveFilters() {
        return activeFilters;
    }

    public void setActiveFilters(int activeFilters) {
        this.activeFilters = activeFilters;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setKindOfAction(String kindOfAction) {
        this.kindOfAction = kindOfAction;
        if (kindOfAction.equals("Mute"))
            setAction(new Mute());
        else if (kindOfAction.equals("Sound"))
            setAction(new Sound());
        else if (kindOfAction.equals("Vibrate"))
            setAction(new Vibrate());

    }

    public String getMyHabitName() {
        return myHabitName;
    }

    public void setMyHabitName(String name) {
        this.myHabitName = name;
    }

    @Override
    public String toString() {
        return myHabitName;
    }

}

