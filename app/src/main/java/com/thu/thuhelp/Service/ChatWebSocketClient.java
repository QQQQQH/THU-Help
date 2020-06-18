package com.thu.thuhelp.Service;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class ChatWebSocketClient extends WebSocketClient {
    public ChatWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.e("ChatWebSocketClient", "onOpen");
    }

    @Override
    public void onMessage(String message) {
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.e("ChatWebSocketClient", "onClose");
    }

    @Override
    public void onError(Exception ex) {
        Log.e("ChatWebSocketClient", "onError");
    }
}
