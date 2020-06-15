package com.thu.thuhelp.MainActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public MissionListAdapter(Context context,
                              LinkedList<Deal> dealList) {
        inflater = LayoutInflater.from(context);
        this.dealList = dealList;
    }

    static class MissionViewHolder extends RecyclerView.ViewHolder
//            implements View.OnClickListener
    {
        final TextView textViewTitle, textViewName, textViewTime;
        final View itemView;

        MissionViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            this.itemView = itemView;
//            itemView.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View v) {
//            Toast.makeText(v.getContext(), "Clicked " + textViewTitle.getText().toString(), Toast.LENGTH_SHORT).show();
//        }
    }

    @NonNull
    @Override
    public MissionListAdapter.MissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.missionlist_item, parent, false);
        return new MissionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MissionListAdapter.MissionViewHolder holder, int position) {
        String title = dealList.get(position).title,
                name = dealList.get(position).name,
                time = dealList.get(position).startTime;
        holder.textViewTitle.setText(title);
        holder.textViewName.setText(name);
        holder.textViewTime.setText(time);
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dealList.size();
    }
}
