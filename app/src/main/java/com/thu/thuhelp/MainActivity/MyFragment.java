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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.thu.thuhelp.App;
import com.thu.thuhelp.R;
import com.thu.thuhelp.EnterActivity.LoginActivity;
import com.thu.thuhelp.EnterActivity.RegisterActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends Fragment {

    static private int
            REQUEST_LOGIN = 0,
            REQUEST_REGISTER = 1;

    public MyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanced) {
        super.onViewCreated(view, savedInstanced);

        view.findViewById(R.id.buttonLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(intent, REQUEST_LOGIN);
            }
        });

        view.findViewById(R.id.buttonRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                startActivityForResult(intent, REQUEST_REGISTER);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == REQUEST_LOGIN) {
                Toast.makeText(getActivity(), R.string.login_success, Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_REGISTER) {
                Toast.makeText(getActivity(), R.string.register_success, Toast.LENGTH_SHORT).show();
            }
        }

    }
}
