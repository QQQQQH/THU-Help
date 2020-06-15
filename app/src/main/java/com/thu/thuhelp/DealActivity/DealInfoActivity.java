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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_info);

        Intent intent = getIntent();
        deal = intent.getParcelableExtra(MainFragment.EXTRA_DEAL);
        String did = deal.name;

        TextView textView = findViewById(R.id.textView);
        textView.setText(did);

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
}
