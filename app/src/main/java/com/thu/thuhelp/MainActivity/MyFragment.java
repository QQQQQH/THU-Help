package com.thu.thuhelp.MainActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.thu.thuhelp.App;
import com.thu.thuhelp.DealActivity.DealListActivity;
import com.thu.thuhelp.R;
import com.thu.thuhelp.UserInfoActivity.EditProfileActivity;
import com.thu.thuhelp.UserInfoActivity.LoginActivity;
import com.thu.thuhelp.UserInfoActivity.RegisterActivity;
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
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends Fragment {

    private final static int
            REQUEST_LOGIN = 0,
            REQUEST_REGISTER = 1,
            REQUEST_CAMERA = 2,
            REQUEST_ALBUM = 3,
            REQUEST_CROP = 4,
            REQUEST_EDIT = 5;

    static private int MAX_VALUE = 1000000;
    private File avatarFile;

    private MainActivity activity;
    private App app;
    private View view;
    private boolean login;

    public MyFragment() {
        // Required empty public constructor
    }

    public static final String
            EXTRA_DEAL_STATE = "com.thu.thuhelp.MainActivity.MyFragment.extra.deal_state";
    public static final int
            DEAL_PUBLISH = 0,
            DEAL_ACCEPT = 1,
            DEAL_CONFIRM = 2,
            DEAL_FINISH = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = (MainActivity) getActivity();
        assert activity != null;
        app = (App) activity.getApplication();
        view = inflater.inflate(R.layout.fragment_my, container, false);
        login = false;

        avatarFile = new File(new File(activity.getFilesDir(), "images"), "avatar_new.jpg");
        if (!avatarFile.getParentFile().exists()) {
            avatarFile.getParentFile().mkdirs();
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanced) {
        super.onViewCreated(view, savedInstanced);

        view.findViewById(R.id.buttonLogin).setOnClickListener(v -> {
            Intent intent = new Intent(activity, LoginActivity.class);
            startActivityForResult(intent, REQUEST_LOGIN);
        });

        view.findViewById(R.id.buttonRegister).setOnClickListener(v -> {
            Intent intent = new Intent(activity, RegisterActivity.class);
            startActivityForResult(intent, REQUEST_REGISTER);
        });

        view.findViewById(R.id.buttonLogout).setOnClickListener(v -> {
            activity.logout();
        });

        view.findViewById(R.id.buttonCash).setOnClickListener(v -> {
            showCashDialog();
        });

        view.findViewById(R.id.buttonDeposit).setOnClickListener(v -> {
            showDepositDialog();
        });

        view.findViewById(R.id.profileLayout).setOnClickListener(v -> {
            if (!login) {
                return;
            }
            showEditProfileDialog();
        });

        view.findViewById(R.id.buttonMyPublish).setOnClickListener(v -> {
            Intent intent = new Intent(activity, DealListActivity.class);
            intent.putExtra(EXTRA_DEAL_STATE, DEAL_PUBLISH);
            startActivity(intent);
        });

        view.findViewById(R.id.buttonMyAccept).setOnClickListener(v -> {
            Intent intent = new Intent(activity, DealListActivity.class);
            intent.putExtra(EXTRA_DEAL_STATE, DEAL_ACCEPT);
            startActivity(intent);
        });

        view.findViewById(R.id.buttonMyConfirm).setOnClickListener(v -> {
            Intent intent = new Intent(activity, DealListActivity.class);
            intent.putExtra(EXTRA_DEAL_STATE, DEAL_CONFIRM);
            startActivity(intent);
        });

        view.findViewById(R.id.buttonMyFinish).setOnClickListener(v -> {
            Intent intent = new Intent(activity, DealListActivity.class);
            intent.putExtra(EXTRA_DEAL_STATE, DEAL_FINISH);
            startActivity(intent);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_LOGIN:
                    Toast.makeText(activity, R.string.login_success, Toast.LENGTH_SHORT).show();
                    activity.mainFragmentSetView();
                    activity.myFragmentSetView();
                    break;
                case REQUEST_REGISTER:
                    Toast.makeText(activity, R.string.register_success, Toast.LENGTH_SHORT).show();
                    break;
                case REQUEST_CAMERA:
                    cropAvatar();
                    break;
                case REQUEST_ALBUM:
                    break;
                case REQUEST_CROP:
                    uploadAvatar();
                    break;
                case REQUEST_EDIT:
                    Toast.makeText(activity, R.string.edit_profile_success, Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    }

    private MaterialDialog.Builder buildInfoDialog(String content) {
        return new MaterialDialog.Builder(activity)
                .title("提示")
                .content(content)
                .positiveText("确认")
                .positiveColor(getResources().getColor(R.color.colorPrimary));
    }

    private void showEditProfileDialog() {
        new MaterialDialog.Builder(activity)
                .title("标题")
                .items(R.array.edit_profile)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (which == 0) {
                            editAvatar();
                        } else if (which == 1) {
                            editProfile();
                        }
                    }
                })
                .positiveText("确认")
                .negativeText("取消")
                .positiveColor(getResources().getColor(R.color.colorPrimary))
                .negativeColor(getResources().getColor(R.color.colorPrimary))
                .show();
    }

    private void showCashDialog() {
        new MaterialDialog.Builder(activity)
                .title("输入金额")
                .inputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .input(null, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        cash(input.toString());
                    }
                })
                .positiveText("确定")
                .negativeText("取消")
                .positiveColor(getResources().getColor(R.color.colorPrimary))
                .negativeColor(getResources().getColor(R.color.colorPrimary))
                .show();
    }

    private void showDepositDialog() {
        new MaterialDialog.Builder(activity)
                .title("输入金额")
                .inputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                .input(null, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        deposit(input.toString());
                    }
                })
                .positiveText("确定")
                .negativeText("取消")
                .positiveColor(getResources().getColor(R.color.colorPrimary))
                .negativeColor(getResources().getColor(R.color.colorPrimary))
                .show();
    }

    private boolean checkValue(String value) {
        if (value == null || value.equals("")) {
            buildInfoDialog("金额不能为空").show();
            return false;
        } else if (Double.parseDouble(value) == 0) {
            buildInfoDialog("金额不能为零").show();
            return false;
        } else if (value.contains(".") && value.length() - value.indexOf(".") > 3) {
            buildInfoDialog("仅限两位小数").show();
            return false;
        }
        return true;
    }

    private void cash(String value) {
        if (!checkValue(value)) {
            return;
        }
        Double balance = Double.parseDouble(((TextView) view.findViewById(R.id.balance_label)).getText().toString());
        if (Double.parseDouble(value) > balance) {
            buildInfoDialog("提现金额大于余额").show();
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("skey", app.getSkey());
        params.put("value", value);
        CommonInterface.sendOkHttpGetRequest("/user/account/cash", params, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("error", e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String resStr = Objects.requireNonNull(response.body()).string();
                try {
                    JSONObject res = new JSONObject(resStr);
                    int statusCode = res.getInt("status");
                    if (statusCode == 200) {
                        updateUserInfo();
                    } else {
                        Toast.makeText(activity, resStr, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException ignored) {
                }
            }
        });
    }

    private void deposit(String value) {
        if (!checkValue(value)) {
            return;
        }
        Double balance = Double.parseDouble(((TextView) view.findViewById(R.id.balance_label)).getText().toString());
        if (Double.parseDouble(value) + balance >= MAX_VALUE) {
            buildInfoDialog("大于余额限制").show();
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("skey", app.getSkey());
        params.put("value", value);
        CommonInterface.sendOkHttpGetRequest("/user/account/deposit", params, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("error", e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String resStr = Objects.requireNonNull(response.body()).string();
                try {
                    JSONObject res = new JSONObject(resStr);
                    int statusCode = res.getInt("status");
                    if (statusCode == 200) {
                        updateUserInfo();
                    } else {
                        Toast.makeText(activity, resStr, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException ignored) {
                }
            }
        });
    }

    private void editAvatar() {
        // 获取权限
        new MaterialDialog.Builder(activity)
                .title("选择图片")
                .items(R.array.edit_avatar)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (which == 0) {
                            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (camera.resolveActivity(activity.getPackageManager()) != null) {
                                if (avatarFile.exists()) {
                                    avatarFile.delete();
                                    try {
                                        avatarFile.createNewFile();
                                    } catch (IOException ignored) {
                                    }
                                }
                                Uri imageURI = FileProvider.getUriForFile(
                                        activity,
                                        "com.edu.thuhelp.fileprovider",
                                        avatarFile
                                );
                                camera.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
                                startActivityForResult(camera, REQUEST_CAMERA);
                            }
                        } else if (which == 1) {
                            Intent album = new Intent(
                                    Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(album, REQUEST_ALBUM);
                        }
                    }
                })
                .positiveText("确认")
                .negativeText("取消")
                .positiveColor(getResources().getColor(R.color.colorPrimary))
                .negativeColor(getResources().getColor(R.color.colorPrimary))
                .show();
    }

    private void cropAvatar() {
        Uri imageURI = FileProvider.getUriForFile(
                activity,
                "com.edu.thuhelp.fileprovider",
                avatarFile
        );
        Intent crop = new Intent("com.android.camera.action.CROP");
        crop.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        crop.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        crop.setDataAndType(imageURI, "image/*");
        crop.putExtra("crop", "true");
        crop.putExtra("aspectX", 1);
        crop.putExtra("aspectY", 1);
        crop.putExtra("outputX", 150);
        crop.putExtra("outputY", 150);
//        crop.putExtra("return-data", true);
//        crop.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
        crop.putExtra("return-data", false);
        crop.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
        crop.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(crop, REQUEST_CROP);
    }

    private void uploadAvatar() {
        HashMap<String, String> params = new HashMap<>();
        params.put("skey", app.getSkey());
        CommonInterface.uploadImage("/user/account/upload-avatar", params, avatarFile.toString(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("error", e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String resStr = Objects.requireNonNull(response.body()).string();
                try {
                    JSONObject res = new JSONObject(resStr);
                    int statusCode = res.getInt("status");
                    if (statusCode == 200) {
                        updateUserInfo();
                    } else {
                        activity.runOnUiThread(() -> Toast.makeText(activity, resStr, Toast.LENGTH_LONG).show());
                    }
                } catch (JSONException ignored) {
                }
            }
        });
    }

    private void updateAvatar(boolean setDefault) {
        if (setDefault) {
            ((ImageView) view.findViewById(R.id.avatarView)).setImageDrawable(getResources().getDrawable(R.drawable.ic_person));
            return;
        }
        ContentResolver cr = activity.getContentResolver();
        try {
            Bitmap avatar = BitmapFactory.decodeStream(cr.openInputStream(Uri.fromFile(avatarFile)));
            ((ImageView) view.findViewById(R.id.avatarView)).setImageBitmap(avatar);
        } catch (FileNotFoundException e) {
            Toast.makeText(activity, "更新头像失败", Toast.LENGTH_LONG).show();
        }
    }

    private void editProfile() {
        Intent intent = new Intent(activity, EditProfileActivity.class);
        startActivityForResult(intent, REQUEST_EDIT);
    }

    private void updateUserInfo() {
        setLoginView();
    }

    void setLogoutView() {
        login = false;
        updateAvatar(true);
        view.findViewById(R.id.userCardView).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.buttonLogout).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);
        view.findViewById(R.id.buttonRegister).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.nickname_label)).setText(R.string.my_nickname);
        ((TextView) view.findViewById(R.id.balance_label)).setText(R.string.my_balance);
    }

    void setLoginView() {
        HashMap<String, String> params = new HashMap<>();
        params.put("skey", app.getSkey());
        CommonInterface.sendOkHttpGetRequest("/user/account/info", params, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("error", e.toString());
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String resStr = response.body().string();
                try {
                    JSONObject res = new JSONObject(resStr);
                    int statusCode = res.getInt("status");
                    if (statusCode == 200) {
                        JSONObject userInfo = res.getJSONObject("data");
                        String avatarString = userInfo.getString("avatar");
                        if (!avatarString.equals("null")) {
                            byte[] avatarBytes = Base64.getDecoder().decode(avatarString);
                            FileOutputStream fos = new FileOutputStream(avatarFile);
                            BufferedOutputStream bos = new BufferedOutputStream(fos);
                            bos.write(avatarBytes);
                            bos.close();
                            fos.close();
                        }
                        activity.runOnUiThread(() -> {
                            try {
                                view.findViewById(R.id.userCardView).setVisibility(View.VISIBLE);
                                view.findViewById(R.id.buttonLogout).setVisibility(View.VISIBLE);
                                view.findViewById(R.id.buttonLogin).setVisibility(View.INVISIBLE);
                                view.findViewById(R.id.buttonRegister).setVisibility(View.INVISIBLE);

                                updateAvatar(avatarString.equals("null"));
                                ((TextView) view.findViewById(R.id.nickname_label)).setText(userInfo.getString("nickname"));
                                ((TextView) view.findViewById(R.id.balance_label)).setText(userInfo.getString("balance"));
                                login = true;
                            } catch (Exception ignored) {
                            }
                        });
                    } else {
                        activity.runOnUiThread(() -> Toast.makeText(activity, resStr, Toast.LENGTH_SHORT).show());
                    }
                } catch (JSONException ignored) {
                }
            }
        });
    }
}
