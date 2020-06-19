package com.thu.thuhelp.ChatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thu.thuhelp.App;
import com.thu.thuhelp.MainActivity.ChatListFragment;
import com.thu.thuhelp.R;
import com.thu.thuhelp.Service.ChatWebSocketClient;
import com.thu.thuhelp.Service.ChatWebSocketClientService;
import com.thu.thuhelp.utils.Message;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerViewChat;
    private MessageListAdapter adapter;
    private EditText editTextMessage;
    private File avatarFile;
    private Bitmap rightAvatar;

    private App app;

    private ServiceConnection serviceConnection;
    private ChatWebSocketClient client;
    private ChatWebSocketClientService chatWebSocketClientService;
    private ChatWebSocketClientService.ChatWebSocketClientBinder binder;
    private ChatMsgReceiver chatMsgReceiver;

    private String uid;
    private File chatDir;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        app = (App) getApplication();
        setView();
        setService();
    }

    // set return actionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    void setView() {
        Intent intent = getIntent();
        uid = intent.getStringExtra(ChatListFragment.EXTRA_CHAT_UID);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // set return actionBar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        editTextMessage = findViewById(R.id.editTextMessage);

        // set avatar
        avatarFile = new File(new File(getFilesDir(), "images"), "avatar.jpg");
        try {
            rightAvatar = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.fromFile(avatarFile)));
        } catch (FileNotFoundException e) {
            rightAvatar = null;
            e.printStackTrace();
        }
        chatDir = new File(app.getDir(), "chat");
        if (!chatDir.exists()) {
            chatDir.mkdirs();
        }

        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        adapter = new MessageListAdapter(this, new LinkedList<>(), rightAvatar, uid, app);

        // reverse layout
        recyclerViewChat.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, true);
        recyclerViewChat.setLayoutManager(linearLayoutManager);
    }

    public void onClickSend(View view) {
        String content = editTextMessage.getText().toString();
        if (!content.equals("")) {
            Message message = new Message(content, "0", Message.TYPE_SEND);
            if (client != null) {
                HashMap<String, String> map = new HashMap<>();
                map.put("messageText", content);
                map.put("skey", app.getSkey());
                map.put("to", uid);
                JSONObject jsonObject = new JSONObject(map);
                try {
                    client.send(jsonObject.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                chatWebSocketClientService.updateChat(message, uid);
                chatWebSocketClientService.updateChatList(message, uid);
            }
            adapter.notifyDataSetChanged();
            editTextMessage.setText("");
            recyclerViewChat.smoothScrollToPosition(0);
        }
    }

    private void setService() {
        // bind service
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.e("Chat Service-Chat", "Service connect");
                binder = (ChatWebSocketClientService.ChatWebSocketClientBinder) service;
                chatWebSocketClientService = binder.getService();

                // connect client
                chatWebSocketClientService.connectClient(app.getSkey(), app.getDir(), uid);
                client = chatWebSocketClientService.client;
                adapter.messageList = chatWebSocketClientService.getChat();
                adapter.notifyDataSetChanged();


                // register receiver
                chatMsgReceiver = new ChatMsgReceiver();
                IntentFilter filter = new IntentFilter(ChatWebSocketClientService.ACTION_UID_MESSAGE);
                registerReceiver(chatMsgReceiver, filter);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.e("Chat Service-Chat", "Service disconnect");
                binder = null;
                chatWebSocketClientService = null;
                client = null;
            }
        };

        Intent intent;
        intent = new Intent(this, ChatWebSocketClientService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private class ChatMsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Message message = intent.getParcelableExtra(ChatWebSocketClientService.EXTRA_MESSAGE);
            assert message != null;
            Log.e("Chat! Receiver-Chat", message.content);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        chatWebSocketClientService.resetUid();
        unregisterReceiver(chatMsgReceiver);
        unbindService(serviceConnection);
    }
}
