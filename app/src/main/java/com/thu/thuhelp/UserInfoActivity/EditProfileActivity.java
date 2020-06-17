package com.thu.thuhelp.UserInfoActivity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
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

public class EditProfileActivity extends AppCompatActivity {
    private EditText editTextNickname, editTextPassword;
    private RadioButton radioButtonMale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editTextNickname = findViewById(R.id.editTextNickname);
        editTextPassword = findViewById(R.id.editTextPassword);
        radioButtonMale = findViewById(R.id.radioButtonMale);

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

    public void onEditClick(View view) {
        String nickname = editTextNickname.getText().toString();
        String password = editTextPassword.getText().toString();
        String gender = radioButtonMale.isChecked() ? "0" : "1";
        HashMap<String, String> params = new HashMap<>();
        params.put("skey", ((App) getApplication()).getSkey());
        params.put("nickname", nickname);
        params.put("password", password);
        params.put("gender", gender);
        CommonInterface.sendOkHttpPostRequest("/user/account/edit", params, new Callback() {
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
                    Intent intent = new Intent();
                    if (statusCode == 200) {
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        runOnUiThread(() -> Toast.makeText(EditProfileActivity.this, R.string.edit_profile_fail, Toast.LENGTH_SHORT).show());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
