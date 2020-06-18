package com.thu.thuhelp.MainActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.ParcelUuid;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thu.thuhelp.App;
import com.thu.thuhelp.R;
import com.thu.thuhelp.utils.ChatAbstract;
import com.thu.thuhelp.utils.ChatContent;

import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    private final LinkedList<ChatAbstract> chatAbstractList = new LinkedList<>();
    private final LinkedList<ChatContent> chatContentList = new LinkedList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewDeal;
    private ChatListAdapter adapter;

    private App app;
    private MainActivity activity;

    public ChatFragment() {
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

    public void setView() {
        // set recycler view
        recyclerViewDeal = activity.findViewById(R.id.recyclerViewChat);
        adapter = new ChatListAdapter(activity, chatAbstractList, app);

        recyclerViewDeal.setAdapter(adapter);
        recyclerViewDeal.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewDeal.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));

        initChatList();
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
