package com.example.proj_mad;

import android.os.Parcel;
import android.os.Parcelable;

public class MainModel implements Parcelable {
    private String name;
    private String email;
    private String blood;

    public MainModel() {
    }

    public MainModel(String name, String email, String blood) {
        this.name = name;
        this.email = email;
        this.blood = blood;
    } //used when create new mainmodel with actual data

    protected MainModel(Parcel in) {
        name = in.readString();
        email = in.readString();
        blood = in.readString();
    } //used when reconstructing object from parcel ,must read values in same order it is written

    public static final Creator<MainModel> CREATOR = new Creator<MainModel>() { //tells how to create object from parcel
        @Override
        public MainModel createFromParcel(Parcel in) {
            return new MainModel(in);
        }

        @Override
        public MainModel[] newArray(int size) {
            return new MainModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getBlood() {
        return blood;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(blood);
    } //writes object to parcel order must match how you read data in constructor
}
