package com.thu.thuhelp.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ChatAbstract implements Parcelable, Serializable {
    public String uid, lastMsg, timeStamp = "0";

    public ChatAbstract(String uid, String lastMsg, String timeStamp) {
        this.uid = uid;
        this.lastMsg = lastMsg;
        this.timeStamp = timeStamp;
    }


    protected ChatAbstract(Parcel in) {
        uid = in.readString();
        lastMsg = in.readString();
        timeStamp = in.readString();
    }

    public static final Creator<ChatAbstract> CREATOR = new Creator<ChatAbstract>() {
        @Override
        public ChatAbstract createFromParcel(Parcel in) {
            return new ChatAbstract(in);
        }

        @Override
        public ChatAbstract[] newArray(int size) {
            return new ChatAbstract[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(lastMsg);
        dest.writeString(timeStamp);
    }

    @Override
    public String toString() {
        return "uid=" + uid + "\n lastMsg=" + lastMsg + "\n timeStamp=" + timeStamp;
    }

}
