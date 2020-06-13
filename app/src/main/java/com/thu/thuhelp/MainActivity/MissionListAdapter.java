package com.thu.thuhelp.mainActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thu.thuhelp.R;

import java.util.LinkedList;

public class MissionListAdapter
        extends RecyclerView.Adapter<MissionListAdapter.MissionViewHolder> {
    private final LinkedList<String> titleList, nameList, timeList;
    private LayoutInflater inflater;

    public MissionListAdapter(Context context,
                              LinkedList<String> titleList,
                              LinkedList<String> nameList,
                              LinkedList<String> timeList) {
        inflater = LayoutInflater.from(context);
        this.titleList = titleList;
        this.nameList = nameList;
        this.timeList = timeList;
    }

    static class MissionViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        final TextView textViewTitle, textViewName, textViewTime;
        final MissionListAdapter adapter;


        MissionViewHolder(@NonNull View itemView, MissionListAdapter adapter) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            this.adapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "Clicked " + textViewTitle.getText().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    @Override
    public MissionListAdapter.MissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.missionlist_item, parent, false);
        return new MissionViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull MissionListAdapter.MissionViewHolder holder, int position) {
        String title = titleList.get(position),
                name = nameList.get(position),
                time = timeList.get(position);
        holder.textViewTitle.setText(title);
        holder.textViewName.setText(name);
        holder.textViewTime.setText(time);
    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }

}
