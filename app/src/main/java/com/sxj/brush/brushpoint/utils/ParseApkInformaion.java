package com.sxj.brush.brushpoint.utils;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.sxj.brush.brushpoint.model.bean.ApkInformation;

import java.io.File;

/**
 * Created by elvissun on 7/4/2016.
 */
public class ParseApkInformaion {

    public static ApkInformation getApkInformation(Context context, File file) {
        ApkInformation apkInformation = null;
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(file.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
        if(info != null) {
            apkInformation = new ApkInformation();
            apkInformation.setPackageName(info.packageName);
            for (ActivityInfo activityInfo : info.activities) {
                if (activityInfo.launchMode == 0) {
                    apkInformation.setMainAcitivity(activityInfo.name);
                    break;
                }
            }
        }
        return apkInformation;
    }

}
