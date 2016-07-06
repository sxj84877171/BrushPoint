package com.sxj.brush.brushpoint.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.DataOutputStream;
import java.io.File;

/**
 * Created by elvissun on 7/4/2016.
 */
public class UnInstall {

    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     *
     * @return 应用程序是/否获取Root权限
     */
    public static boolean uninstallHasNoUi(String packageName) {
        String installCmd = "pm uninstall " + packageName  + "\n";
        try {
           String result = Install.readConsole(installCmd,true);
            System.out.println(result);
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
//
        return true;
    }


    public static void uninstallHasUI(Context context,Uri packageURI){
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(uninstallIntent);
    }
}
