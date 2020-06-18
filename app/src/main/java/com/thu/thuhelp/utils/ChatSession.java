package com.thu.thuhelp.utils;

import java.util.LinkedList;

public class ChatSession {
    public LinkedList<ChatAbstract> abstracrList = new LinkedList<>();
    public LinkedList<ChatContent> contentList = new LinkedList<>();

    public ChatAbstract getAbstractByUid(String uid) {
        final int length = abstracrList.size();
        for (int i = 0; i < length; ++i) {
            ChatAbstract chatAbstract = abstracrList.get(i);
            if (chatAbstract.uid.equals(uid)) {
                return chatAbstract;
            }
        }
        return null;
    }

    public ChatContent getContentByUid(String uid) {
        final int length = contentList.size();
        for (int i = 0; i < length; ++i) {
            ChatContent chatContent = contentList.get(i);
            if (chatContent.uid.equals(uid)) {
                return chatContent;
            }
        }
        return null;
    }
}
