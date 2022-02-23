package msc.unical.it.mrhush.Handlers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import msc.unical.it.mrhush.Logic.BluetoothFilter;
import msc.unical.it.mrhush.Logic.DateFilter;
import msc.unical.it.mrhush.Logic.DefaultProfile;
import msc.unical.it.mrhush.Logic.Filter;
import msc.unical.it.mrhush.Logic.Habit;
import msc.unical.it.mrhush.Logic.LocationFilter;
import msc.unical.it.mrhush.Logic.MrHushService;
import msc.unical.it.mrhush.Logic.WiFiFilter;

/**
 * Created by Koko on 23/03/2018.
 **/

public class HabitsHandler implements Serializable {

    public static String filePath = "";
    private static HabitsHandler instance = null;
    private static Location lastKnown = new Location("");
    private ArrayList<Habit> inUseHabits;
    private ArrayList<Habit> dismissedHabits;
    private Habit tmpHabit;
    private boolean anHabitIsActive = false;
    private DefaultProfile defaultProfile;
    private String authUserID = null;

    private HabitsHandler() {

        defaultProfile = new DefaultProfile();
        inUseHabits = new ArrayList<>();
        dismissedHabits = new ArrayList<>();
        tmpHabit = new Habit();
    }

    public static HabitsHandler getInstance() {
        if (instance == null)
            instance = new HabitsHandler();

        return instance;
    }

    public String getAuthUserID() {
        return authUserID;
    }

    public void setAuthUserID(String authUserID) {
        this.authUserID = authUserID;
    }

    public Habit checkFilterExists(Filter f) {

        if (f instanceof BluetoothFilter) {
            for (Habit habit : HabitsHandler.getInstance().getInUseHabits()) {
                if (habit.getB().isActivatedFilter())
                    if (((BluetoothFilter) f).getTargetDevice().equals(habit.getB().getTargetDevice()))
                        return habit;
            }

        } else if (f instanceof WiFiFilter) {
            for (Habit habit : HabitsHandler.getInstance().getInUseHabits()) {
                if (habit.getW().isActivatedFilter())
                    if (((WiFiFilter) f).getTargetNetwork().equals(habit.getW().getTargetNetwork()))
                        return habit;
            }

        } else if (f instanceof DateFilter) {
            for (Habit habit : HabitsHandler.getInstance().getInUseHabits()) {
                if (habit.getD().isActivatedFilter())
                    if (habit.getD().getTargetDateEnd().compareTo(((DateFilter) f).getTargetDateEnd()) >= 0 && ((DateFilter) f).getTargetDateEnd().compareTo(habit.getD().getTargetDateStart()) >= 0)
                        return habit;
                    else if (((DateFilter) f).getTargetDateEnd().compareTo(habit.getD().getTargetDateEnd()) >= 0 && habit.getD().getTargetDateEnd().compareTo(((DateFilter) f).getTargetDateStart()) >= 0)
                        return habit;
            }

        } else if (f instanceof LocationFilter)
            return null;

        return null;
    }

    public DefaultProfile getDefaultProfile() {
        return defaultProfile;
    }

    public void setDefaultProfile() {
        defaultProfile.getActDeviceProfile();
    }

    public String checkOp(Habit h) {
        String res = null;
        if (h.getD().isActivatedFilter())
            if (checkFilterExists(h.getD()) != null)
                res = checkFilterExists(h.getD()).getMyHabitName();
        if (h.getW().isActivatedFilter())
            if (checkFilterExists(h.getW()) != null)
                res = checkFilterExists(h.getW()).getMyHabitName().concat("\n" + res);
        if (h.getB().isActivatedFilter())
            if (checkFilterExists(h.getB()) != null)
                res = checkFilterExists(h.getB()).getMyHabitName().concat("\n" + res);

        return res;
    }

