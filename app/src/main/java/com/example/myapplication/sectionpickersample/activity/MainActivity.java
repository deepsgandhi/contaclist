package com.example.myapplication.sectionpickersample.activity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.sectionpicker.BloodGroupSectionPicker;
import com.example.myapplication.sectionpicker.SectionPicker;
import com.example.myapplication.sectionpickersample.adapter.CountriesRecyclerViewAdapter;
import com.example.myapplication.sectionpickersample.model.CountriesRecyclerViewModel;
import com.example.myapplication.sectionpickersample.model.Country;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CountriesRecyclerViewAdapter.RowClickListener {

    private List<CountriesRecyclerViewModel> countriesRecyclerViewModels;
    private RecyclerView recyclerViewCountries;
    private SectionPicker sectionPickerCountries;
    private BloodGroupSectionPicker sectionpicker_bloodGroup;
    private TextView textViewSection;
    private CountriesRecyclerViewAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private TextView savedContactTV,bloodGroupTV;
    private EditText searchET;

    public String[] sections = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "NA"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);

        textViewSection = findViewById(R.id.textview_section);
        sectionPickerCountries = findViewById(R.id.sectionpicker_countries);
        sectionpicker_bloodGroup = findViewById(R.id.sectionpicker_bloodGroup);
        recyclerViewCountries = findViewById(R.id.recyclerview_countries);
        searchET = findViewById(R.id.searchET);

        savedContactTV = findViewById(R.id.savedContactTV);
        bloodGroupTV = findViewById(R.id.bloodGroupTV);

        setRecyclerViewLayoutManager();
        populateRecyclerView();
        initSectionPicker();

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("text",s.toString());
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        savedContactTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedContactTV.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                savedContactTV.setTextColor(Color.WHITE);
                bloodGroupTV.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DFE1E0")));
                bloodGroupTV.setTextColor(Color.BLACK);
                sectionPickerCountries.setVisibility(View.VISIBLE);
                sectionpicker_bloodGroup.setVisibility(View.GONE);
                populateRecyclerView();
                initSectionPicker();

            }
        });

        bloodGroupTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bloodGroupTV.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                bloodGroupTV.setTextColor(Color.WHITE);
                savedContactTV.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DFE1E0")));
                savedContactTV.setTextColor(Color.BLACK);
                sectionPickerCountries.setVisibility(View.GONE);
                sectionpicker_bloodGroup.setVisibility(View.VISIBLE);
                populateRecyclerViewByBloodGroup();
                initBloodGroupSectionPicker();
            }
        });
    }

    public void setRecyclerViewLayoutManager() {
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewCountries.setLayoutManager(linearLayoutManager);
    }

    private void populateRecyclerView() {
        List<Country> countries = new ArrayList<>();

        try {
            String jsonData = readJsonFromAssets("cont.json");
            if (jsonData != null) {
                JSONArray jsonArray = new JSONArray(jsonData);

                // Loop through the JSON array
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String name = jsonObject.getString("firstName");
                    String bloodGroup = jsonObject.getString("bloodGroup");


                    countries.add(new Country(name, "TR",bloodGroup));

                    // Print or process each object as needed

                }


                Collections.sort(countries, new Comparator<Country>() {
                    @Override
                    public int compare(Country p1, Country p2) {
                        return p1.getName().compareToIgnoreCase(p2.getName());
                    }
                });


            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        adapter = new CountriesRecyclerViewAdapter(transformCountriesForRecyclerView(countries), this, this,1);
        recyclerViewCountries.setAdapter(adapter);
    }

    private void populateRecyclerViewByBloodGroup() {
        List<Country> countries = new ArrayList<>();

        try {
            String jsonData = readJsonFromAssets("cont.json");
            if (jsonData != null) {
                JSONArray jsonArray = new JSONArray(jsonData);

                // Loop through the JSON array
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String name = jsonObject.getString("firstName");
                    String bloodGroup = jsonObject.getString("bloodGroup");


                    countries.add(new Country(name, "TR",bloodGroup));

                    // Print or process each object as needed

                }


                Collections.sort(countries, new Comparator<Country>() {
                    @Override
                    public int compare(Country p1, Country p2) {
                        return p1.getBloodGroup().compareTo(p2.getBloodGroup());
                    }
                });




            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        adapter = new CountriesRecyclerViewAdapter(transformBloodGroupForRecyclerView(countries), this, this,2);
        recyclerViewCountries.setAdapter(adapter);
    }


    private String readJsonFromAssets(String fileName) throws IOException {
        InputStream inputStream = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            inputStream = getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return stringBuilder.toString();
    }


    private void initSectionPicker() {
        Object[] sectionsAsObject = adapter.getSections();
        String[] sections = Arrays.copyOf(sectionsAsObject, sectionsAsObject.length, String[].class);

        sectionPickerCountries.setTextViewIndicator(textViewSection);
//        sectionPickerCountries.setSections(sections);
        sectionPickerCountries.setOnTouchingLetterChangedListener(new SectionPicker.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.charAt(0));

                if (position != -1) {
                    linearLayoutManager.scrollToPositionWithOffset(position, 0);
                }
            }
        });
    }

    private void initBloodGroupSectionPicker() {
//        Object[] sectionsAsObject = adapter.getSections();
//        String[] sections = Arrays.copyOf(sectionsAsObject, sectionsAsObject.length, String[].class);

        sectionpicker_bloodGroup.setTextViewIndicator(textViewSection);
//        sectionPickerCountries.setSections(sections);
        sectionpicker_bloodGroup.setOnTouchingLetterChangedListener(new BloodGroupSectionPicker.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForBloodGroupSection(s);

                if (position != -1) {
                    linearLayoutManager.scrollToPositionWithOffset(position, 0);
                }
            }
        });
    }

    private List<CountriesRecyclerViewModel> transformCountriesForRecyclerView(List<Country> countries) {
        countriesRecyclerViewModels = new ArrayList<>();
        if ((countries != null) && !countries.isEmpty()) {
            String letter = "";
            for (Country country : countries) {
                String countryLetter = country.getName().substring(0, 1);
                String bloodGroup = country.getBloodGroup();
                if (TextUtils.isEmpty(letter) || !letter.equalsIgnoreCase(countryLetter)) {
                    countriesRecyclerViewModels.add(new CountriesRecyclerViewModel(null, countryLetter, CountriesRecyclerViewAdapter.TYPE_LETTER,bloodGroup));
                    letter = countryLetter;
                }
                countriesRecyclerViewModels.add(new CountriesRecyclerViewModel(country, null, CountriesRecyclerViewAdapter.TYPE_COUNTRY,bloodGroup));
            }
        }
        return countriesRecyclerViewModels;
    }



    private List<CountriesRecyclerViewModel> transformBloodGroupForRecyclerView(List<Country> countries) {
        countriesRecyclerViewModels = new ArrayList<>();
        if ((countries != null) && !countries.isEmpty()) {
            String letter = "";
            for (Country country : countries) {
                String countryLetter = country.getName().substring(0, 1);
                String bloodGroup = country.getBloodGroup();
                if (TextUtils.isEmpty(letter) || !letter.equalsIgnoreCase(bloodGroup)) {
                    countriesRecyclerViewModels.add(new CountriesRecyclerViewModel(null, countryLetter, CountriesRecyclerViewAdapter.TYPE_LETTER,bloodGroup));
                    letter = bloodGroup;
                }
                countriesRecyclerViewModels.add(new CountriesRecyclerViewModel(country, null, CountriesRecyclerViewAdapter.TYPE_COUNTRY,bloodGroup));
            }
        }
        return countriesRecyclerViewModels;
    }



    @Override
    public void onRowClick(View view, int position) {
        CountriesRecyclerViewModel countriesRecyclerViewModel = countriesRecyclerViewModels.get(position);
        Country country = countriesRecyclerViewModel.getCountry();
        if (country != null) {
            Toast.makeText(this, country.getName(), Toast.LENGTH_SHORT).show();
        }
    }
}
