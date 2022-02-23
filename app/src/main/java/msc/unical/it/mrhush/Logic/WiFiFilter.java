package msc.unical.it.mrhush.Logic;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ADAMONE on 26/03/2018.
 */

public class WiFiFilter extends Filter implements Serializable{

    transient List<WifiConfiguration> res;
    transient WifiManager wifiManager;
    String targetNetwork = "";

    public WiFiFilter() {
        super();
        wifiManager = (WifiManager) MrHushService.c.getSystemService(Context.WIFI_SERVICE);
    }

    public List<WifiConfiguration> getRes() {

        getConfNetworks();
        return res;
    }


    public boolean checkState() {
        return (wifiManager.isWifiEnabled());
    }

    @Override
    public boolean checkStartCondition(Object o) {

        if(isActivatedFilter()) {
            wifiManager= (WifiManager) MrHushService.c.getSystemService(Context.WIFI_SERVICE);
           if (wifiManager.isWifiEnabled())
                if (wifiManager.getConnectionInfo().getSSID().equals(targetNetwork))
                    condition = true;
                else
                    condition = false;
        }
        return condition;
    }

    @Override
    public boolean checkEndCondition() {
        return true;
    }

    public void getConfNetworks() {
        res = wifiManager.getConfiguredNetworks();
    }

    public String getTargetNetwork() {
        return targetNetwork;
    }

    public void setTargetNetwork(String targetNetwork) {
        this.targetNetwork = targetNetwork;

    }

    @Override
    public String toString() {
        if (isActivatedFilter())
            return "WiFi: " + targetNetwork;
        else
            return "";
    }
}
