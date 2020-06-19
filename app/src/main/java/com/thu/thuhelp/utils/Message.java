package com.thu.thuhelp.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Message implements Parcelable, Serializable {
    public static final int TYPE_RECEIVED = 0, TYPE_SEND = 1;

    public String content, timeStamp;
    public int type;

    public Message(String content, String timeStamp, int type) {
        this.content = content;
        this.timeStamp = timeStamp;
        this.type = type;
    }

    @Override
    public String toString() {
        return "content=" + content + "\n timeStamp=" + timeStamp + "\n type=" + type;
    }

    protected Message(Parcel in) {
        content = in.readString();
        timeStamp = in.readString();
        type = in.readInt();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
        dest.writeString(timeStamp);
        dest.writeInt(type);
    }
}
