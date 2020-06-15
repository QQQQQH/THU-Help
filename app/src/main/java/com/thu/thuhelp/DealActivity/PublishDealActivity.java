package com.thu.thuhelp.DealActivity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thu.thuhelp.App;
import com.thu.thuhelp.MainActivity.MainActivity;
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

public class PublishDealActivity extends AppCompatActivity {

    private enum TimeType {
        start, end, none;
    }

    private TimeType timeType = TimeType.none;
    private String stringTimeMessage, stringStartTime, stringEndTime;
    private App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_deal);

        // set return actionBar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        app = (App) getApplication();
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


    public void onStartTimeClick(View view) {
        timeType = TimeType.start;
        showDatePicker();
    }

    public void onEndTimeClick(View view) {
        timeType = TimeType.end;
        showDatePicker();
    }

    private void showDatePicker() {
        DialogFragment dialogFragment = new DatePickerFragment();
        dialogFragment.show(getSupportFragmentManager(), "date");
    }

    private void showTimePicker() {
        DialogFragment dialogFragment = new TimePickerFragment();
        dialogFragment.show(getSupportFragmentManager(), "time");
    }

    @SuppressLint("DefaultLocale")
    public void setDatePickerRes(int year, int month, int day) {
        String stringYear = String.format("%04d", year);
        String stringMonth = String.format("%02d", month + 1);
        String stringDay = String.format("%02d", day);
        stringTimeMessage = stringYear + "-" + stringMonth + "-" + stringDay;
        showTimePicker();
    }

    @SuppressLint("DefaultLocale")
    public void setTimePickerRes(int hour, int minute) {
        String stringHour = String.format("%02d", hour);
        String stringMinute = String.format("%02d", minute);
        stringTimeMessage += " " + stringHour + ":" + stringMinute;
        TextView textViewShowTime;
        if (timeType == TimeType.start) {
            textViewShowTime = findViewById(R.id.textViewShowStartTime);
            stringStartTime = stringTimeMessage;
        } else {
            textViewShowTime = findViewById(R.id.textViewShowEndTime);
            stringEndTime = stringTimeMessage;
        }
        textViewShowTime.setText(stringTimeMessage);
    }

    public void onSubmitClick(View view) {
        EditText editTextTitle = findViewById(R.id.editTextTitle),
                editTextDescription = findViewById(R.id.editTextDescription),
                editTextName = findViewById(R.id.editTextName),
                editTextPhone = findViewById(R.id.editTextPhone),
                editTextAddress = findViewById(R.id.editTextAddress),
                editTextBonus = findViewById(R.id.editTextBonus);
        String stringTitle = editTextTitle.getText().toString(),
                stringDescription = editTextDescription.getText().toString(),
                stringName = editTextName.getText().toString(),
                stringPhone = editTextPhone.getText().toString(),
                stringAddress = editTextAddress.getText().toString(),
                stringBonus = editTextBonus.getText().toString();
        if (stringTitle.equals("") || stringDescription.equals("") || stringName.equals("") ||
                stringPhone.equals("") || stringAddress.equals("") || stringBonus.equals("") ||
                stringStartTime == null || stringEndTime == null
        ) {
            Toast.makeText(this, R.string.toast_fill_publish_message, Toast.LENGTH_SHORT).show();
        } else {
            if (stringEndTime.compareTo(stringStartTime) <= 0) {
                Toast.makeText(this, R.string.toast_publish_time_error, Toast.LENGTH_SHORT).show();
            } else {
                stringStartTime += ":00";
                stringEndTime += ":00";

                HashMap<String, String> params = new HashMap<>();
                params.put("skey", app.get_skey());
                params.put("title", stringTitle);
                params.put("description", stringDescription);
                params.put("name", stringName);
                params.put("phone", stringPhone);
                params.put("address", stringAddress);
                params.put("bonus", stringBonus);
                params.put("start_time", stringStartTime);
                params.put("end_time", stringEndTime);
                CommonInterface.sendOkHttpPostRequest("/user/deal/publish", params, new Callback() {
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
                                runOnUiThread(() -> Toast.makeText(PublishDealActivity.this, R.string.publish_success, Toast.LENGTH_SHORT).show());
                            } else {
                                runOnUiThread(() -> Toast.makeText(PublishDealActivity.this, resStr, Toast.LENGTH_SHORT).show());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

    }
}
