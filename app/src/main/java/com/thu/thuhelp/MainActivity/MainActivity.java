package com.thu.thuhelp.MainActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.thu.thuhelp.App;
import com.thu.thuhelp.R;
import com.thu.thuhelp.Service.ChatWebSocketClient;
import com.thu.thuhelp.Service.ChatWebSocketClientService;
import com.thu.thuhelp.utils.ChatAbstract;
import com.thu.thuhelp.utils.CommonInterface;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    private App app;
    private SharedPreferences sharedPreferences = null;
    private PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());

    private MainFragment mainFragment = new MainFragment();
    private ChatListFragment chatListFragment = new ChatListFragment();
    private MyFragment myFragment = new MyFragment();

    private ServiceConnection serviceConnection;
    private ChatWebSocketClient client;
    private ChatWebSocketClientService chatWebSocketClientService;
    private ChatWebSocketClientService.ChatWebSocketClientBinder binder;
    private ChatMsgReceiver chatMsgReceiver;

    public LinkedList<ChatAbstract> chatList = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (App) getApplication();
        sharedPreferences = getSharedPreferences(
                getString(R.string.sharedPreFile_login), Context.MODE_PRIVATE);
        app.setUid(sharedPreferences.getString(getString(R.string.uid), null));

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.pager);

        pagerAdapter.addFragment(mainFragment);
        pagerAdapter.addFragment(chatListFragment);
        pagerAdapter.addFragment(myFragment);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.item_main_page:
                            viewPager.setCurrentItem(0);
                            return true;
                        case R.id.item_chat_page:
                            viewPager.setCurrentItem(1);
                            return true;
                        case R.id.item_my_page:
                            viewPager.setCurrentItem(2);
                            return true;
                    }
                    return false;
                }
        );
        new Thread() {
            @Override
            public void run() {
                if (app.getSkey() == null &&
                        sharedPreferences.getString(getString(R.string.student_id), null) != null &&
                        sharedPreferences.getString(getString(R.string.password), null) != null) {
                    autoLogin();
                }
            }
        }.start();
    }

    private void autoLogin() {
        String sid = sharedPreferences.getString(getString(R.string.student_id), ""),
                password = sharedPreferences.getString(getString(R.string.password), "");
        HashMap<String, String> params = new HashMap<>();
        params.put("sid", sid);
        params.put("password", password);
        CommonInterface.sendOkHttpPostRequest("/user/account/login", params, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("error", e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String resStr = response.body().string();
                Log.e("response", resStr);
                try {
                    JSONObject jsonObject = new JSONObject(resStr);
                    int statusCode = jsonObject.getInt("status");
                    if (statusCode == 200) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show());

                        // set skey and uid
                        JSONObject data = jsonObject.getJSONObject("data");
                        String uid = data.getString("uid");
                        App app = (App) getApplication();
                        app.setUid(uid);
                        app.setSkey(data.getString("skey"));
                        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
                        preferencesEditor.putString(getString(R.string.uid), uid);
                        preferencesEditor.apply();

                        runOnUiThread(() -> onLogin());
                    } else {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, R.string.login_fail, Toast.LENGTH_SHORT).show());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setUid(String uid) {
        app.setUid(uid);
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        preferencesEditor.putString(getString(R.string.uid), uid);
        preferencesEditor.apply();
    }

    public void onLogout() {
        chatWebSocketClientService.disConnectClient();
        unbindService(serviceConnection);
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        preferencesEditor.clear();
        preferencesEditor.apply();
        app.setSkey(null);
        app.setUid(null);

        myFragment.setLogoutView();
        mainFragment.setLogoutView();
        chatListFragment.setLogoutView();
    }


    public void onLogin() {
        myFragment.setLoginView();
        mainFragment.setLoginView();
        chatListFragment.setLoginView();
        setService();
    }

    private void setService() {
        // bind service
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.e("Chat Service-Main", "Service connect");
                binder = (ChatWebSocketClientService.ChatWebSocketClientBinder) service;
                chatWebSocketClientService = binder.getService();

                // connect client
                chatWebSocketClientService.connectClient(app.getSkey(), app.getDir(),null);
                client = chatWebSocketClientService.client;
                chatList = chatWebSocketClientService.chatList;
                chatListFragment.setChatList(chatList);

                // register receiver
                chatMsgReceiver = new ChatMsgReceiver();
                IntentFilter filter = new IntentFilter(ChatWebSocketClientService.ACTION_ALL_MESSAGE);
                registerReceiver(chatMsgReceiver, filter);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.e("Chat Service-Main", "Service disconnect");
                binder = null;
                chatWebSocketClientService = null;
                client = null;
                chatList = new LinkedList<>();
            }
        };

        Intent intent;
        intent = new Intent(this, ChatWebSocketClientService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private class ChatMsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(ChatWebSocketClientService.EXTRA_MESSAGE);
            assert message != null;
            Log.e("Chat Receiver-Main", message);
            chatListFragment.updateView();
        }
    }
}