    public void checkBroadcastCondition(Object filter) {

        boolean activategps;
        switch (filter.toString()) {
            case "WIFICONNECTED":
                if (!anHabitIsActive)
                    for (Habit inUseHabit : inUseHabits)  //iterazione sulla lista di habits
                        if (inUseHabit.getW().isActivatedFilter()) //verifico se il filtro che ha innescato il broadcast è attivo
                        {
                            if (inUseHabit.getW().checkStartCondition(null)) { //se il filtro è verificato, verifico gli altri filtri

                                if (inUseHabit.getB().isActivatedFilter())
                                    if (!inUseHabit.getB().checkStartCondition(null)) {
                                        //Se il filtro è attivo ma non si verifica termino l'iterazione,
                                        // altrimenti procedo al successivo

                                        continue;
                                    }
                                if (inUseHabit.getD().isActivatedFilter())
                                    if (!inUseHabit.getD().checkStartCondition(null)) {

                                        continue;
                                    }
                                if (inUseHabit.getL().isActivatedFilter())
                                    if (!inUseHabit.getL().checkStartCondition(lastKnown)) {

                                        continue;
                                    }

                                //Se arrivo a questo punto, tutti i filtri attivi sono verificati
                                // e posso procedere all'attivazione dell'habit
                                inUseHabit.activateHabit();
                                anHabitIsActive = true;
                                break; //smetto di iterare, poiché posso attivare UNA SOLA HABIT ALLA VOLTA SECONDO PRIORITA'.

                            }

                            //l'habit in analisi non ha attivo il filtro che ha innescato il broadcast,
                            // quindi procedo all'iterazione succesiva

                        }
                break;
            case "WIFIDISCONNECTED":
                if (anHabitIsActive)//Se c'è un habit attiva, procedo con il foreach
                    for (Habit inUseHabit : inUseHabits) {
                        if (inUseHabit.isActive())
                            if (inUseHabit.getW().isActivatedFilter()) {
                                anHabitIsActive = false;
                                inUseHabit.deactivateHabit();
                                break;

                                //se l'habit in esame è attiva e il filtro che ha innescato il broadcast è attivo nell'habit,
                                // disattivo l'habit senza ulteriori controlli e termino le iterazioni.
                            }
                    }
                break;
            case "BLUETOOTHCONNECTED": //vedi wificonnected
                if (!anHabitIsActive)
                    for (Habit inUseHabit : inUseHabits)
                        if (inUseHabit.getB().isActivatedFilter()) {
                            if (inUseHabit.getB().checkStartCondition(null)) {
                                if (inUseHabit.getW().isActivatedFilter())
                                    if (!inUseHabit.getW().checkStartCondition(null)) {
                                        continue;
                                    }
                                if (inUseHabit.getD().isActivatedFilter())
                                    if (!inUseHabit.getD().checkStartCondition(null)) {
                                        continue;
                                    }
                                if (inUseHabit.getL().isActivatedFilter())
                                    if (!inUseHabit.getL().checkStartCondition(lastKnown)) {
                                        continue;
                                    }
                                inUseHabit.activateHabit();
                                anHabitIsActive = true;
                                break;

                            }


                        }
                break;


            case "BLUETOOTHDISCONNECTED": //vedi wifidisconnected

                if (anHabitIsActive)
                    for (Habit inUseHabit : inUseHabits) {
                        if (inUseHabit.isActive()) {
                            if (inUseHabit.getB().isActivatedFilter()) {
                                inUseHabit.deactivateHabit();
                                anHabitIsActive = false;
                                break;
                            }
                        }
                    }
                break;

            case "DATESTART":
                for (Habit inUseHabit : inUseHabits) { //iterazione sulle habits

                    if (!anHabitIsActive) { //Se non ci sono habit attive
                        if (inUseHabit.getD().isActivatedFilter()) { //verifico il filtro che ha innescato il broadcast
                            if (inUseHabit.getD().checkStartCondition(null)) { //Se il filtro si verifica controllo gli altri.
                                if (inUseHabit.getW().isActivatedFilter())
                                    if (!inUseHabit.getW().checkStartCondition(null)) {
                                        continue;
                                    }
                                if (inUseHabit.getB().isActivatedFilter())
                                    if (!inUseHabit.getB().checkStartCondition(null)) {
                                         continue;
                                    }
                                if (inUseHabit.getL().isActivatedFilter())
                                    if (!inUseHabit.getL().checkStartCondition(lastKnown)) {
                                        continue;
                                    }
                                inUseHabit.activateHabit();
                                anHabitIsActive = true;
                                break;

                            }
                        }
                    }
                }
                break;

            case "DATEEND":
                for (Habit inUseHabit : inUseHabits) {
                    if (anHabitIsActive)
                        if (inUseHabit.isActive())
                            if (inUseHabit.getD().isActivatedFilter())
                                if (inUseHabit.getD().checkEndCondition()) {
                                    inUseHabit.deactivateHabit();
                                    anHabitIsActive = false;
                                    break;
                                }

                }
                break;


            default: //vedi DATE
                lastKnown = (Location) filter;
                for (Habit inUseHabit : inUseHabits) {
                    if (!anHabitIsActive) {
                        if (inUseHabit.getL().isActivatedFilter())
                            if (inUseHabit.getL().checkStartCondition(filter)) {
                                 if (inUseHabit.getB().isActivatedFilter())
                                    if (!inUseHabit.getB().checkStartCondition(null)) {
                                        continue;
                                    }
                                if (inUseHabit.getD().isActivatedFilter())
                                    if (!inUseHabit.getD().checkStartCondition(null)) {
                                         continue;
                                    }
                                if (inUseHabit.getW().isActivatedFilter())
                                    if (!inUseHabit.getW().checkStartCondition(null)) {
                                        continue;
                                    }
                                inUseHabit.activateHabit();
                                anHabitIsActive = true;
                                break;

                            }

                    } else if (inUseHabit.isActive()) {
                        inUseHabit.deactivateHabit();
                        anHabitIsActive = false;
                        break;
                    }


                }
                break;


        }


        if (!anHabitIsActive) {
            if (defaultProfile.isConf())
                if (!defaultProfile.isActive())
                    defaultProfile.setProfile();
        } else
            defaultProfile.setIsactive();

        saveData();
    }

