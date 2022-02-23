package msc.unical.it.mrhush.Logic;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by ADAMONE on 26/03/2018.
 */

public class BluetoothFilter extends Filter implements Serializable {

    private String targetDevice = "";
    transient private BluetoothAdapter bluetoothAdapter;
    transient private Set<BluetoothDevice> res;

    public BluetoothFilter() {

        super();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean checkState() {
        if (bluetoothAdapter.isEnabled())
            return true;
        else
            return false;
    }

    @Override
    public boolean checkStartCondition(Object o) {
        getDevices();

        for (BluetoothDevice bd : res) {
            if (bd.getBondState() == 12) {

// BOND_NONE=10 NOT CONNECTED, BOND_BONDING=11 CONNECTING, BOND_BONDED=12 CONNECTED

                if (bd.getName().equals(targetDevice)) {

                    condition = true;
                    break;

                } else
                    condition = false;
            }
        }
        return condition;
    }

    @Override
    public boolean checkEndCondition() {
        return true;
    }

    public void getDevices() {

        res = bluetoothAdapter.getBondedDevices();
    }

    public String getTargetDevice() {
        return targetDevice;
    }

    public void setTargetDevice(String targetDevice) {
        this.targetDevice = targetDevice;
    }

    public Set<BluetoothDevice> getRes() {
        getDevices();
        return res;
    }

    @Override
    public String toString() {

        if (isActivatedFilter())
            return "Bluetooth: " + targetDevice;
        else
            return "";
    }

}