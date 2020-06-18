package com.thu.thuhelp.utils;

import java.util.LinkedList;

public class ChatContent {
    public String uid;
    public LinkedList<Message> msgList;

    ChatContent(String uid, LinkedList<Message> msgList) {
        this.uid = uid;
        this.msgList = msgList;
    }
}