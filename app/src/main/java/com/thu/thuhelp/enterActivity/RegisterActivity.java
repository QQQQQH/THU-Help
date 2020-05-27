package com.thu.thuhelp.enterActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

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

public class RegisterActivity extends AppCompatActivity {
    static public String EXTRA_ID = "studentId", EXTRA_PW = "password";
    private EditText editTextSid, editTextPassword, editTextNickname;
    private RadioButton radioButtonMale;
    private String sid, password, nickname, gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editTextSid = findViewById(R.id.editTextsid);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextNickname = findViewById(R.id.editTextNickname);
        radioButtonMale = findViewById(R.id.radioButtonMale);
    }

    public void onRegistClick(View view) {
        sid = editTextSid.getText().toString();
        password = editTextPassword.getText().toString();
        nickname = editTextNickname.getText().toString();
        gender = radioButtonMale.isChecked() ? "0" : "1";
        HashMap<String, String> params = new HashMap<>();
        params.put("sid", sid);
        params.put("password", password);
        params.put("nickname", nickname);
        params.put("gender", gender);
        CommonInterface.sendOkHttpPostRequest("/user/account/register", params, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("error", e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String resStr = response.body().string();
//                RegisterActivity.this.runOnUiThread(() -> Toast.makeText(RegisterActivity.this, resStr, Toast.LENGTH_LONG).show());
                Log.e("response", resStr);
                try {
                    JSONObject jsonObject = new JSONObject(resStr);
                    int statusCode = jsonObject.getInt("status");
                    Intent intent = new Intent();
                    if (statusCode == 200) {
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        RegisterActivity.this.runOnUiThread(() -> Toast.makeText(RegisterActivity.this, resStr, Toast.LENGTH_LONG).show());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
