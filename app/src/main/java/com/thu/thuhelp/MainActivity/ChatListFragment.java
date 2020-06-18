package com.thu.thuhelp.MainActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thu.thuhelp.App;
import com.thu.thuhelp.ChatActivity.ChatActivity;
import com.thu.thuhelp.R;
import com.thu.thuhelp.Service.ChatWebSocketClient;
import com.thu.thuhelp.Service.ChatWebSocketClientService;
import com.thu.thuhelp.utils.ChatAbstract;
import com.thu.thuhelp.utils.ChatSession;
import com.thu.thuhelp.utils.ChatContent;
import com.thu.thuhelp.utils.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment {
    private final ChatSession chatSession = new ChatSession();
    private final LinkedList<ChatContent> chatContentList = new LinkedList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewChat;
    private ChatListAdapter adapter;

    private ChatWebSocketClient client;
    private ChatWebSocketClientService chatWebSocketClientService;
    private ChatWebSocketClientService.ChatWebSocketClientBinder binder;
    private ChatMsgReceiver chatMsgReceiver;

    private App app;
    private MainActivity activity;
    private File chatListFile;


    public static final String
            EXTRA_CHAT_CONTENT = "com.thu.thuhelp.MainActivity.MainFragment.extra.chat_content";

    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        assert activity != null;
        app = (App) activity.getApplication();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanced) {
        super.onViewCreated(view, savedInstanced);

        // set swipe refresh layout
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutChat);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    void setView() {
        // set recycler view
        recyclerViewChat = activity.findViewById(R.id.recyclerViewChat);
        adapter = new ChatListAdapter(activity, chatSession.abstracrList, app);

        recyclerViewChat.setAdapter(adapter);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewChat.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));

        chatListFile = new File(new File(activity.getFilesDir(), "chat"), "chat_list");
        if (!chatListFile.getParentFile().exists()) {
            chatListFile.getParentFile().mkdirs();
        }
        initChatList();
        bindService();

        adapter.setOnItemClickListener((view, position) -> {
            ChatAbstract chatAbstract = chatSession.abstracrList.get(position);
            ChatContent chatContent = chatSession.getContentByUid(chatAbstract.uid);
            Intent intent = new Intent(activity, ChatActivity.class);
            intent.putExtra(EXTRA_CHAT_CONTENT, chatContent);
            startActivity(intent);
        });
    }

    // bind service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("ChatFragment", "服务与活动成功绑定");
            binder = (ChatWebSocketClientService.ChatWebSocketClientBinder) service;
            chatWebSocketClientService = binder.getService();
            try {
                chatWebSocketClientService.setClient(app.getSkey());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            client = chatWebSocketClientService.client;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private void bindService() {
        Intent bindIntent;
        bindIntent = new Intent(activity, ChatWebSocketClientService.class);
        activity.bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        doRegisterReceiver();
    }

    // register receiver
    private class ChatMsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(ChatWebSocketClientService.EXTRA_MESSAGE);
            assert message != null;
            Log.e("ChatFragment", message);
            receiveMsg(message);
        }
    }

    private void doRegisterReceiver() {
        chatMsgReceiver = new ChatMsgReceiver();
        IntentFilter filter = new IntentFilter(ChatWebSocketClientService.ACTION_MESSAGE);
        activity.registerReceiver(chatMsgReceiver, filter);
    }

    private void receiveMsg(String message) {
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
            String message = jsonMsg.getString("messageText");
            String timeStamp = jsonMsg.getString("sendTime");

            ChatAbstract chatAbstract = chatSession.getAbstractByUid(uid);
            if (chatAbstract == null) {
                chatSession.abstracrList.addFirst(new ChatAbstract(uid, message, timeStamp));
                chatSession.contentList.addFirst(new ChatContent(uid, message, timeStamp, Message.TYPE_RECEIVED));
            } else {
                chatSession.abstracrList.remove(chatAbstract);
                chatAbstract.timeStamp = timeStamp;
                chatAbstract.lastMsg = message;
                chatSession.abstracrList.addFirst(chatAbstract);

                ChatContent chatContent = chatSession.getContentByUid(uid);
                if (chatContent == null) {
                    chatSession.contentList.addFirst(new ChatContent(uid, message, timeStamp, Message.TYPE_RECEIVED));
                } else {
                    chatContent.addMsg(message, timeStamp, Message.TYPE_RECEIVED);
                }
            }
            adapter.notifyDataSetChanged();
            saveChatList();
        } catch (
                JSONException e) {
            e.printStackTrace();
        }

    }

    private void saveChatList() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(chatListFile));
            for (ChatAbstract chatAbstract : chatSession.abstracrList) {
                oos.writeObject(chatAbstract);
            }
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getChatList() {
        try {
            LinkedList<ChatAbstract> list;
            FileInputStream fis = new FileInputStream(chatListFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            while (fis.available() > 0) {
                chatSession.abstracrList.add((ChatAbstract) ois.readObject());
            }
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initChatList() {
        getChatList();
//        ChatAbstract chatAbstract = new ChatAbstract("2a337d32-61a6-4088-97d4-2930ba2a634d",
//                "最后一条消息~", "0");
//        chatSession.abstracrList.addLast(chatAbstract);
//        chatAbstract = new ChatAbstract("8a343a55-bf02-42df-99d1-c764ea2d05cf",
//                "最最最最最最最最最最后一条消息息息息息息息息息息息息息息息息息息息息息息息息息息息息息息",
//                "0");
//        chatSession.abstracrList.addLast(chatAbstract);
//        adapter.notifyDataSetChanged();
    }
}
