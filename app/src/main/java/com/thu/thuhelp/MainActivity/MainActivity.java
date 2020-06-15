package com.thu.thuhelp.MainActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.thu.thuhelp.App;
import com.thu.thuhelp.R;
import com.thu.thuhelp.utils.CommonInterface;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    private App app;
    private SharedPreferences sharedPreferences = null;
    private PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (App) getApplication();
        sharedPreferences = getSharedPreferences(
                getString(R.string.sharedPreFile_login), Context.MODE_PRIVATE);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.pager);

        pagerAdapter.addFragment(new MainFragment());
        pagerAdapter.addFragment(new MyFragment());

        viewPager.setAdapter(pagerAdapter);
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
                        case R.id.item_my_page:
                            viewPager.setCurrentItem(1);
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
                        // set skey
                        app.setSkey(jsonObject.getString("data"));
                        mainFragmentSetView();
                        myFragmentSetView();
                    } else {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, R.string.login_fail, Toast.LENGTH_SHORT).show());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void mainFragmentSetView() {
        MainFragment mainFragment = (MainFragment) (pagerAdapter.getItem(0));
        runOnUiThread(mainFragment::setView);
    }

    public void myFragmentSetView() {
        MyFragment myFragment = (MyFragment) (pagerAdapter.getItem(1));
        runOnUiThread(myFragment::setLoginView);
    }
}
