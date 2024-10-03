package com.example.myapplication.sectionpickersample.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.sectionpickersample.adapter.ContactAdapter;
import com.example.myapplication.sectionpickersample.model.Contact;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialerActivity extends AppCompatActivity {

    private RecyclerView contactRecyclerView;
    private ContactAdapter contactAdapter;
    private List<Contact> contactList = new ArrayList<>();
    private StringBuilder dialedNumber = new StringBuilder();
    private Map<Character, String> t9Map = new HashMap<>();
    private TextView dialedNumberText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialer);

        // Initialize T9 Mapping
        initializeT9Map();

        // Initialize UI elements
        contactRecyclerView = findViewById(R.id.contactRecyclerView);
        contactRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        contactAdapter = new ContactAdapter(contactList);
        contactRecyclerView.setAdapter(contactAdapter);

        dialedNumberText = findViewById(R.id.dialedNumberText);

        // Load contacts from JSON
        loadContactsFromJson();

        // Set listeners for dialpad buttons and clear button
        setDialpadListeners();

        // Set clear button listener for single-press and long-press actions
        Button clearButton = findViewById(R.id.button_clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLastDigit();
            }
        });

        clearButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clearDialedNumber();
                return true;
            }
        });
    }

    private void initializeT9Map() {
        t9Map.put('2', "abc");
        t9Map.put('3', "def");
        t9Map.put('4', "ghi");
        t9Map.put('5', "jkl");
        t9Map.put('6', "mno");
        t9Map.put('7', "pqrs");
        t9Map.put('8', "tuv");
        t9Map.put('9', "wxyz");
    }

    private void loadContactsFromJson() {
        try {
            // Load JSON from assets
            String jsonStr = loadJSONFromAsset(this, "contacts.json");

            if (jsonStr != null) {
                JSONArray jsonArray = new JSONArray(jsonStr);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    // Extract fields
                    String firstName = jsonObject.optString("firstName", "");
                    String lastName = jsonObject.optString("lastName", "");
                    String phoneNumber = jsonObject.optString("phoneNumber", "");

                    // Combine first and last names for the full name
                    String fullName = (firstName + " " + lastName).trim();

                    // Create a new contact and add to the list
                    contactList.add(new Contact(fullName, phoneNumber));
                }
                contactAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void setDialpadListeners() {
        Button[] dialButtons = {
                findViewById(R.id.button_1), findViewById(R.id.button_2),
                findViewById(R.id.button_3), findViewById(R.id.button_4),
                findViewById(R.id.button_5), findViewById(R.id.button_6),
                findViewById(R.id.button_7), findViewById(R.id.button_8),
                findViewById(R.id.button_9), findViewById(R.id.button_0)
        };

        for (Button button : dialButtons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Only add the digit to the dialed number (ignore alphabets)
                    String numberPressed = ((Button) v).getText().toString().substring(0, 1);
                    dialedNumber.append(numberPressed);
                    updateDialedNumberDisplay();
                    filterContactsByDialedNumber();
                }
            });
        }
    }

    private void updateDialedNumberDisplay() {
        dialedNumberText.setText(dialedNumber.toString());
    }

    private void deleteLastDigit() {
        if (dialedNumber.length() > 0) {
            dialedNumber.deleteCharAt(dialedNumber.length() - 1);
            updateDialedNumberDisplay();
            filterContactsByDialedNumber();
        }
    }

    private void clearDialedNumber() {
        dialedNumber.setLength(0);  // Clear the dialed number
        updateDialedNumberDisplay();  // Update the UI
        contactAdapter.updateList(contactList);  // Reset the list to all contacts
    }

    private void filterContactsByDialedNumber() {
        String dialString = dialedNumber.toString();
        if (TextUtils.isEmpty(dialString)) {
            contactAdapter.updateList(contactList);
            return;
        }

        List<Contact> filteredList = new ArrayList<>();
        for (Contact contact : contactList) {
            if (contact.getPhoneNumber().contains(dialString)) {
                filteredList.add(contact);
            } else if (matchesT9(contact.getName(), dialString)) {
                filteredList.add(contact);
            }
        }
        contactAdapter.updateList(filteredList);
    }

    private boolean matchesT9(String name, String dialString) {
        String lowerName = name.toLowerCase();
        if (dialString.length() > lowerName.length()) {
            return false;  // Dialed string is longer than the name
        }

        for (int i = 0; i < dialString.length(); i++) {
            char digit = dialString.charAt(i);
            String mappedChars = t9Map.get(digit);

            if (mappedChars == null || !mappedChars.contains(String.valueOf(lowerName.charAt(i)))) {
                return false;  // Name doesn't match the corresponding T9 digit
            }
        }
        return true;  // Name matches the T9 input
    }
}
