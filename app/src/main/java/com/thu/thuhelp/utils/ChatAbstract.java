package com.thu.thuhelp.utils;

public class ChatAbstract {
    public String uid, nickName, lastMsg;

    public ChatAbstract(String uid, String nickName, String lastMsg) {
        this.uid = uid;
        this.nickName = nickName;
        this.lastMsg = lastMsg;
    }
}
