package com.thu.thuhelp.DealActivity;

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
import com.thu.thuhelp.MainActivity.MissionListAdapter;
import com.thu.thuhelp.MainActivity.MyFragment;
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
public class DealListFragment extends Fragment {
    private final LinkedList<Deal> dealList = new LinkedList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewDeal;
    private MissionListAdapter adapter;
    private SearchView searchView;

    private App app;
    private DealListActivity activity;
    private int state, init;
    private int clickedPosition;
    private int dealType;

    public static final String
            EXTRA_DEAL = "com.thu.thuhelp.DealActivity.DealListFragment.extra.deal",
            EXTRA_DEAL_TYPE = "com.thu.thuhelp.DealActivity.DealListFragment.extra.deal_type";

    public static final int
            DEAL_ALL_PUBLISH = -1,
            DEAL_MY_PUBLISH = 0,
            DEAL_MY_ACCEPT = 1,
            DEAL_OTHERS_ACCEPT = 2,
            DEAL_MY_CONFIRM = 3,
            DEAL_OTHERS_CONFIRM = 4,
            DEAL_MY_FINISH = 5,
            DEAL_OTHERS_FINISH = 6;

    public DealListFragment(int state, int init) {
        this.state = state;
        this.init = init;
        if (init == DealListActivity.INIT_YES) {
            switch (state) {
                case MyFragment.DEAL_PUBLISH:
                    dealType = DEAL_MY_PUBLISH;
                    break;
                case MyFragment.DEAL_ACCEPT:
                    dealType = DEAL_MY_ACCEPT;
                    break;
                case MyFragment.DEAL_CONFIRM:
                    dealType = DEAL_MY_CONFIRM;
                    break;
                case MyFragment.DEAL_FINISH:
                    dealType = DEAL_MY_FINISH;
                    break;
            }
        } else {
            switch (state) {
                case MyFragment.DEAL_ACCEPT:
                    dealType = DEAL_OTHERS_ACCEPT;
                    break;
                case MyFragment.DEAL_CONFIRM:
                    dealType = DEAL_OTHERS_CONFIRM;
                    break;
                case MyFragment.DEAL_FINISH:
                    dealType = DEAL_OTHERS_FINISH;
                    break;
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (DealListActivity) getActivity();
        assert activity != null;
        app = (App) activity.getApplication();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_deal_list, container, false);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanced) {
        super.onViewCreated(view, savedInstanced);

        // set swipe refresh layout
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutDeal);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateDealList();
            }
        });

        // set recycler view
        recyclerViewDeal = view.findViewById(R.id.recyclerViewDeal);
        adapter = new MissionListAdapter(activity, dealList, app);

        recyclerViewDeal.setAdapter(adapter);
        recyclerViewDeal.setLayoutManager(new LinearLayoutManager(activity));
        recyclerViewDeal.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));

        updateDealList();

        adapter.setOnItemClickListener((v, position) -> {
            clickedPosition = position;
            Deal deal = adapter.dealList.get(position);
            Intent intent = new Intent(activity, DealInfoActivity.class);
            intent.putExtra(EXTRA_DEAL, deal);
            intent.putExtra(EXTRA_DEAL_TYPE, dealType);
            startActivityForResult(intent, dealType);
        });

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

    private void updateDealList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("skey", app.getSkey());
        params.put("state", String.valueOf(state));
        params.put("init", String.valueOf(init));

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
            dealList.remove(clickedPosition);
            adapter.notifyDataSetChanged();
            updateDealList();
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
