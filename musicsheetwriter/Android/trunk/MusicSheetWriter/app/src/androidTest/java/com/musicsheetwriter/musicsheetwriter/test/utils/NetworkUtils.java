package com.musicsheetwriter.musicsheetwriter.test.utils;

import android.content.Context;
import android.net.wifi.WifiManager;

import java.lang.reflect.InvocationTargetException;


public class NetworkUtils {

    public static boolean WIFI_STATE;

    public static boolean getWifiEnabled(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    public static void setWifiEnabled(Context context, boolean enable) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(enable);
    }

    public static void saveState(Context context) throws ClassNotFoundException,
            NoSuchMethodException, InvocationTargetException, IllegalAccessException,
            NoSuchFieldException {
        WIFI_STATE = getWifiEnabled(context);
    }

    public static void restoreState(Context context) throws IllegalAccessException,
            InvocationTargetException, NoSuchFieldException, NoSuchMethodException,
            ClassNotFoundException {
        setWifiEnabled(context, WIFI_STATE);
    }
}
