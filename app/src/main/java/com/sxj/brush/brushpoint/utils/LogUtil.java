package com.sxj.brush.brushpoint.utils;

import android.text.TextUtils;
import android.util.Log;

import com.sxj.brush.brushpoint.model.Install;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by elvissun on 7/6/2016.
 */
public class LogUtil {
    public static String customTagPrefix = "BrushPoint";
    private static FileWriter fileWriter;
    public static String LOG_PATH = File.separator + "sdcard" + File.separator + "BrushPoint" + File.separator + "log" + File.separator;

    public static String SPEAR_LINE = "------------------------------------------------------------------------------------------------------------------------------------------\n" +
            "------------------------------------------------------------------------------------------------------------------------------------------\n"+
            "------------------------------------------------------------------------------------------------------------------------------------------\n";

    private LogUtil() {
        init();
    }

    private static void init() {
        if (fileWriter == null) {
            File path = new File(LOG_PATH);
            if (!path.exists()) {
                path.mkdirs();
            }
            File logFile = new File(LOG_PATH + customTagPrefix + ".log");

            if(!logFile.exists()){
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                if (fileWriter == null) {
                    fileWriter = new FileWriter(logFile, true);
                }
            } catch (IOException e) {
            }
        }
    }

    private static String generateTag() {
        StackTraceElement caller = new Throwable().getStackTrace()[2];
        String tag = "%s.%s(L:%d)";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        tag = TextUtils.isEmpty(customTagPrefix) ? tag : customTagPrefix + ":" + tag;
        return tag;
    }

    public static void d(String content) {
        String tag = generateTag();
        Log.d(tag, content);
    }

    public static void d(String content, Throwable tr) {
        String tag = generateTag();

        Log.d(tag, content, tr);
    }

    public static void e(String content) {
        String tag = generateTag();

        Log.e(tag, content);
    }

    public static void e(String content, Throwable tr) {
        String tag = generateTag();

        Log.e(tag, content, tr);
    }

    public static void i(String content) {
        String tag = generateTag();

        Log.i(tag, content);
    }

    public static void i(String content, Throwable tr) {
        String tag = generateTag();

        Log.i(tag, content, tr);
    }

    public static void v(String content) {
        String tag = generateTag();

        Log.v(tag, content);
    }

    public static void v(String content, Throwable tr) {
        String tag = generateTag();
        Log.v(tag, content, tr);
    }

    public static void w(String content) {
        String tag = generateTag();

        Log.w(tag, content);
    }

    public static void w(String content, Throwable tr) {
        String tag = generateTag();

        Log.w(tag, content, tr);
    }

    public static void w(Throwable tr) {
        String tag = generateTag();

        Log.w(tag, tr);
    }


    public static void wtf(String content) {
        String tag = generateTag();

        Log.wtf(tag, content);
    }

    public static void wtf(String content, Throwable tr) {
        String tag = generateTag();

        Log.wtf(tag, content, tr);
    }

    public static void wtf(Throwable tr) {
        String tag = generateTag();

        Log.wtf(tag, tr);
    }


    public static void writeToFile(String message) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        String dateStr = sdf.format(date);
        init();
        String tag = generateTag();
        Log.i(tag, message);
        String msg = String.format("%s : %s\n", dateStr, message);
        if (fileWriter != null) {
            try {
                fileWriter.write(msg);
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeSpearLine() {
        try {
            init();
            if(fileWriter != null) {
                fileWriter.write(SPEAR_LINE);
                fileWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void close(){
        if(fileWriter != null){
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
