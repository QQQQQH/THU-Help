package com.thu.thuhelp.MainActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thu.thuhelp.App;
import com.thu.thuhelp.ChatActivity.ChatActivity;
import com.thu.thuhelp.R;
import com.thu.thuhelp.utils.ChatAbstract;
import com.thu.thuhelp.utils.ChatAbstractList;
import com.thu.thuhelp.utils.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewChat;
    private ChatListAdapter adapter;

    private App app;
    private MainActivity activity;

    public static final String
            EXTRA_CHAT_UID = "com.thu.thuhelp.MainActivity.MainFragment.extra.chat_uid";

    public ChatListFragment() {
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

    void setLoginView() {
        // set recycler view
        recyclerViewChat = activity.findViewById(R.id.recyclerViewChat);
        adapter = new ChatListAdapter(activity, new LinkedList<>(), app);

        recyclerViewChat.setAdapter(adapter);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewChat.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));

        adapter.setOnItemClickListener((view, position) -> {
            ChatAbstract chatAbstract = activity.chatList.get(position);
            Intent intent = new Intent(activity, ChatActivity.class);
            intent.putExtra(EXTRA_CHAT_UID, chatAbstract.uid);
            startActivity(intent);
        });
    }

    void setLogoutView() {
        updateView();
    }

    public void setChatList(LinkedList<ChatAbstract> chatList) {
        adapter.chatAbstractList = chatList;
        adapter.notifyDataSetChanged();

    }

    public void updateView() {
        adapter.notifyDataSetChanged();
    }
}
