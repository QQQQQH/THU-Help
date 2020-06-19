package com.thu.thuhelp.MainActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.thu.thuhelp.App;
import com.thu.thuhelp.R;
import com.thu.thuhelp.utils.CommonInterface;
import com.thu.thuhelp.utils.Deal;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MissionListAdapter extends RecyclerView.Adapter<MissionListAdapter.MissionViewHolder> {
    private App app;
    public LinkedList<Deal> dealList;
    private LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;

    public MissionListAdapter(Context context, LinkedList<Deal> dealList, App app) {
        this.app = app;
        this.inflater = LayoutInflater.from(context);
        this.dealList = dealList;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    static class MissionViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewTitle, textViewName, textViewTime;
        final ImageView imageViewAvatar;
        final View itemView;

        MissionViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewName = itemView.findViewById(R.id.textViewLastMessage);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            imageViewAvatar = itemView.findViewById(R.id.imageViewAvatar);
            this.itemView = itemView;
        }
    }

    @NonNull
    @Override
    public MissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_mission, parent, false);
        return new MissionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MissionViewHolder holder, int position) {
        Deal deal = dealList.get(position);
        String title = deal.title,
                name = deal.name,
                time = deal.startTime,
                uid = deal.initiator;
        holder.textViewTitle.setText(title);
        holder.textViewName.setText(name);
        holder.textViewTime.setText(time);
        holder.imageViewAvatar.setImageResource(R.drawable.ic_person);

        Bitmap avatar;

        File avatarFile = new File(new File(app.getFilesDir(), "images"), "avatar" + uid + ".jpg");
        try {
            avatar = BitmapFactory.decodeStream(app.getContentResolver().openInputStream(Uri.fromFile(avatarFile)));
        } catch (FileNotFoundException e) {
            avatar = null;
        }
        if (avatar != null) {
            holder.imageViewAvatar.setImageBitmap(avatar);
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("skey", app.getSkey());
        params.put("uid", uid);
        Handler handler = new Handler();

        CommonInterface.sendOkHttpGetRequest("/user/account/info", params, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("error", e.toString());
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String resStr = response.body().string();
                Log.e("response", resStr);
                try {
                    JSONObject res = new JSONObject(resStr);
                    int statusCode = res.getInt("status");
                    if (statusCode == 200) {
                        JSONObject userInfo = res.getJSONObject("data");

                        String avatarString = userInfo.getString("avatar");
                        byte[] avatarBytes = Base64.getDecoder().decode(avatarString);
                        Bitmap avatar = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);
                        handler.post(() -> holder.imageViewAvatar.setImageBitmap(avatar));

                        if (!avatarFile.getParentFile().exists()) {
                            avatarFile.getParentFile().mkdirs();
                        }
                        FileOutputStream fos = new FileOutputStream(avatarFile);
                        BufferedOutputStream bos = new BufferedOutputStream(fos);
                        bos.write(avatarBytes);
                        bos.close();
                        fos.close();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(v, position));
        }

    }

    @Override
    public int getItemCount() {
        return dealList.size();
    }
}
