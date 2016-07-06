package com.sxj.brush.brushpoint.model.bean;

/**
 * Created by elvissun on 7/4/2016.
 */
public class ApkInformation {

    private String packageName ;
    private String mainAcitivity;
    private boolean hasInstall ;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getMainAcitivity() {
        return mainAcitivity;
    }

    public void setMainAcitivity(String mainAcitivity) {
        this.mainAcitivity = mainAcitivity;
    }

    public boolean isInstall() {
        return hasInstall;
    }

    public void setInstall(boolean hasInstall) {
        this.hasInstall = hasInstall;
    }
}
