package com.thu.thuhelp.MainActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thu.thuhelp.DealActivity.PublishDealActivity;
import com.thu.thuhelp.R;

import java.util.LinkedList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    private final LinkedList<String> titleList = new LinkedList<>();
    private final LinkedList<String> nameList = new LinkedList<>();
    private final LinkedList<String> timeList = new LinkedList<>();
    private RecyclerView recyclerViewDeal;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
//                Toast.makeText(getActivity(), "Fab clicked.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), PublishDealActivity.class);
                startActivity(intent);
            }
        });


        // set recycler view
        for (int i = 0; i < 20; ++i) {
            titleList.addLast("任务标题" + String.format("%3d", i));
            nameList.addLast("发布人" + String.format("%3d", i));
            timeList.addLast("发布时间" + String.format("%3d", i));
        }

        recyclerViewDeal = view.findViewById(R.id.recyclerViewDeal);
        MissionListAdapter adapter = new MissionListAdapter(view.getContext(),
                titleList, nameList, timeList
        );

        recyclerViewDeal.setAdapter(adapter);
        recyclerViewDeal.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerViewDeal.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
    }
}
