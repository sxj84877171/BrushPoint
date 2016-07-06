package com.sxj.brush.brushpoint.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.sxj.brush.brushpoint.model.DeviceInfomation;
import com.sxj.brush.brushpoint.model.FileDownload;
import com.sxj.brush.brushpoint.model.Install;
import com.sxj.brush.brushpoint.model.UnInstall;
import com.sxj.brush.brushpoint.model.bean.ApkInformation;
import com.sxj.brush.brushpoint.utils.LogUtil;
import com.sxj.brush.brushpoint.utils.ParseApkInformaion;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InstallService extends Service {

    public static final String MESSAGE_BROADCASE = "com.sxj.brush.brushpoint.inforeceiver";

    private ApkInformation apkInformation;

    private final String ADD_APP = "android.intent.action.PACKAGE_ADDED";
    private final String REPLACE_APP = "android.intent.action.PACKAGE_REPLACED";
    private final String REMOVE_APP = "android.intent.action.PACKAGE_REMOVED";
    private Handler handler = new Handler();
    private Object lock;
    private AppBroadcastReceiver appBroadcastReceiver;
    private boolean isUninstall;
    private long sleepTime = 20000;

    private int success = 0 ;
    private int fail = 0 ;
    private int cur = 0 ;

    public InstallService() {
    }

    private ExecutorService pool;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("onCreate");
        pool = Executors.newSingleThreadExecutor();
    }

    @NonNull
    private DeviceInfomation getDeviceInfomation() {
        DeviceInfomation deviceInfomation = new DeviceInfomation();
        deviceInfomation.setMac(DeviceInfomation.getLocalMacAddress(this));
        deviceInfomation.setDeviceId(DeviceInfomation.getDeviceId(this));
        deviceInfomation.setSn(DeviceInfomation.getSnNumber());
        LogUtil.writeToFile(deviceInfomation.toJson());
        return deviceInfomation;
    }

    private void changeDeviceInformation(){
        DeviceInfomation.setSnNumber();
        DeviceInfomation.changeLocalMacAddress(this);
        DeviceInfomation.randDeviceId(this);
    }

    private StringBuilder stringBuilder = new StringBuilder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        LogUtil.writeSpearLine();
        System.out.println("onStartCommand");
        if (intent != null) {
            final String url = intent.getStringExtra("url");
            final int times = intent.getIntExtra("times", 1);
             isUninstall = intent.getBooleanExtra("isUninstall",false);
            sleepTime = intent.getLongExtra("delay",20000);
            LogUtil.writeToFile(String.format("url:%s, test times:%d",url,times));
            Thread thread = new Thread() {
                public void run() {
                    success = 0 ;
                    cur = 0 ;
                    fail = 0 ;
                   while(success <  times){
                        try {
                            String log = "第" + ++cur + "次" ;
                            stringBuilder.append(log).append("\n");
                            LogUtil.writeToFile(log);
                            changeDeviceInformation();
                            DeviceInfomation deviceInfomation = getDeviceInfomation();
                            stringBuilder.append(deviceInfomation.toJson() + "\n");
                            install(url);
                            success++;
                            stringBuilder.append(success).append("次success").append("\n");
                            log = success + "次success" ;
                            LogUtil.writeToFile(log);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            fail++;
                            String log = fail + "次fail" ;
                            stringBuilder.append(fail).append("次fail").append("\n");
                            LogUtil.writeToFile(log);
                        }

                    }
                    LogUtil.writeToFile("测试结束，一共要测试：" + times + "次，其中成功：" + success+ "次，失败" +fail +  "次");
                    stringBuilder.append("测试结束，一共要测试：" + times + "次，其中成功：" + success+ "次，失败" +fail +  "次").append("\n");
                    sendMsgToUI();
                }
            };
            pool.execute(thread);
        }
        System.out.println("onStartCommand end");
        return START_STICKY_COMPATIBILITY;
    }

    private void install(String url) throws Exception {
        File file = downloadFile(url);// download
        if(file != null) {
            apkInformation = ParseApkInformaion.getApkInformation(InstallService.this, file);// get information
            installApplication(file);//install
            sleepTime(5000);//sleep
            launchApplication();//launch
            sleepTime(sleepTime);//sleep
            if(isUninstall){
                uninstallApplication(apkInformation.getPackageName());//uninstall
            }
        }else{
            stringBuilder.append("下载失败!\n");
            throw new Exception("fail");
        }
    }

    private void sleepTime(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private File downloadFile(String url) {
        File file = FileDownload.downloadFile(url, new FileDownload.Progress() {

            private long cur = System.currentTimeMillis();
            @Override
            public void onProgress(int all, int size) {
                if(System.currentTimeMillis() - cur > 500) {
                    String text = String.format("download size: %s/%s\n", getSizeString(size), getSizeString(all));
                    sendMsgToUI(text);
                    cur = System.currentTimeMillis();
                }
            }
        });
        if(file != null) {
            stringBuilder.append("file:").append(file.getName()).append("\n")
                    .append("file path:").append(file.getPath()).append("\n")
                    .append("file size:");
            if (file.length() < 1024) {
                stringBuilder.append(file.length()).append("B");
            } else if (file.length() < 1024 * 1024) {
                stringBuilder.append(String.format("%.2f", file.length() / 1024.0)).append("KB");
            } else if (file.length() < 1024 * 1024 * 1024) {
                stringBuilder.append(String.format("%.2f", file.length() / 1024.0 / 1024)).append("MB");
            } else {
                stringBuilder.append(String.format("%.2f", file.length() / 1024.0 / 1024 / 1024)).append("G");
            }
            stringBuilder.append("\n");

            sendMsgToUI();
        }
        return file;
    }

    @NonNull
    private String getSizeString(int size) {
        String text = "" ;
        if (size < 1024) {
            text = size + "B";
        } else if (size < 1024 * 1024) {
            text = String.format("%.2f", size / 1024.0) + "KB";
        } else if (size < 1024 * 1024 * 1024) {
            text = String.format("%.2f", size / 1024.0 / 1024.0) + "MB";
        } else {
            text = String.format("%.4f", size / 1024.0 / 1024 / 1024) + "G";
        }
        return text;
    }

    private void installApplication(File file) {
        stringBuilder.append("正在进行安装。。。\n");
        sendMsgToUI();
        if (apkInformation != null) {
            if (!Install.installHasNoUi(file)) {
                stringBuilder.append("root 权限获取失败. 进行有界面安装\n");
                Install.installHasUi(InstallService.this, file);
            } else {
                stringBuilder.append("root 权限获取成功. 进行无界面安装\n");
            }
            sendMsgToUI();
        }
    }

    private void sendMsgToUI() {
        sendMsgToUI(null);
    }

    private class UITask implements Runnable {
        private String process;

        public void setMessage(String msg) {
            this.process = msg;
        }

        @Override
        public void run() {
            Intent intent = new Intent(MESSAGE_BROADCASE);
            intent.putExtra("msg", stringBuilder.toString());
            if (process != null) {
                intent.putExtra("progress", process);
            }
            sendBroadcast(intent);
            task = null;
        }
    }

    private UITask task;
    private long current = System.currentTimeMillis();

    private void sendMsgToUI(String process) {
        Intent intent = new Intent(MESSAGE_BROADCASE);
        intent.putExtra("msg", stringBuilder.toString());
        intent.putExtra("success",success);
        intent.putExtra("fail",fail);
        if (process != null) {
            intent.putExtra("progress", process);
        }
        sendBroadcast(intent);
        if(stringBuilder.toString().length() > 200){
            stringBuilder = new StringBuilder();
        }

    }

    @Override
    public void onDestroy() {
        pool.shutdown();
//        unregisterReceiver(appBroadcastReceiver);
        Toast.makeText(this, "程序彻底退出", Toast.LENGTH_LONG).show();
        LogUtil.writeSpearLine();
        LogUtil.close();
        super.onDestroy();
    }


    public class AppBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ADD_APP.equals(action) || REPLACE_APP.equals(action)) {
                final String packageName = intent.getDataString();
                stringBuilder.append("安装了:").append(packageName).append("\n");

                sendMsgToUI();
                if (("package:" + apkInformation.getPackageName()).equals(packageName)) {
                    if (!apkInformation.isInstall()) {
                        stringBuilder.append("正在启动应用程序").append("\n");
                        try {
                            launchApplication();
                            apkInformation.setInstall(true);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                uninstallApplication(packageName);
                                sendMsgToUI();
                            }
                        }, 15 * 1000);
                    }
                }


            }
            if (REMOVE_APP.equals(action)) {
                String packageName = intent.getDataString();
                if (apkInformation.isInstall()) {
                    stringBuilder.append("卸载了:").append(packageName).append("\n");
                    sendMsgToUI();
                    if (lock != null) {
                        synchronized (lock) {
                            lock.notify();
                            lock = null;
                        }
                    }
                    apkInformation.setInstall(false);
                }

            }
        }
    }

    private void uninstallApplication(String packageName) {
        stringBuilder.append("正在进行卸载。。。\n");
        LogUtil.writeToFile("正在进行卸载。。。");
        Uri packageURI = Uri.parse("package:" + apkInformation.getPackageName());
        try {
            if (!UnInstall.uninstallHasNoUi(packageName)) {
                stringBuilder.append("获取root权限失败。进行有界面卸载。\n");
                LogUtil.writeToFile("获取root权限失败。进行有界面卸载。");
                UnInstall.uninstallHasUI(InstallService.this, packageURI);
            } else {
                stringBuilder.append("获取root权限成功。进行无界面卸载。\n");
                LogUtil.writeToFile("获取root权限成功。进行无界面卸载。");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        sendMsgToUI();
    }

    private void launchApplication() {
        Intent installIntent = new Intent();
        PackageManager packageManager = getPackageManager();
        installIntent = packageManager.getLaunchIntentForPackage(apkInformation.getPackageName());
        if(installIntent == null){
            stringBuilder.append("没有找到对象应用程序\n");
            return ;
        }
        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(installIntent);
        stringBuilder.append("正在启动应用程序").append("\n");
        sendMsgToUI();
    }
}
