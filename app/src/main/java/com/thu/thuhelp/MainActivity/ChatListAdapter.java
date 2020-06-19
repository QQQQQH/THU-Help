package com.thu.thuhelp.MainActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import com.thu.thuhelp.utils.ChatAbstract;
import com.thu.thuhelp.utils.CommonInterface;

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

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {
    private App app;
    private LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;
    public LinkedList<ChatAbstract> chatAbstractList;

    public ChatListAdapter(Context context, LinkedList<ChatAbstract> chatAbstractList, App app) {
        this.app = app;
        this.inflater = LayoutInflater.from(context);
        this.chatAbstractList = chatAbstractList;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    static class ChatViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewNickname, textViewLastMsg;
        final ImageView imageViewAvatar;
        final View itemView;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNickname = itemView.findViewById(R.id.textViewNickName);
            textViewLastMsg = itemView.findViewById(R.id.textViewLastMsg);
            imageViewAvatar = itemView.findViewById(R.id.imageViewAvatar);
            this.itemView = itemView;
        }
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_chat, parent, false);
        return new ChatListAdapter.ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatAbstract chatAbstract = chatAbstractList.get(position);
        holder.textViewNickname.setText(R.string.nickname);
        holder.textViewLastMsg.setText(chatAbstract.lastMsg);
        holder.imageViewAvatar.setImageResource(R.drawable.ic_person);

        String uid = chatAbstract.uid;

        Bitmap avatar;

        File avatarFile = new File(new File(app.getFilesDir(), "images"), "avatar" + uid + ".jpg");
        try {
            avatar = BitmapFactory.decodeStream(app.getContentResolver().openInputStream(Uri.fromFile(avatarFile)));
        } catch (FileNotFoundException e) {
            avatar = null;
        }
        if (avatar != null) {
            holder.imageViewAvatar.setImageBitmap(avatar);
        } else {
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

                            String nickName = userInfo.getString("nickname");
                            handler.post(() -> holder.textViewNickname.setText(nickName));

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
        }

        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(v, position));
        }
    }

    @Override
    public int getItemCount() {
        return chatAbstractList.size();
    }


}
