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
import com.thu.thuhelp.utils.ChatContent;

import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment {
    private final LinkedList<ChatAbstract> chatAbstractList = new LinkedList<>();
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

    public static final String
            EXTRA_CHAR_ABSTRACT = "com.thu.thuhelp.MainActivity.MainFragment.extra.chat_abstract",
            EXTRA_LEFT_AVATAR = "com.thu.thuhelp.MainActivity.MainFragment.extra.left_avatar";

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
        adapter = new ChatListAdapter(activity, chatAbstractList, app);

        recyclerViewChat.setAdapter(adapter);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewChat.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));

        initChatList();
        bindService();

        adapter.setOnItemClickListener((view, position) -> {
            ChatAbstract chatAbstract = chatAbstractList.get(position);
            Intent intent = new Intent(activity, ChatActivity.class);
            intent.putExtra(EXTRA_CHAR_ABSTRACT, chatAbstract);
            startActivity(intent);
        });
    }

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

    private static class ChatMsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(ChatWebSocketClientService.EXTRA_MESSAGE);
            assert message != null;
            Log.e("ChatFragment", message);
        }
    }

    private void doRegisterReceiver() {
        chatMsgReceiver = new ChatMsgReceiver();
        IntentFilter filter = new IntentFilter(ChatWebSocketClientService.ACTION_MESSAGE);
        activity.registerReceiver(chatMsgReceiver, filter);
    }

    private void initChatList() {
        ChatAbstract chatAbstract = new ChatAbstract("2a337d32-61a6-4088-97d4-2930ba2a634d",
                "SQC2", "最后一条消息~");
        chatAbstractList.addLast(chatAbstract);
        chatAbstract = new ChatAbstract("8a343a55-bf02-42df-99d1-c764ea2d05cf",
                "SQC", "最最最最最最最最最最后一条消息息息息息息息息息息息息息息息息息息息息息息息息息息息息息息");
        chatAbstractList.addLast(chatAbstract);
        adapter.notifyDataSetChanged();
    }
}
