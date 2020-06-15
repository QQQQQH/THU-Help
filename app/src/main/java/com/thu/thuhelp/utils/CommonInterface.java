package com.thu.thuhelp.utils;

import android.util.Log;

import java.util.HashMap;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class CommonInterface {
    private static final String server_url = "http://10.0.2.2:8000";

    private static Request request;


    // Get
    public static void sendOkHttpGetRequest(String url, HashMap<String, String> params, okhttp3.Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(server_url + url)).newBuilder();
        if (params != null) {
            for (String key: params.keySet()) {
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
}
