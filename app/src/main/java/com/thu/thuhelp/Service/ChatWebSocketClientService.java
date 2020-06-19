package com.thu.thuhelp.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcelable;

import com.thu.thuhelp.utils.ChatAbstract;
import com.thu.thuhelp.utils.ChatAbstractList;
import com.thu.thuhelp.utils.ChatIO;
import com.thu.thuhelp.utils.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.util.LinkedList;

public class ChatWebSocketClientService extends Service {
    public ChatWebSocketClient client;
    public final ChatAbstractList chatAbstractList = new ChatAbstractList();
    public final LinkedList<ChatAbstract> chatList = chatAbstractList.chatList;
    public final LinkedList<Message> messageList = new LinkedList<>();

    private ChatWebSocketClientBinder binder = new ChatWebSocketClientBinder();
    private ChatIO chatIO = null;
    private String uid = null;

    public static final String
            ACTION_ALL_MESSAGE = "com.thu.thuhelp.Service.ChatWebSocketClientService.action.all_message",
            ACTION_UID_MESSAGE = "com.thu.thuhelp.Service.ChatWebSocketClientService.action.uid_message",
            ACTION_NOT_MESSAGE = "com.thu.thuhelp.Service.ChatWebSocketClientService.action.not_message",
            EXTRA_MESSAGE = "com.thu.thuhelp.Service.ChatWebSocketClientService.extra.message",
            EXTRA_NOT_TEXT = "com.thu.thuhelp.Service.ChatWebSocketClientService.extra.not_text",
            EXTRA_NOT_TYPE = "com.thu.thuhelp.Service.ChatWebSocketClientService.extra.not_type";

    public class ChatWebSocketClientBinder extends Binder {
        public ChatWebSocketClientService getService() {
            return ChatWebSocketClientService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void connectClient(String skey, File dir, String uid) {
        if (uid != null) {
            this.uid = uid;
        }
        if (client != null) {
            return;
        }
        chatIO = new ChatIO(dir, chatList, messageList);
        chatIO.getChatList();

        URI uri = URI.create("ws://123.57.140.189/websocket/" + skey);
        client = new ChatWebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                receiveMsg(message);
            }
        };
        try {
            client.connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void disConnectClient() {
        if (client != null) {
            client.close();
            client = null;
            chatIO = null;
            chatList.clear();
            messageList.clear();
        }
    }

    public void receiveMsg(String message) {
        try {
            JSONObject res = new JSONObject(message);
            int statusCode = res.getInt("status");
            if (statusCode == 200) {
                int type = res.getInt("type");
                switch (type) {
                    case 0:
                        return;
                    case 1:
                        JSONObject jsonMsg = res.getJSONObject("data");
                        receiveSingle(jsonMsg);
                        break;
                    case 2:
                        JSONArray msgList = res.getJSONArray("data");
                        int length = msgList.length();
                        for (int i = 0; i < length; ++i) {
                            jsonMsg = msgList.getJSONObject(i);
                            receiveSingle(jsonMsg);
                        }
                        break;
                    case 3:
                        jsonMsg = res.getJSONObject("data");
                        receiveNotification(jsonMsg);
                        break;
                    case 4:
                        msgList = res.getJSONArray("data");
                        length = msgList.length();
                        for (int i = 0; i < length; ++i) {
                            jsonMsg = msgList.getJSONObject(i);
                            receiveNotification(jsonMsg);
                        }
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void receiveSingle(JSONObject jsonMsg) {
        try {
            String uid = jsonMsg.getString("sender");
            String messageText = jsonMsg.getString("messageText");
            String timeStamp = jsonMsg.getString("sendTime");

            Message message = new Message(messageText, timeStamp, Message.TYPE_RECEIVED);
            updateChatList(message, uid);
            updateChat(message, uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void receiveNotification(JSONObject jsonMsg) {
        try {
            String text = jsonMsg.getString("notificationText");
            int type = jsonMsg.getInt("notificationType");

            Intent intent = new Intent();
            intent.setAction(ACTION_ALL_MESSAGE);
            intent.putExtra(EXTRA_NOT_TEXT, text);
            intent.putExtra(EXTRA_NOT_TYPE, type);
            sendBroadcast(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateChatList(Message message, String uid) {
        ChatAbstract chatAbstract = chatAbstractList.getAbstractByUid(uid);
        if (chatAbstract == null) {
            chatList.addFirst(new ChatAbstract(uid, message.content, message.timeStamp));
        } else {
            chatAbstractList.chatList.remove(chatAbstract);
            chatAbstract.timeStamp = message.timeStamp;
            chatAbstract.lastMsg = message.content;
            chatAbstractList.chatList.addFirst(chatAbstract);
        }
        Intent intent = new Intent();
        intent.setAction(ACTION_ALL_MESSAGE);
        intent.putExtra(EXTRA_MESSAGE, (Parcelable) message);
        sendBroadcast(intent);
        chatIO.saveChatList();
    }

    public void updateChat(Message message, String uid) {
        if (this.uid != null && this.uid.equals(uid)) {
            messageList.addFirst(message);
            Intent intent = new Intent();
            intent.setAction(ACTION_UID_MESSAGE);
            intent.putExtra(EXTRA_MESSAGE, (Parcelable) message);
            sendBroadcast(intent);
            chatIO.updateChatCurrent(message, uid);
        } else {
            chatIO.updateChat(message, uid);
        }
    }

    public LinkedList<Message> getChat() {
        chatIO.getChat(uid);
        return messageList;
    }

    public void resetUid() {
        uid = null;
    }

}
