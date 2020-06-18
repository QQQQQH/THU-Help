package com.thu.thuhelp.utils;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Deal implements Parcelable {
    public String address;
    public double bonus;
    public String initiator;
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
        initiator = obj.getString("initiator");
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

    protected Deal(Parcel in) {
        address = in.readString();
        bonus = in.readDouble();
        initiator = in.readString();
        description = in.readString();
        title = in.readString();
        phone = in.readString();
        name = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        did = in.readString();
    }

    public static final Creator<Deal> CREATOR = new Creator<Deal>() {
        @Override
        public Deal createFromParcel(Parcel in) {
            return new Deal(in);
        }

        @Override
        public Deal[] newArray(int size) {
            return new Deal[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeDouble(bonus);
        dest.writeString(initiator);
        dest.writeString(description);
        dest.writeString(title);
        dest.writeString(phone);
        dest.writeString(name);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(did);
    }

    public boolean containString(String query) {
        query = query.toLowerCase();
        return title.toLowerCase().contains(query) ||
                name.toLowerCase().contains(query) ||
                description.toLowerCase().contains(query) ||
                address.toLowerCase().contains(query) ||
                phone.toLowerCase().contains(query);
    }
}
