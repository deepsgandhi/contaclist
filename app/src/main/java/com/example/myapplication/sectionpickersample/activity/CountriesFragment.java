package com.example.myapplication.sectionpickersample.activity;



import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
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

public class CountriesFragment extends Fragment implements CountriesRecyclerViewAdapter.RowClickListener {

    private List<CountriesRecyclerViewModel> countriesRecyclerViewModels;
    private RecyclerView recyclerViewCountries;
    private SectionPicker sectionPickerCountries;
    private TextView textViewSection;
    private CountriesRecyclerViewAdapter adapter;
    private LinearLayoutManager linearLayoutManager;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.activity_main_new, container, false);

        // Initialize the UI components
        textViewSection = view.findViewById(R.id.textview_section);
        sectionPickerCountries = view.findViewById(R.id.sectionpicker_countries);
        recyclerViewCountries = view.findViewById(R.id.recyclerview_countries);

        setRecyclerViewLayoutManager();
        populateRecyclerView();
        initSectionPicker();

        return view;
    }

    private void setRecyclerViewLayoutManager() {
        linearLayoutManager = new LinearLayoutManager(getContext());
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
                    countries.add(new Country(name, "TR"));
                }

                Collections.sort(countries, new Comparator<Country>() {
                    @Override
                    public int compare(Country p1, Country p2) {
                        return p1.getName().compareTo(p2.getName());
                    }
                });
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        adapter = new CountriesRecyclerViewAdapter(transformCountriesForRecyclerView(countries), getContext(), this);
        recyclerViewCountries.setAdapter(adapter);
    }

    private String readJsonFromAssets(String fileName) throws IOException {
        InputStream inputStream = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            inputStream = requireContext().getAssets().open(fileName);
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

    private List<CountriesRecyclerViewModel> transformCountriesForRecyclerView(List<Country> countries) {
        countriesRecyclerViewModels = new ArrayList<>();
        if ((countries != null) && !countries.isEmpty()) {
            String letter = "";
            for (Country country : countries) {
                String countryLetter = country.getName().substring(0, 1);
                if (TextUtils.isEmpty(letter) || !letter.equals(countryLetter)) {
                    countriesRecyclerViewModels.add(new CountriesRecyclerViewModel(null, countryLetter, CountriesRecyclerViewAdapter.TYPE_LETTER));
                    letter = countryLetter;
                }
                countriesRecyclerViewModels.add(new CountriesRecyclerViewModel(country, null, CountriesRecyclerViewAdapter.TYPE_COUNTRY));
            }
        }
        return countriesRecyclerViewModels;
    }

    @Override
    public void onRowClick(View view, int position) {
        CountriesRecyclerViewModel countriesRecyclerViewModel = countriesRecyclerViewModels.get(position);
        Country country = countriesRecyclerViewModel.getCountry();
        if (country != null) {
            Toast.makeText(getContext(), country.getName(), Toast.LENGTH_SHORT).show();
        }
    }
}
