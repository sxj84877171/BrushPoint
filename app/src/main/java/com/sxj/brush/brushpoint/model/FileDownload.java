package com.sxj.brush.brushpoint.model;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by elvissun on 7/4/2016.
 */
public class FileDownload {

    public static interface Progress{
        void onProgress(int all,int size);
    }

    public static File downloadFile(String urlStr) {
        File insallPath = new File(Install.INTALL_PATH);
        if (!insallPath.exists()) {
            insallPath.mkdirs();
        }
        File downloadFile = new File(Install.INTALL_PATH + getFileName(urlStr));
        if (!downloadFile.exists()) {
            try {
                downloadFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        URL url = null;
        InputStream input = null;
        FileOutputStream fos = null;
        try {
            url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //取得inputStream，并进行读取
            input = conn.getInputStream();
            byte[] bytes = new byte[1024*8];
            fos = new FileOutputStream(downloadFile);
            int len = -1;
            while ((len = input.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
                System.out.println(len);
            }
            System.out.println("download end:" + downloadFile.getName());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return downloadFile;
    }


    public static File downloadFile(String urlStr,Progress progress) {
        File insallPath = new File(Install.INTALL_PATH);
        if (!insallPath.exists()) {
            insallPath.mkdirs();
        }
        File downloadFile = new File(Install.INTALL_PATH + getFileName(urlStr));
        if (!downloadFile.exists()) {
            try {
                downloadFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        URL url = null;
        InputStream input = null;
        FileOutputStream fos = null;
        int size = 0 ;
        try {
            url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
            //取得inputStream，并进行读取
            input = conn.getInputStream();
            int allSize = conn.getContentLength();
            byte[] bytes = new byte[1024*8];
            fos = new FileOutputStream(downloadFile);
            int len = -1;
            while ((len = input.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
                size+= len;
                if(progress != null){
                    progress.onProgress(allSize,size);
                }
            }
            System.out.println("download end:" + downloadFile.getName());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return downloadFile;
    }


    public static String getFileName(String url) {
        String result = "";
        int index = url.indexOf('?');
        if (index == -1) {
            index = url.lastIndexOf("/");
            if (index != -1) {
                result = url.substring(index + 1);
            }
            return result;
        }
        int start = url.substring(0, index).lastIndexOf("/");
        if (start != -1) {
            result = url.substring(start + 1, index);
        }
        return result;
    }


}
