package com.sxj.brush.brushpoint.model;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by elvissun on 7/4/2016.
 */
public class Install {

    public final static String INTALL_PATH = File.separator + "sdcard" + File.separator + "BrushPoint" + File.separator + "apk" + File.separator;


    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     *
     * @return 应用程序是/否获取Root权限
     */
    public static boolean installHasNoUi(File pathFile) {
        String installCmd = "pm install -r " + pathFile.getAbsolutePath()  +"\n" ;
        try {
            String result = readConsole(installCmd, true);
            System.out.println(result);
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
        return true;
    }


    public static void installHasUi(Context context, File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public static String readConsole(String cmd, Boolean isPrettify) throws Exception{
        StringBuffer cmdout = new StringBuffer();
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            OutputStream os = process.getOutputStream();
            os.write(cmd.getBytes());
            os.flush();
            os.write("exit\n".getBytes());
            InputStream fis = process.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = null;
            if (isPrettify == null || isPrettify) {
                while ((line = br.readLine()) != null) {
                    cmdout.append(line);
                }
            } else {
                while ((line = br.readLine()) != null) {
                    cmdout.append(line).append(
                            System.getProperty("line.separator"));
                }
            }
            process.waitFor();
        } finally {
            process.destroy();
        }
        return cmdout.toString().trim();
    }
}
