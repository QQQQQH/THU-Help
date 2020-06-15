package com.thu.thuhelp.MainActivity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thu.thuhelp.App;
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

    private MainActivity activity;
    private App app;
    private MainActivity activity;
    private View view;

    public MyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_my, container, false);
        activity = (MainActivity) getActivity();
        assert activity != null;
        app = (App) requireActivity().getApplication();
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == REQUEST_LOGIN) {
                Toast.makeText(getActivity(), R.string.login_success, Toast.LENGTH_SHORT).show();
                activity.mainFragmentSetView();
            } else if (requestCode == REQUEST_REGISTER) {
                Toast.makeText(getActivity(), R.string.register_success, Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        setLoginLayout();
    }

    private void setLoginLayout() {
        String skey = app.getSkey();
        if (skey == null) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("skey", skey);
        CommonInterface.sendOkHttpGetRequest("/user/account/info", params, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("error", e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String resStr = response.body().string();
                Log.e("response", resStr);
                try {
                    JSONObject jsonObject = new JSONObject(resStr);
                    int statusCode = jsonObject.getInt("status");
                    if (statusCode == 200) {
//                        ((TextView) view.findViewById(R.id.nickname_label)).setText(jsonObject.getString("nickname"));
//                        ((TextView) view.findViewById(R.id.balance_label)).setText(jsonObject.getString("balance"));
                    }
                    else {
                        activity.runOnUiThread(() -> Toast.makeText(activity, resStr, Toast.LENGTH_LONG).show());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        view.findViewById(R.id.financeLayout).setVisibility(View.VISIBLE);
        view.findViewById(R.id.dealLayout).setVisibility(View.VISIBLE);
    }
}
