package com.example.myapplication.sectionpickersample.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by EgemenH on 02.06.2017.
 */

public class Country implements Parcelable {
    private String name;
    private String countryCode;
    private String bloodGroup;

    public Country(String name, String countryCode,String bloodGroup) {
        this.name = name;
        this.countryCode = countryCode;
        this.bloodGroup = bloodGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.countryCode);
    }

    protected Country(Parcel in) {
        this.name = in.readString();
        this.countryCode = in.readString();
    }

    public static final Creator<Country> CREATOR = new Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel source) {
            return new Country(source);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };
}
