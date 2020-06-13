package com.thu.thuhelp.mainActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thu.thuhelp.R;

import java.util.LinkedList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    private final LinkedList<String> titleList = new LinkedList<>();
    private final LinkedList<String> nameList = new LinkedList<>();
    private final LinkedList<String> timeList = new LinkedList<>();
    private RecyclerView recyclerViewMission;

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
        for (int i = 0; i < 20; ++i) {
            titleList.addLast("任务标题" + String.format("%3d", i));
            nameList.addLast("发布人" + String.format("%3d", i));
            timeList.addLast("发布时间" + String.format("%3d", i));
        }

        recyclerViewMission = view.findViewById(R.id.recyclerViewMission);
        MissionListAdapter adapter = new MissionListAdapter(view.getContext(),
                titleList, nameList, timeList
        );

        recyclerViewMission.setAdapter(adapter);
        recyclerViewMission.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerViewMission.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
    }
}
