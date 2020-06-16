package com.thu.thuhelp.DealActivity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.thu.thuhelp.App;
import com.thu.thuhelp.MainActivity.MainFragment;
import com.thu.thuhelp.R;
import com.thu.thuhelp.utils.CommonInterface;
import com.thu.thuhelp.utils.Deal;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DealInfoActivity extends AppCompatActivity {
    private App app;
    private Deal deal;
    private TextView
            textViewShowTitle,
            textViewShowDescription,
            textViewShowName,
            textViewShowPhone,
            textViewShowAddress,
            textViewShowBonus,
            textViewShowStartTime,
            textViewShowEndTime;
    private Button button;

    private enum Type {
        Accept, Delete;
    }

    private Type type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_info);

        app = (App) getApplication();

        // set return actionBar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if ((deal = intent.getParcelableExtra(MainFragment.EXTRA_DEAL)) != null) {
            type = Type.Accept;
            String buttonText = "接收任务";
            setView(buttonText);
        } else if ((deal = intent.getParcelableExtra(DealListActivity.EXTRA_DEAL_PUBLISHED)) != null) {
            type = Type.Delete;
            String buttonText = "删除任务";
            setView(buttonText);
        }
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

    private void setView(String buttonText) {
        textViewShowTitle = findViewById(R.id.textViewShowTitle);
        textViewShowDescription = findViewById(R.id.textViewShowDescription);
        textViewShowName = findViewById(R.id.textViewShowName);
        textViewShowPhone = findViewById(R.id.textViewShowPhone);
        textViewShowAddress = findViewById(R.id.textViewShowAddress);
        textViewShowBonus = findViewById(R.id.textViewShowBonus);
        textViewShowStartTime = findViewById(R.id.textViewShowStartTime);
        textViewShowEndTime = findViewById(R.id.textViewShowEndTime);
        button = findViewById(R.id.button);

        textViewShowTitle.setText(deal.title);
        textViewShowDescription.setText(deal.description);
        textViewShowName.setText(deal.name);
        textViewShowPhone.setText(deal.phone);
        textViewShowAddress.setText(deal.address);
        textViewShowBonus.setText(String.valueOf(deal.bonus));
        textViewShowStartTime.setText(deal.startTime);
        textViewShowEndTime.setText(deal.endTime);
        button.setText(buttonText);
    }

    public void onButtonClick(View view) {
        if (type == Type.Accept) {
            onAcceptClick();
        } else if (type == Type.Delete) {
            onDeletetClick();
        }
    }

    private void onAcceptClick() {
        HashMap<String, String> params = new HashMap<>();
        params.put("skey", app.getSkey());
        params.put("did", deal.did);
        CommonInterface.sendOkHttpGetRequest("/user/deal/accept", params, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("error", e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String resStr = response.body().string();
                Log.e("response", resStr);
                try {
                    JSONObject res = new JSONObject(resStr);
                    int statusCode = res.getInt("status");
                    if (statusCode == 200) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        runOnUiThread(() -> Toast.makeText(DealInfoActivity.this, R.string.accept_deal_fail, Toast.LENGTH_SHORT).show());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void onDeletetClick() {
        HashMap<String, String> params = new HashMap<>();
        params.put("skey", app.getSkey());
        params.put("did", deal.did);
        CommonInterface.sendOkHttpGetRequest("/user/deal/delete", params, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("error", e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String resStr = response.body().string();
                Log.e("response", resStr);
                try {
                    JSONObject res = new JSONObject(resStr);
                    int statusCode = res.getInt("status");
                    if (statusCode == 200) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        runOnUiThread(() -> Toast.makeText(DealInfoActivity.this, R.string.delete_deal_fail, Toast.LENGTH_SHORT).show());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
