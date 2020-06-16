package com.thu.thuhelp.DealActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.thu.thuhelp.App;
import com.thu.thuhelp.MainActivity.MissionListAdapter;
import com.thu.thuhelp.R;
import com.thu.thuhelp.utils.CommonInterface;
import com.thu.thuhelp.utils.Deal;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DealListActivity extends AppCompatActivity {
    private App app;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewDeal;
    private final LinkedList<Deal> dealList = new LinkedList<>();
    private MissionListAdapter adapter;
    private int clickedPosition;

    static private int
            REQUEST_INFO_PUBLISHED = 0,
            REQUEST_INFO_IN_PROGRESS = 1,
            REQUEST_INFO_COMPLETED = 2;
    public static final String
            EXTRA_DEAL_PUBLISHED = "com.thu.thuhelp.DealActivity.DealListActivity.extra.deal.published";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_list);

        app = (App) getApplication();

        // set return actionBar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        setView();
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
        // set swipe refresh layout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateDealList();
            }
        });

        // set recycler view
        recyclerViewDeal = findViewById(R.id.recyclerViewDeal);
        adapter = new MissionListAdapter(this, dealList);

        recyclerViewDeal.setAdapter(adapter);
        recyclerViewDeal.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDeal.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        updateDealList();

        adapter.setOnItemClickListener((view, position) -> {
            clickedPosition = position;
            Deal deal = dealList.get(position);
            Intent intent = new Intent(this, DealInfoActivity.class);
            intent.putExtra(EXTRA_DEAL_PUBLISHED, deal);
            startActivityForResult(intent, REQUEST_INFO_PUBLISHED);
        });
    }


    private void updateDealList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("skey", app.getSkey());
        params.put("state", "0");

        swipeRefreshLayout.setRefreshing(true);
        CommonInterface.sendOkHttpGetRequest("/user/deal/list", params, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("error", e.toString());
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String resStr = response.body().string();
                Log.e("response", resStr);
                try {
                    JSONObject res = new JSONObject(resStr);
                    int statusCode = res.getInt("status");
                    if (statusCode == 200) {
                        runOnUiThread(() -> Toast.makeText(DealListActivity.this, R.string.get_deal_list_success, Toast.LENGTH_SHORT).show());

                        JSONArray jsonDealList = res.getJSONArray("data");
                        dealList.clear();
                        int dealListSize = jsonDealList.length();
                        for (int i = 0; i < dealListSize; ++i) {
                            JSONObject jsonDeal = jsonDealList.getJSONObject(i);
                            Deal deal = new Deal(jsonDeal);
                            dealList.addLast(deal);
                        }
                        runOnUiThread(() -> adapter.dealList = dealList);
                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                    } else {
                        runOnUiThread(() -> Toast.makeText(DealListActivity.this, R.string.get_deal_list_fail, Toast.LENGTH_SHORT).show());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == REQUEST_INFO_PUBLISHED) {
                Toast.makeText(this, R.string.delete_deal_success, Toast.LENGTH_SHORT).show();
                dealList.remove(clickedPosition);
                adapter.notifyDataSetChanged();
                updateDealList();
            }
        }

    }
}
