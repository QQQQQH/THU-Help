package com.thu.thuhelp.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Deal {
    public String address;
    public double bonus;
    public String description;
    public String title;
    public String phone;
    public String name;
    public String startTime;
    public String endTime;
    public String did;

    public Deal(JSONObject obj) throws JSONException {
        address = obj.getString("address");
        bonus = obj.getDouble("bonus");
        description = obj.getString("description");
        title = obj.getString("title");
        phone = obj.getString("phone");
        name = obj.getString("name");
        startTime = obj.getString("startTime");
        endTime = obj.getString("endTime");
        did = obj.getString("did");

        DateFormat dateFormatLong = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        DateFormat dateFormatShort = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            startTime = dateFormatShort.format(dateFormatLong.parse(startTime));
            endTime = dateFormatShort.format(dateFormatLong.parse(endTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
