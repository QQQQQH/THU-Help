package com.thu.thuhelp.MainActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.thu.thuhelp.App;
import com.thu.thuhelp.DealActivity.PublishDealActivity;
import com.thu.thuhelp.R;
import com.thu.thuhelp.utils.CommonInterface;
import com.thu.thuhelp.utils.Deal;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    private final LinkedList<Deal> dealList = new LinkedList<>();
    private RecyclerView recyclerViewDeal;
    private MissionListAdapter adapter;
    private App app;
    private MainActivity activity;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = (MainActivity) getActivity();
        assert activity != null;
        app = (App) activity.getApplication();


        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanced) {
        super.onViewCreated(view, savedInstanced);

        // set fab
        view.findViewById(R.id.fabPublishDeal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (app.get_skey() == null) {
                    Toast.makeText(activity, R.string.please_login, Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(activity, PublishDealActivity.class);
                    startActivity(intent);
                }
            }
        });

        SwipeRefreshLayout swipeRefreshLayout = activity.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDealList();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        setRecyclerView(view);

    }

    private void setRecyclerView(View view) {
        getDealList();

        // set recycler view
        recyclerViewDeal = view.findViewById(R.id.recyclerViewDeal);
        adapter = new MissionListAdapter(view.getContext(), dealList);

        recyclerViewDeal.setAdapter(adapter);
        recyclerViewDeal.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerViewDeal.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void getDealList() {
        HashMap<String, String> params = new HashMap<>();
        String url = "/user/deal/list?" + "skey=" + app.get_skey();
        CommonInterface.sendOkHttpGetRequest(url, new Callback() {
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
                        activity.runOnUiThread(() -> Toast.makeText(activity, R.string.get_deal_list_success, Toast.LENGTH_LONG).show());

                        JSONArray jsonDealList = res.getJSONArray("data");
                        dealList.clear();
                        int dealListSize = jsonDealList.length();
                        for (int i = 0; i < dealListSize; ++i) {
                            JSONObject jsonDeal = jsonDealList.getJSONObject(i);
                            Deal deal = new Deal(jsonDeal.getString("address"),
                                    jsonDeal.getDouble("bonus"),
                                    jsonDeal.getString("description"),
                                    jsonDeal.getString("title"),
                                    jsonDeal.getString("phone"),
                                    jsonDeal.getString("name"),
                                    jsonDeal.getString("startTime"),
                                    jsonDeal.getString("endTime"));
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                            DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                            try {
                                deal.startTime = df2.format(df.parse(deal.startTime));
                                deal.endTime = df2.format(df.parse(deal.endTime));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            dealList.addLast(deal);
                        }
                        if (adapter != null) {
                            activity.runOnUiThread(() -> adapter.dealList = dealList);
                            activity.runOnUiThread(() -> adapter.notifyDataSetChanged());
                        }
                    } else {
                        activity.runOnUiThread(() -> Toast.makeText(activity, resStr, Toast.LENGTH_LONG).show());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
