package com.thu.thuhelp.MainActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.thu.thuhelp.App;
import com.thu.thuhelp.ChatActivity.ChatActivity;
import com.thu.thuhelp.DealActivity.DealInfoActivity;
import com.thu.thuhelp.DealActivity.DealListFragment;
import com.thu.thuhelp.DealActivity.PublishDealActivity;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    private final LinkedList<Deal> dealList = new LinkedList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewDeal;
    private MissionListAdapter adapter;
    private SearchView searchView;

    private App app;
    private MainActivity activity;
    private int clickedPosition;

    static private int
            REQUEST_PUBLISH = 0,
            REQUEST_INFO = 1;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        assert activity != null;
        app = (App) activity.getApplication();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanced) {
        super.onViewCreated(view, savedInstanced);
        swipeRefreshLayout = activity.findViewById(R.id.swipeRefreshLayoutDeal);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(activity, R.string.please_login, Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    public void setLoginView() {
        // set fab
        activity.findViewById(R.id.fabPublishDeal).setOnClickListener(v -> {
            Intent intent = new Intent(activity, PublishDealActivity.class);
            startActivityForResult(intent, REQUEST_PUBLISH);
        });

        // set swipe refresh layout
        swipeRefreshLayout.setOnRefreshListener(this::updateDealList);
        setRecyclerView();

        // set query
        searchView = activity.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchDeal(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnCloseListener(() -> {
            adapter.dealList = dealList;
            adapter.notifyDataSetChanged();
            return false;
        });
    }

    public void setLogoutView() {

    }

    private void setRecyclerView() {

        // set recycler view
        recyclerViewDeal = activity.findViewById(R.id.recyclerViewDeal);
        adapter = new MissionListAdapter(activity, dealList, app);

        recyclerViewDeal.setAdapter(adapter);
        recyclerViewDeal.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewDeal.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));

        updateDealList();

        adapter.setOnItemClickListener((view, position) -> {
            clickedPosition = position;
            Deal deal = adapter.dealList.get(position);
            Intent intent = new Intent(activity, DealInfoActivity.class);
            intent.putExtra(DealListFragment.EXTRA_DEAL, deal);
            startActivityForResult(intent, REQUEST_INFO);
        });
    }

    private void updateDealList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("skey", app.getSkey());

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
//                        activity.runOnUiThread(() -> Toast.makeText(activity, R.string.get_deal_list_success, Toast.LENGTH_SHORT).show());

                        JSONArray jsonDealList = res.getJSONArray("data");
                        dealList.clear();
                        int dealListSize = jsonDealList.length();
                        for (int i = 0; i < dealListSize; ++i) {
                            JSONObject jsonDeal = jsonDealList.getJSONObject(i);
                            Deal deal = new Deal(jsonDeal);
                            dealList.addLast(deal);
                        }
                        activity.runOnUiThread(() -> adapter.dealList = dealList);
                        activity.runOnUiThread(() -> adapter.notifyDataSetChanged());
                    } else {
                        activity.runOnUiThread(() -> Toast.makeText(activity, R.string.get_deal_list_fail, Toast.LENGTH_SHORT).show());
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
            if (requestCode == REQUEST_PUBLISH) {
                Toast.makeText(activity, R.string.publish_success, Toast.LENGTH_SHORT).show();
                updateDealList();
            } else if (requestCode == REQUEST_INFO) {
                Toast.makeText(activity, R.string.accept_deal_success, Toast.LENGTH_SHORT).show();
                dealList.remove(clickedPosition);
                adapter.notifyDataSetChanged();
                updateDealList();
            }
        }

    }

    private void searchDeal(String query) {
        if (query.equals("")) {
            return;
        }
        LinkedList<Deal> searchDealList = new LinkedList<>();
        for (Deal deal : dealList) {
            if (deal.containString(query)) {
                searchDealList.addLast(deal);
            }
        }
        adapter.dealList = searchDealList;
        adapter.notifyDataSetChanged();
    }
}
