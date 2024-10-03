package com.example.myapplication.sectionpickersample.model;


import androidx.annotation.Nullable;

/**
 * Created by EgemenH on 02.06.2017.
 */

public class CountriesRecyclerViewModel {
    private Country country;
    private String letter;
    private int type;
    private String bloodGroup;

    public CountriesRecyclerViewModel(@Nullable Country country, String letter, int type,String bloodGroup) {
        this.country = country;
        this.letter = letter;
        this.type = type;
        this.bloodGroup = bloodGroup;
    }

    @Nullable
    public Country getCountry() {
        return country;
    }

    public String getLetter() {
        return letter;
    }

    public int getType() {
        return type;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }
}
