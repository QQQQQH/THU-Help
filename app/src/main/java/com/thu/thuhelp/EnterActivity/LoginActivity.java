package com.thu.thuhelp.EnterActivity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

public class LoginActivity extends AppCompatActivity {
    //    public static final String EXTRA_SKEY = "com.thu.thuhelp.SKEY",
//            EXTRA_STUDENT_ID = "com.thu.thuhelp.STUDENT_ID",
//            EXTRA_PASSWORD = "com.thu.thuhelp.PASSWORD";
    private EditText editTextSid, editTextPassword;
    private String sid, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextSid = findViewById(R.id.editTextsid);
        editTextPassword = findViewById(R.id.editTextPassword);

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


    public void onLoginClick(View view) {
        sid = editTextSid.getText().toString();
        password = editTextPassword.getText().toString();
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
                        Intent intent = new Intent();

                        // set skey
                        App app = (App) getApplication();
                        app.setSkey(jsonObject.getString("data"));
                        SharedPreferences sharedPreferences = getSharedPreferences(
                                getString(R.string.sharedPreFile_login), Context.MODE_PRIVATE);

                        // save sid and password
                        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
                        preferencesEditor.putString(getString(R.string.student_id), sid);
                        preferencesEditor.putString(getString(R.string.password), password);
                        preferencesEditor.apply();

                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        LoginActivity.this.runOnUiThread(() -> Toast.makeText(LoginActivity.this, resStr, Toast.LENGTH_SHORT).show());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
