package com.sxj.brush.brushpoint.model;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

/**
 * Created by elvissun on 7/4/2016.
 */
public class DeviceInfomation {

    private String sn;
    private String deviceId;
    private String mac;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public static String getLocalMacAddress(Context context) {
        String mac = null;
        try {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            mac = info.getMacAddress();
        } catch (Exception ex) {

        }
        return mac;
    }

    public static void changeLocalMacAddress(Context context){
        String mac = null;
        try {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            mac = info.getMacAddress();
            System.out.println(mac);
            Class<?> infoClass = info.getClass();
            Method[] ms = infoClass.getMethods();
            Method method = infoClass.getMethod("setMacAddress",String.class);
            method.setAccessible(true);
            method.invoke(info,generMAC());
            mac = info.getMacAddress();
            System.out.println(mac);

        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }


    public static String getDeviceId(Context context) {
        String deviceId = null;
        TelephonyManager telephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = telephonyMgr.getDeviceId();
        return deviceId;

    }

    public static void randDeviceId(Context context){

    }

    public static String getSnNumber(){
        return android.os.Build.SERIAL;
    }

    public static void setSnNumber(){
        try {
            String sn = getSnNumber();
            System.out.println(sn);

            Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method method = systemPropertiesClass.getMethod("set",String.class,String.class);
            method.invoke(null,"ro.serialno",generSN());

            sn = getSnNumber();
            System.out.println(sn);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static String generSN(){
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder stringBuilder = new StringBuilder();
        for(int i =0 ; i < 11 ;i++){
            int ran = (new Random()).nextInt();
            if(ran < 0){
                ran = -ran ;
            }
            stringBuilder.append(str.charAt(ran % str.length()));
        }
        return stringBuilder.toString();
    }

    public static String generMAC(){
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder stringBuilder = new StringBuilder();
        for(int i =0 ; i < 12 ;i++){
            int ran = (new Random()).nextInt();
            if(ran < 0){
                ran = -ran ;
            }
            if(i > 0 && i % 2 == 0){
                stringBuilder.append(":");
            }
            stringBuilder.append(str.charAt(ran % str.length()));
        }
        return stringBuilder.toString();
    }


    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
