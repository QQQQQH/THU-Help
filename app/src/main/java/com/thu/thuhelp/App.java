package com.thu.thuhelp;

import android.app.Application;

public class App extends Application {
    private String skey = null;

    public String get_skey() {
        return skey;
    }

    public void set_skey(String skey) {
        this.skey = skey;
    }
}
