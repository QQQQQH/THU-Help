package com.thu.thuhelp.ChatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thu.thuhelp.R;
import com.thu.thuhelp.utils.Message;

import java.util.LinkedList;


public class ChatActivity extends AppCompatActivity {
    private final LinkedList<Message> messageList = new LinkedList<>();
    private RecyclerView recyclerViewChat;
    private MessageListAdapter adapter;
    private EditText editTextMessage;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        editTextMessage = findViewById(R.id.editTextMessage);

        initMessage();

        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        adapter = new MessageListAdapter(this, messageList);

        recyclerViewChat.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChat.smoothScrollToPosition(messageList.size());

        // set return actionBar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
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

    private void initMessage() {
        Message msg1 = new Message("111", Message.TYPE_RECEIVED);
        messageList.addLast(msg1);
        messageList.addLast(msg1);
        messageList.addLast(msg1);
        messageList.addLast(msg1);
        messageList.addLast(msg1);
        messageList.addLast(msg1);
        Message msg2 = new Message("222", Message.TYPE_SEND);
        messageList.addLast(msg2);
        messageList.addLast(msg2);
        messageList.addLast(msg2);
        messageList.addLast(msg2);
        messageList.addLast(msg2);
        messageList.addLast(msg2);
        messageList.addLast(msg2);
        messageList.addLast(msg2);
        messageList.addLast(msg2);
        messageList.addLast(msg2);
        messageList.addLast(msg2);
        messageList.addLast(msg1);
        messageList.addLast(msg1);
        messageList.addLast(msg1);
        messageList.addLast(msg1);

    }

    public void onClickSend(View view) {
        String content = editTextMessage.getText().toString();
        if (!content.equals("")) {
            Message message = new Message(content, Message.TYPE_SEND);
            messageList.addLast(message);
            adapter.notifyDataSetChanged();
            editTextMessage.setText("");
            recyclerViewChat.smoothScrollToPosition(messageList.size());
        }
    }
}
