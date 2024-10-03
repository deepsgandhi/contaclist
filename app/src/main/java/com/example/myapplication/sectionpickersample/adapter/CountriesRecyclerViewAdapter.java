package com.example.myapplication.sectionpickersample.adapter;

import android.content.Context;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.sectionpickersample.model.CountriesRecyclerViewModel;
import com.example.myapplication.sectionpickersample.model.Country;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EgemenH on 02.06.2017.
 */

public class CountriesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SectionIndexer {
    public static final int TYPE_COUNTRY = 0;
    public static final int TYPE_LETTER = 1;

    private final List<CountriesRecyclerViewModel> orieginalList;
    private final List<CountriesRecyclerViewModel> countries;
    private final Context context;
    private final RowClickListener rowClickListener;

    int type =0;

    public CountriesRecyclerViewAdapter(@NonNull List<CountriesRecyclerViewModel> countries, @NonNull Context context, @NonNull RowClickListener rowClickListener,int type) {
        this.countries = countries;
        this.context = context;
        this.rowClickListener = rowClickListener;
        this.type=type;
        orieginalList = new ArrayList<>(countries); // Copy the original list
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        switch (viewType) {
            case TYPE_COUNTRY: {
                View view = LayoutInflater.from(context).inflate(R.layout.row_recyclerview_country, parent, false);
                final CountryViewHolder viewHolder = new CountryViewHolder(view);

                viewHolder.textViewCountry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = viewHolder.getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            rowClickListener.onRowClick(v, position);
                        }
                    }
                });

                return viewHolder;
            }
            case TYPE_LETTER: {
                View view = LayoutInflater.from(context).inflate(R.layout.letter_layout, parent, false);
                return new LetterViewHolder(view);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CountriesRecyclerViewModel countriesRecyclerViewModel = countries.get(position);
        if (countriesRecyclerViewModel != null) {
            Log.e("position",""+position);
            switch (getItemViewType(position)) {
                case TYPE_COUNTRY: {
                    Country country = countriesRecyclerViewModel.getCountry();
                    if (country != null) {
                        ((CountryViewHolder) holder).bindTo(countriesRecyclerViewModel.getCountry(), context);
                    }
                    break;
                }
                case TYPE_LETTER: {

                    if(type == 1) {
                        ((LetterViewHolder) holder).bindTo(countriesRecyclerViewModel.getLetter().substring(0,1));
                    }
                    else
                    {
                        ((LetterViewHolder) holder).bindTo(countriesRecyclerViewModel.getBloodGroup());

                    }
                    break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return (countries != null) ? countries.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        CountriesRecyclerViewModel country = orieginalList.get(position);
        if (country != null) {
            return country.getType();
        }
        return super.getItemViewType(position);
    }

    @Override
    public Object[] getSections() {
        List<String> sectionList = new ArrayList<>();

        for (CountriesRecyclerViewModel country : countries) {
            if (country.getType() == TYPE_LETTER) {
                sectionList.add(country.getLetter());
            }
        }

        return sectionList.toArray();
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0, size = countries.size(); i < size; i++) {
            CountriesRecyclerViewModel countriesRecyclerViewModel = countries.get(i);
            if (countriesRecyclerViewModel.getType() == TYPE_LETTER) {
                String sortStr = countriesRecyclerViewModel.getLetter();
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == sectionIndex) {
                    return i;
                }
            }
        }

        return -1;
    }

    public int getPositionForBloodGroupSection(String bloodgroup) {
        for (int i = 0, size = countries.size(); i < size; i++) {
            CountriesRecyclerViewModel countriesRecyclerViewModel = countries.get(i);
                String sortStr = countriesRecyclerViewModel.getBloodGroup();
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (sortStr.equalsIgnoreCase(bloodgroup)) {
                    return i;
                }

        }

        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        int realSize = getItemCount();
        if (position >= realSize) {
            position = realSize - 1;
        }

        CountriesRecyclerViewModel countriesRecyclerViewModel = countries.get(position);
        Object[] sectionArray = getSections();

        String letter = "";
        switch (countriesRecyclerViewModel.getType()) {
            case TYPE_COUNTRY: {
                Country country = countriesRecyclerViewModel.getCountry();
                if (country != null) {
                    letter = country.getName().substring(0, 1);
                }
                break;
            }
            case TYPE_LETTER: {
                letter = countriesRecyclerViewModel.getLetter();
                break;
            }
        }

        for (int i = 0; i < sectionArray.length; i++) {
            if (sectionArray[i].toString().equals(letter)) {
                return i;
            }
        }
        return -1;
    }

    public static class CountryViewHolder extends RecyclerView.ViewHolder {

        TextView textViewCountry,despName,firstLetter;

        public CountryViewHolder(View itemView) {
            super(itemView);
            textViewCountry = itemView.findViewById(R.id.textview_country);
            despName = itemView.findViewById(R.id.despName);
            firstLetter = itemView.findViewById(R.id.firstLetter);
        }

        public void bindTo(@NonNull Country country, @NonNull Context context) {
            textViewCountry.setText(country.getName());
            despName.setText(country.getName());
            firstLetter.setText(country.getName().substring(0,1));
//            textViewCountry.setCompoundDrawablesWithIntrinsicBounds(FlagKit.drawableWithFlag(context, country.getCountryCode().toLowerCase()), null, null, null);
        }
    }

    public static class LetterViewHolder extends RecyclerView.ViewHolder {

        TextView firstLetter;


        public LetterViewHolder(View itemView) {
            super(itemView);

            firstLetter = itemView.findViewById(R.id.letter_text);
        }

        public void bindTo(@NonNull String letter) {

            firstLetter.setText(letter);
        }
    }

    public interface RowClickListener {
        void onRowClick(View view, int position);
    }

    // Filter method to filter data in the adapter
    public void filter(String text) {
        countries.clear();
        if (text.isEmpty()) {
            countries.addAll(orieginalList);
        } else {
            Log.e("textttt",text);
            text = text.toLowerCase();
            Log.e("size",""+orieginalList.size());
            int index = 0;
            for (CountriesRecyclerViewModel item : orieginalList) {
                Log.e("enter","enter"+index);
                if (item != null) {
                    switch (getItemViewType(index++)) {
                        case TYPE_COUNTRY: {
                            Country country = item.getCountry();
                            if (country.getName().toLowerCase().contains(text)) {
                                Log.e("item",item.getCountry().getName());
                                countries.add(item);
                            }
                            break;
                        }
                        case TYPE_LETTER: {
                            String country = item.getLetter();
                            if (country.toLowerCase().contains(text)) {
//                                assert item.getCountry() != null;
//                                Log.e("item",item.getCountry().getName());
                                countries.add(item);
                            }
                            break;
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

}