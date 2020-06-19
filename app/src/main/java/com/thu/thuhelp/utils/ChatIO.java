package com.thu.thuhelp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

public class ChatIO {
    private File chatDir;
    private final LinkedList<ChatAbstract> chatList;
    public final LinkedList<Message> messageList;

    public ChatIO(File dir, LinkedList<ChatAbstract> chatList, LinkedList<Message> messageList) {
        this.chatDir = new File(dir, "chat");
        this.chatList = chatList;
        this.messageList = messageList;
        if (!chatDir.exists()) {
            chatDir.mkdirs();
        }
    }

    public void saveChatList() {
        try {
            File chatListFile = new File(chatDir, "chat_list");
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(chatListFile));
            for (ChatAbstract chatAbstract : chatList) {
                oos.writeObject(chatAbstract);
            }
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getChatList() {
        chatList.clear();
        try {
            File chatListFile = new File(chatDir, "chat_list");
            if (!chatListFile.exists()) {
                return;
            }
            FileInputStream fis = new FileInputStream(chatListFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            while (fis.available() > 0) {
                chatList.add((ChatAbstract) ois.readObject());
            }
            ois.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getChat(String uid) {
        messageList.clear();
        try {
            File chatFile = new File(chatDir, uid);
            if (chatFile.exists()) {
                FileInputStream fis = new FileInputStream(chatFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                while (fis.available() > 0) {
                    messageList.add((Message) ois.readObject());
                }
                ois.close();
                fis.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateChat(Message message, String uid) {
        LinkedList<Message> messageList = new LinkedList<>();
        try {
            // read
            File chatFile = new File(chatDir, uid);
            if (chatFile.exists()) {
                FileInputStream fis = new FileInputStream(chatFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                while (fis.available() > 0) {
                    messageList.add((Message) ois.readObject());
                }
                ois.close();
                fis.close();
            }

            // add
            messageList.addFirst(message);

            // write
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(chatFile));
            for (Message msg : messageList) {
                oos.writeObject(msg);
            }
            oos.flush();
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateChatCurrent(Message message, String uid) {
        try {
            File chatFile = new File(chatDir, uid);

            // write
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(chatFile));
            for (Message msg : messageList) {
                oos.writeObject(msg);
            }
            oos.flush();
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
