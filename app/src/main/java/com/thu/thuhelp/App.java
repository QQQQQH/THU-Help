package com.thu.thuhelp;

import android.app.Application;

import java.io.File;

public class App extends Application {
    private String skey = null;
    private String uid = null;
    private File dir = null;

    public String getSkey() {
        return skey;
    }

    public String getUid() {
        return uid;
    }

    public File getDir() {
        return dir;
    }

    public void setSkey(String skey) {
        this.skey = skey;
        setDir();
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setDir() {
        if (uid == null) {
            dir = null;
        } else {
            dir = new File(getFilesDir(), uid);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }
}
