package com.thu.thuhelp.ChatActivity;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.thu.thuhelp.App;
import com.thu.thuhelp.R;
import com.thu.thuhelp.utils.CommonInterface;
import com.thu.thuhelp.utils.Message;

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

public class MessageListAdapter
        extends RecyclerView.Adapter<MessageListAdapter.MessageViewHolder> {
    private LayoutInflater inflater;
    private LinkedList<Message> messageList;
    private Bitmap leftAvatar, rightAvatar;
    private String uid;
    private App app;
    private File avatarLeftFile;

    MessageListAdapter(Context context, LinkedList<Message> messageList, Bitmap rightAvatar, String uid, App app) {
        inflater = LayoutInflater.from(context);
        this.messageList = messageList;
        this.rightAvatar = rightAvatar;
        this.uid = uid;
        this.app = app;
        avatarLeftFile = new File(new File(app.getFilesDir(), "images"), "avatar" + uid + ".jpg");
        try {
            this.leftAvatar = BitmapFactory.decodeStream(app.getContentResolver().openInputStream(Uri.fromFile(avatarLeftFile)));
        } catch (FileNotFoundException e) {
            this.leftAvatar = null;
        }
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        final LinearLayout leftLayout, rightLayout;
        final TextView textViewLeftMsg, textViewRightMsg;
        final ImageView imageViewAvatarLeft, imageViewAvatarRight;
        final View itemView;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            leftLayout = itemView.findViewById(R.id.layoutLeft);
            rightLayout = itemView.findViewById(R.id.layoutRight);
            textViewLeftMsg = itemView.findViewById(R.id.left_msg);
            textViewRightMsg = itemView.findViewById(R.id.right_msg);
            imageViewAvatarLeft = itemView.findViewById(R.id.imageViewAvatarLeft);
            imageViewAvatarRight = itemView.findViewById(R.id.imageViewAvatarRight);
            this.itemView = itemView;
        }
    }

    @NonNull
    @Override
    public MessageListAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                   int viewType) {
        View itemView = inflater.inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageListAdapter.MessageViewHolder holder,
                                 int position) {
        Message message = messageList.get(position);
        if (message.type == Message.TYPE_RECEIVED) {
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.textViewLeftMsg.setText(message.content);
            if (leftAvatar != null) {
                holder.imageViewAvatarLeft.setImageBitmap(leftAvatar);
                return;
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
                            leftAvatar = avatar;
                            handler.post(() -> holder.imageViewAvatarLeft.setImageBitmap(avatar));

                            if (!avatarLeftFile.getParentFile().exists()) {
                                avatarLeftFile.getParentFile().mkdirs();
                            }
                            FileOutputStream fos = new FileOutputStream(avatarLeftFile);
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
        } else {
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.textViewRightMsg.setText(message.content);
            holder.imageViewAvatarRight.setImageBitmap(rightAvatar);
        }


    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
