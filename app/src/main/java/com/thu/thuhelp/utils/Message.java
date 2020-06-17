package com.thu.thuhelp.utils;

public class Message {
    public static final int TYPE_RECEIVED = 0, TYPE_SEND = 1;

    public String content;
    public int type;

    public Message(String content, int type) {
        this.content = content;
        this.type = type;
    }
}
