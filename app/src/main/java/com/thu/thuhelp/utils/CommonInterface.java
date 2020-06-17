package com.thu.thuhelp.utils;

import android.util.Log;
import android.widget.Toast;

import com.thu.thuhelp.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommonInterface {
    private static final String server_url = "http://123.57.140.189:80";
//    private static final String server_url = "http://192.168.1.137:8000";
    private static Request request;

    // Get
    public static void sendOkHttpGetRequest(String url, HashMap<String, String> params, okhttp3.Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(server_url + url)).newBuilder();
        if (params != null) {
            for (String key : params.keySet()) {
                urlBuilder.addQueryParameter(key, params.get(key));
            }
        }
        request = new Request.Builder().url(urlBuilder.build()).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpGetRequest(String url, okhttp3.Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        request = new Request.Builder().url(server_url + url).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    // Post
    public static void sendOkHttpPostRequest(String url, HashMap<String, String> params, okhttp3.Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : params.keySet()) {
            builder.add(key, params.get(key));
        }

        RequestBody requestBody = builder.build();

        request = new Request.Builder().url(server_url + url).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void uploadImage(String url, HashMap<String, String> params, String imagePath, okhttp3.Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        File file = new File(imagePath);
        RequestBody image = RequestBody.create(MediaType.parse("image/png"), file);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (String key : params.keySet()) {
            builder.addFormDataPart(key, params.get(key));
        }
        builder.addFormDataPart("file", imagePath, image);
        RequestBody requestBody = builder.build();
        request = new Request.Builder()
                .url(server_url + url)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }
}

