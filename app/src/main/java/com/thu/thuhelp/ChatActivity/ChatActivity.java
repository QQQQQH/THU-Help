package com.thu.thuhelp.ChatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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
import com.thu.thuhelp.utils.ChatAbstract;
import com.thu.thuhelp.utils.Message;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;


public class ChatActivity extends AppCompatActivity {
    private final LinkedList<Message> messageList = new LinkedList<>();
    private RecyclerView recyclerViewChat;
    private MessageListAdapter adapter;
    private EditText editTextMessage;
    private File avatarFile;
    private Bitmap rightAvatar;

    private App app;

    private ChatAbstract chatAbstract;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        app = (App) getApplication();
        setView();
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
        chatAbstract = intent.getParcelableExtra(ChatListFragment.EXTRA_CHAR_ABSTRACT);
        avatarFile = new File(new File(getFilesDir(), "images"), "avatar.jpg");
        try {
            rightAvatar = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.fromFile(avatarFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // set return actionBar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        editTextMessage = findViewById(R.id.editTextMessage);

        initMessage();

        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        adapter = new MessageListAdapter(this, messageList, rightAvatar, chatAbstract.uid, app);

        recyclerViewChat.setAdapter(adapter);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
    }

    private void initMessage() {
        Message msg1 = new Message("111", Message.TYPE_RECEIVED);
        messageList.addFirst(msg1);
        messageList.addFirst(msg1);
        messageList.addFirst(msg1);
        messageList.addFirst(msg1);
        messageList.addFirst(msg1);
        messageList.addFirst(msg1);
        Message msg2 = new Message("222", Message.TYPE_SEND);
        messageList.addFirst(msg2);
        messageList.addFirst(msg2);
        messageList.addFirst(msg2);
        messageList.addFirst(msg2);
        messageList.addFirst(msg2);
        messageList.addFirst(msg2);
        messageList.addFirst(msg2);
        messageList.addFirst(msg2);
        messageList.addFirst(msg2);
        messageList.addFirst(msg2);
        messageList.addFirst(msg2);
        Message msg3 = new Message("333", Message.TYPE_RECEIVED);
        messageList.addFirst(msg3);
        messageList.addFirst(msg3);
        messageList.addFirst(msg3);
        messageList.addFirst(msg3);

    }

    public void onClickSend(View view) {
        String content = editTextMessage.getText().toString();
        if (!content.equals("")) {
            Message message = new Message(content, Message.TYPE_SEND);
            messageList.addFirst(message);
            adapter.notifyDataSetChanged();
            editTextMessage.setText("");
            recyclerViewChat.smoothScrollToPosition(0);
        }
    }
}
