package com.thu.thuhelp.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

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
            EXTRA_MESSAGE = "com.thu.thuhelp.Service.ChatWebSocketClientService.extra.message";

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
                if (type == 0) {
                    return;
                }
                if (type == 1) {
                    JSONObject jsonMsg = res.getJSONObject("data");
                    receiveSingle(jsonMsg);
                } else if (type == 2) {
                    JSONArray msgList = res.getJSONArray("data");
                    int length = msgList.length();
                    for (int i = 0; i < length; ++i) {
                        JSONObject jsonMsg = msgList.getJSONObject(i);
                        receiveSingle(jsonMsg);
                    }
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
        intent.putExtra(EXTRA_MESSAGE, message.content);
        sendBroadcast(intent);
        chatIO.saveChatList();
    }

    public void updateChat(Message message, String uid) {
        if (this.uid != null && this.uid.equals(uid)) {
            messageList.addFirst(message);
            Intent intent = new Intent();
            intent.setAction(ACTION_UID_MESSAGE);
            intent.putExtra(EXTRA_MESSAGE, message.content);
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
