package com.thu.thuhelp.MainActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thu.thuhelp.R;
import com.thu.thuhelp.utils.Deal;

import java.util.LinkedList;

public class MissionListAdapter
        extends RecyclerView.Adapter<MissionListAdapter.MissionViewHolder> {
    public LinkedList<Deal> dealList;
    private LayoutInflater inflater;

    public MissionListAdapter(Context context,
                              LinkedList<Deal> dealList) {
        inflater = LayoutInflater.from(context);
        this.dealList = dealList;
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
        String title = dealList.get(position).title,
                name = dealList.get(position).name,
                time = dealList.get(position).startTime;
        holder.textViewTitle.setText(title);
        holder.textViewName.setText(name);
        holder.textViewTime.setText(time);
    }

    @Override
    public int getItemCount() {
        return dealList.size();
    }

}
