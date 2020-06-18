package com.thu.thuhelp;

import android.app.Application;

public class App extends Application {
    private String skey = null;

    public String getSkey() {
        return skey;
    }

    public void setSkey(String skey) {
        this.skey = skey;
    }
}
