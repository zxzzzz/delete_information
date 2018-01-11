package com.example.cache;

import android.graphics.drawable.Drawable;

/**
 * Created by zx on 16-9-2.
 */
public class AppInfo {
    long cacheSize;
    long dataSize;
    long codeSize;
    String label;
    Drawable icon;
    String pckName;

    public String getPckName() {
        return pckName;
    }

    public void setPckName(String pckName) {
        this.pckName = pckName;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public long getDataSize() {
        return dataSize;
    }

    public long getCodeSize() {
        return codeSize;
    }

    public String getLabel() {
        return label;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setCodeSize(long codeSize) {
        this.codeSize = codeSize;
    }

    public void setDataSize(long dataSize) {
        this.dataSize = dataSize;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
