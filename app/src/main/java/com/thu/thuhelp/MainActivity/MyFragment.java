package com.thu.thuhelp.MainActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.thu.thuhelp.App;
import com.thu.thuhelp.DealActivity.DealListActivity;
import com.thu.thuhelp.R;
import com.thu.thuhelp.EnterActivity.LoginActivity;
import com.thu.thuhelp.EnterActivity.RegisterActivity;
import com.thu.thuhelp.utils.CommonInterface;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends Fragment {

    static private int
            REQUEST_LOGIN = 0,
            REQUEST_REGISTER = 1;

    static private int MAX_VALUE = 1000000;

    private MainActivity activity;
    private App app;
    private View view;

    public MyFragment() {
        // Required empty public constructor
    }

    public static final String
            EXTRA_DEAL_LIST_TYPE = "com.thu.thuhelp.MainActivity.MyFragment.extra.deal_list_type";
    public static final int
            MY_PUBLISH = 0,
            MY_ACCEPT = 1,
            MY_FINISH = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_my, container, false);
        activity = (MainActivity) getActivity();
        assert activity != null;
        app = (App) activity.getApplication();
        view = inflater.inflate(R.layout.fragment_my, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanced) {
        super.onViewCreated(view, savedInstanced);

        view.findViewById(R.id.buttonLogin).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivityForResult(intent, REQUEST_LOGIN);
        });

        view.findViewById(R.id.buttonRegister).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RegisterActivity.class);
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

        view.findViewById(R.id.buttonMyPublish).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DealListActivity.class);
            intent.putExtra(EXTRA_DEAL_LIST_TYPE, MY_PUBLISH);
            startActivity(intent);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == REQUEST_LOGIN) {
                Toast.makeText(getActivity(), R.string.login_success, Toast.LENGTH_SHORT).show();
                activity.mainFragmentSetView();
                activity.myFragmentSetView();
            } else if (requestCode == REQUEST_REGISTER) {
                Toast.makeText(getActivity(), R.string.register_success, Toast.LENGTH_SHORT).show();
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
                .positiveColor(getResources().getColor(R.color.colorPrimary))
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
                .positiveColor(getResources().getColor(R.color.colorPrimary))
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

    private void updateUserInfo() {
        setLoginView();
    }

    void setLogoutView() {
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

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String resStr = response.body().string();
                try {
                    JSONObject res = new JSONObject(resStr);
                    int statusCode = res.getInt("status");
                    if (statusCode == 200) {
                        JSONObject userInfo = res.getJSONObject("data");
                        activity.runOnUiThread(() -> {
                            try {
                                view.findViewById(R.id.userCardView).setVisibility(View.VISIBLE);
                                view.findViewById(R.id.buttonLogout).setVisibility(View.VISIBLE);
                                view.findViewById(R.id.buttonLogin).setVisibility(View.INVISIBLE);
                                view.findViewById(R.id.buttonRegister).setVisibility(View.INVISIBLE);
                                ((TextView) view.findViewById(R.id.nickname_label)).setText(userInfo.getString("nickname"));
                                ((TextView) view.findViewById(R.id.balance_label)).setText(userInfo.getString("balance"));
                            } catch (JSONException ignored) {
                            }
                        });
                    } else {
                        Toast.makeText(activity, resStr, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException ignored) {
                }
            }
        });
    }
}
