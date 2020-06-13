package com.thu.thuhelp.DealActivity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thu.thuhelp.R;

public class PushlishDealActivity extends AppCompatActivity {

    private enum TimeType {
        start, end, none;
    }

    private TimeType timeType = TimeType.none;
    private String stringTimeMessage, stringStartTime, stringEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushlish_deal);

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
                editTextAddress = findViewById(R.id.editTextAddress),
                editTextBonus = findViewById(R.id.editTextBonus);
        String stringTitle = editTextTitle.getText().toString(),
                stringDescription = editTextDescription.getText().toString(),
                stringAddress = editTextAddress.getText().toString(),
                stringBonus = editTextBonus.getText().toString();
        if (stringTitle.equals("") || stringDescription.equals("") || stringAddress.equals("") || stringBonus.equals("")) {
            Toast.makeText(this, R.string.toast_fill_publish_message, Toast.LENGTH_SHORT).show();
        } else {
            if (stringStartTime == null || stringEndTime == null || stringEndTime.compareTo(stringStartTime) <= 0) {
                Toast.makeText(this, R.string.toast_publish_time_error, Toast.LENGTH_SHORT).show();
            } else {
                stringStartTime += ":00";
                stringEndTime += ":00";
                String message = "Title: " + stringTitle + "\n Description: " + stringDescription +
                        "\n Address: " + stringAddress + "\n Bouns: " + stringBonus +
                        "\n StartTime: " + stringStartTime + "\n EndTime: " + stringEndTime;
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }

    }
}
