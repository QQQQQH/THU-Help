package com.thu.thuhelp.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.LinkedList;

public class ChatContent implements Parcelable {
    public String uid;
    public LinkedList<Message> msgList = new LinkedList<>();

    public ChatContent(String uid, String content, String timeStamp, int type) {
        this.uid = uid;
        addMsg(content, timeStamp, type);
    }

    public ChatContent(String uid, LinkedList<Message> msgList) {
        this.uid = uid;
        this.msgList = msgList;
    }

    public void addMsg(String content, String timeStamp, int type) {
        final int length = msgList.size();
        int i;
        for (i = length - 1; i >= 0; --i) {
            if (timeStamp.compareTo(msgList.get(i).timeStamp) < 0) {
                continue;
            }
            break;
        }
        msgList.add(i + 1, new Message(content, timeStamp, type));
    }

    protected ChatContent(Parcel in) {
        uid = in.readString();
        if (msgList == null) {
            msgList = new LinkedList<>();
        }
        in.readList(msgList, Message.class.getClassLoader());
    }

    public static final Creator<ChatContent> CREATOR = new Creator<ChatContent>() {
        @Override
        public ChatContent createFromParcel(Parcel in) {
            return new ChatContent(in);
        }

        @Override
        public ChatContent[] newArray(int size) {
            return new ChatContent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeList(msgList);
    }
}