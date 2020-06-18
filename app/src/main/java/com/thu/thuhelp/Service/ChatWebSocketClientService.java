package com.thu.thuhelp.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.net.URI;

public class ChatWebSocketClientService extends Service {
    public ChatWebSocketClient client;
    private ChatWebSocketClientBinder binder = new ChatWebSocketClientBinder();

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

    public void setClient(String skey) throws InterruptedException {
        URI uri = URI.create("ws://123.57.140.189/websocket/" + skey);
        client = new ChatWebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                Intent intent = new Intent();
                intent.setAction(ACTION_MESSAGE);
                intent.putExtra(EXTRA_MESSAGE, message);
                sendBroadcast(intent);
            }
        };
        client.connectBlocking();
    }
}
