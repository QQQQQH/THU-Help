package com.thu.thuhelp.utils;

import java.util.LinkedList;

public class ChatAbstractList {
    public LinkedList<ChatAbstract> chatList = new LinkedList<>();

    public ChatAbstract getAbstractByUid(String uid) {
        final int length = chatList.size();
        for (int i = 0; i < length; ++i) {
            ChatAbstract chatAbstract = chatList.get(i);
            if (chatAbstract.uid.equals(uid)) {
                return chatAbstract;
            }
        }
        return null;
    }
}