    public Habit getTmp() {
        return tmpHabit;
    }

    public void setTmp(Habit tmpHabit) {
        this.tmpHabit = tmpHabit;
    }

    public void clearTmp() {
        tmpHabit = new Habit();

    }

    public List<Habit> getInUseHabits() {
        return inUseHabits;
    }

    public void setInUseHabits(ArrayList<Habit> inUseHabits) {
        this.inUseHabits = inUseHabits;
    }

    public List<Habit> getDismissedHabits() {
        return dismissedHabits;
    }

    public void setDismissedHabits(ArrayList<Habit> dismissedHabits) {
        this.dismissedHabits = dismissedHabits;
    }

    public void listSwitch(Habit h, String activated) {

        if (activated.equals("Deactivate")) {

            if (h.isActive()) {
                anHabitIsActive = false;
                h.deactivateHabit();
            }

            h.setInUse(false);
            dismissedHabits.add(h);
            inUseHabits.remove(h);

        } else {

            h.setInUse(true);
            inUseHabits.add(h);
            dismissedHabits.remove(h);
        }

        saveData();
    }

    public void saveData() {
        try {

            ArrayList<String> jsonList = new ArrayList<>();


            FileWriter f = new FileWriter(filePath + new PrefManager(MrHushService.c).isAuthenticatedUser());

            Gson g = new Gson();

            for (Habit activatedHabit : inUseHabits) {
                jsonList.add(activatedHabit.toGsonString());

            }

            for (Habit deactivatedHabit : dismissedHabits) {
                jsonList.add(deactivatedHabit.toGsonString());

            }

            jsonList.add(g.toJson(defaultProfile, DefaultProfile.class));
            jsonList.add(g.toJson(anHabitIsActive, Boolean.class));

            f.write(g.toJson(jsonList, ArrayList.class));


            f.flush();
            f.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readData() {
        File f = new File(filePath + new PrefManager(MrHushService.c).isAuthenticatedUser());
        if (f.length() != 0) {
            try {

                inUseHabits = new ArrayList<>();
                dismissedHabits = new ArrayList<>();

                Gson g = new Gson();

                FileInputStream fis = new FileInputStream(filePath + new PrefManager(MrHushService.c).isAuthenticatedUser());
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                String line = br.readLine();

                Type k = new TypeToken<ArrayList>() {
                }.getType();
                Type defp = new TypeToken<DefaultProfile>() {
                }.getType();


                ArrayList<String> myStrings = g.fromJson(line, k);
                Habit h = new Habit();

                for (int i = 0; i < myStrings.size(); i++) {
                    if (i == myStrings.size() - 2) {
                        defaultProfile = g.fromJson(myStrings.get(i), defp);
                    } else if (i == myStrings.size() - 1)
                        anHabitIsActive = g.fromJson(myStrings.get(i), Boolean.class);
                    else {
                        h = h.toHabitFromString(myStrings.get(i));

                        if (h.isInUse()) {
                            inUseHabits.add(h);

                        } else {
                            dismissedHabits.add(h);

                        }
                    }

                }

                fis.close();
                isr.close();
                br.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addHabit(int priority) {
        Intent i1 = new Intent("DATESTART");
        Intent i2 = new Intent("DATEEND");
        if (tmpHabit.getD().isActivatedFilter()) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MrHushService.c, 0, i1, 0);
            MrHushService.alarmManager.set(AlarmManager.RTC_WAKEUP, tmpHabit.getD().getTargetDateStart().getTime(), pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(MrHushService.c, 0, i2, 0);
            MrHushService.alarmManager.set(AlarmManager.RTC_WAKEUP, tmpHabit.getD().getTargetDateEnd().getTime(), pendingIntent);

        }

        inUseHabits.add(priority - 1, tmpHabit);

    }

}