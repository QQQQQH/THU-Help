package com.thu.thuhelp.DealActivity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thu.thuhelp.App;
import com.thu.thuhelp.ChatActivity.ChatActivity;
import com.thu.thuhelp.MainActivity.ChatListFragment;
import com.thu.thuhelp.MainActivity.MainFragment;
import com.thu.thuhelp.R;
import com.thu.thuhelp.utils.CommonInterface;
import com.thu.thuhelp.utils.Deal;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DealInfoActivity extends AppCompatActivity {
    private App app;
    private Deal deal;
    private ImageView imageViewAvatar;
    private TextView
            textViewShowTitle,
            textViewShowDescription,
            textViewShowName,
            textViewShowPhone,
            textViewShowAddress,
            textViewShowBonus,
            textViewShowStartTime,
            textViewShowEndTime;
    private Button buttonPM, button1, button2;

    private int dealType;

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
        deal = intent.getParcelableExtra(DealListFragment.EXTRA_DEAL);
        dealType = intent.getIntExtra(DealListFragment.EXTRA_DEAL_TYPE, DealListFragment.DEAL_ALL_PUBLISH);

        imageViewAvatar = findViewById(R.id.imageViewAvatar);
        buttonPM = findViewById(R.id.buttonPM);
        File avatarFile = new File(new File(getFilesDir(), "images"), "avatar" + deal.initiator + ".jpg");
        try {
            Bitmap avatar = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.fromFile(avatarFile)));
            imageViewAvatar.setImageBitmap(avatar);
        } catch (FileNotFoundException e) {
            imageViewAvatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_circle));
            e.printStackTrace();
        }

        String buttonText1 = null, buttonText2 = null;

        switch (dealType) {
            case DealListFragment.DEAL_ALL_PUBLISH:
                buttonText1 = "接收任务";
                break;
            case DealListFragment.DEAL_MY_PUBLISH:
                buttonText1 = "删除任务";
                break;
            case DealListFragment.DEAL_OTHERS_ACCEPT:
                buttonText1 = "完成任务";
                buttonText2 = "放弃任务";
                break;
            case DealListFragment.DEAL_MY_CONFIRM:
                buttonText1 = "确认完成";
                break;
        }

        setView(buttonText1, buttonText2);
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

    private void setView(String buttonText1, String buttonText2) {
        textViewShowTitle = findViewById(R.id.textViewShowTitle);
        textViewShowDescription = findViewById(R.id.textViewShowDescription);
        textViewShowName = findViewById(R.id.textViewShowName);
        textViewShowPhone = findViewById(R.id.textViewShowPhone);
        textViewShowAddress = findViewById(R.id.textViewShowAddress);
        textViewShowBonus = findViewById(R.id.textViewShowBonus);
        textViewShowStartTime = findViewById(R.id.textViewShowStartTime);
        textViewShowEndTime = findViewById(R.id.textViewShowEndTime);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);

        textViewShowTitle.setText(deal.title);
        textViewShowDescription.setText(deal.description);
        textViewShowName.setText(deal.name);
        textViewShowPhone.setText(deal.phone);
        textViewShowAddress.setText(deal.address);
        textViewShowBonus.setText(String.valueOf(deal.bonus));
        textViewShowStartTime.setText(deal.startTime);
        textViewShowEndTime.setText(deal.endTime);

        if (buttonText1 != null) {
            button1.setText(buttonText1);
            button1.setVisibility(View.VISIBLE);
            if (buttonText2 != null) {
                button2.setText(buttonText2);
                button2.setVisibility(View.VISIBLE);
            }
        }
    }


    public void onPMClick(View view) {
        Intent newIntent = new Intent(this, ChatActivity.class);
        newIntent.putExtra(ChatListFragment.EXTRA_CHAT_UID, deal.initiator);
        startActivity(newIntent);
    }

    public void onButton1Click(View view) {
        switch (dealType) {
            case DealListFragment.DEAL_ALL_PUBLISH:
                onAcceptClick();
                break;
            case DealListFragment.DEAL_MY_PUBLISH:
                onDeletetClick();
                break;
            case DealListFragment.DEAL_OTHERS_ACCEPT:
                onFinishClick();
                break;
            case DealListFragment.DEAL_MY_CONFIRM:
                onConfirmClick();
                break;
        }
    }

    public void onButton2Click(View view) {
        onGiveUpClick();
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
                        runOnUiThread(() -> Toast.makeText(app, R.string.delete_deal_success, Toast.LENGTH_SHORT).show());
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        runOnUiThread(() -> Toast.makeText(app, R.string.delete_deal_fail, Toast.LENGTH_SHORT).show());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void onFinishClick() {
        HashMap<String, String> params = new HashMap<>();
        params.put("skey", app.getSkey());
        params.put("did", deal.did);
        CommonInterface.sendOkHttpGetRequest("/user/deal/confirm", params, new Callback() {
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
                        runOnUiThread(() -> Toast.makeText(DealInfoActivity.this, R.string.finish_deal_success, Toast.LENGTH_SHORT).show());
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        runOnUiThread(() -> Toast.makeText(DealInfoActivity.this, R.string.finish_deal_fail, Toast.LENGTH_SHORT).show());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void onConfirmClick() {
        HashMap<String, String> params = new HashMap<>();
        params.put("skey", app.getSkey());
        params.put("did", deal.did);
        CommonInterface.sendOkHttpGetRequest("/user/deal/confirm", params, new Callback() {
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
                        runOnUiThread(() -> Toast.makeText(DealInfoActivity.this, R.string.confirm_deal_success, Toast.LENGTH_SHORT).show());
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        runOnUiThread(() -> Toast.makeText(DealInfoActivity.this, R.string.confirm_deal_fail, Toast.LENGTH_SHORT).show());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void onGiveUpClick() {
        HashMap<String, String> params = new HashMap<>();
        params.put("skey", app.getSkey());
        params.put("did", deal.did);
        CommonInterface.sendOkHttpGetRequest("/user/deal/giveup", params, new Callback() {
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
                        runOnUiThread(() -> Toast.makeText(DealInfoActivity.this, R.string.giveup_deal_success, Toast.LENGTH_SHORT).show());
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        runOnUiThread(() -> Toast.makeText(DealInfoActivity.this, R.string.giveup_deal_fail, Toast.LENGTH_SHORT).show());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
