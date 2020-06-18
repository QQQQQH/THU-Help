package com.thu.thuhelp.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatAbstract implements Parcelable {
    public String uid, nickName, lastMsg;

    public ChatAbstract(String uid, String nickName, String lastMsg) {
        this.uid = uid;
        this.nickName = nickName;
        this.lastMsg = lastMsg;
    }

    protected ChatAbstract(Parcel in) {
        uid = in.readString();
        nickName = in.readString();
        lastMsg = in.readString();
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
        dest.writeString(nickName);
        dest.writeString(lastMsg);
    }
}
