package com.thu.thuhelp.DealActivity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.thu.thuhelp.MainActivity.MainFragment;
import com.thu.thuhelp.R;
import com.thu.thuhelp.utils.Deal;

public class DealInfoActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_info);

        Intent intent = getIntent();
        deal = intent.getParcelableExtra(MainFragment.EXTRA_DEAL);

        setView();

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

    private void setView() {
        textViewShowTitle = findViewById(R.id.textViewShowTitle);
        textViewShowDescription = findViewById(R.id.textViewShowDescription);
        textViewShowName = findViewById(R.id.textViewShowName);
        textViewShowPhone = findViewById(R.id.textViewShowPhone);
        textViewShowAddress = findViewById(R.id.textViewShowAddress);
        textViewShowBonus = findViewById(R.id.textViewShowBonus);
        textViewShowStartTime = findViewById(R.id.textViewShowStartTime);
        textViewShowEndTime = findViewById(R.id.textViewShowEndTime);

        textViewShowTitle.setText(deal.title);
        textViewShowDescription.setText(deal.description);
        textViewShowName.setText(deal.name);
        textViewShowPhone.setText(deal.phone);
        textViewShowAddress.setText(deal.address);
        textViewShowBonus.setText(String.valueOf(deal.bonus));
        textViewShowStartTime.setText(deal.startTime);
        textViewShowEndTime.setText(deal.endTime);
    }
}
