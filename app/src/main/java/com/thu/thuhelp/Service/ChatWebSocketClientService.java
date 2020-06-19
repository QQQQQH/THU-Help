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

    private ChatWebSocketClientBinder binder = new ChatWebSocketClientBinder();
    private ChatIO chatIO = null;

    public static final String
            ACTION_MESSAGE = "com.thu.thuhelp.Service.ChatWebSocketClientService.action.message",
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

    public void connectClient(String skey, File dir) {
        if (client != null) {
            return;
        }
        chatIO = new ChatIO(dir, chatList);
        chatIO.getChatList();

        URI uri = URI.create("ws://123.57.140.189/websocket/" + skey);
        client = new ChatWebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                Intent intent = new Intent();
                intent.setAction(ACTION_MESSAGE);
                intent.putExtra(EXTRA_MESSAGE, message);
                sendBroadcast(intent);
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

            ChatAbstract chatAbstract = chatAbstractList.getAbstractByUid(uid);
            Message message = new Message(messageText, timeStamp, Message.TYPE_RECEIVED);
            if (chatAbstract == null) {
                chatList.addFirst(new ChatAbstract(uid, messageText, timeStamp));
            } else {
                chatAbstractList.chatList.remove(chatAbstract);
                chatAbstract.timeStamp = timeStamp;
                chatAbstract.lastMsg = messageText;
                chatAbstractList.chatList.addFirst(chatAbstract);
            }
            chatIO.saveChatList();
            chatIO.updateChat(message, uid);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }

    }

    public LinkedList<Message> getChat(String uid) {
        return chatIO.getChat(uid);
    }

}
