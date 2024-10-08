package com.example.myapplication.sectionpickersample.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

import android.provider.CallLog;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class DialerActivity extends AppCompatActivity {

    private RecyclerView contactRecyclerView;
    private ContactAdapter contactAdapter;
    private List<Contact> contactList = new ArrayList<>();
    List<Contact> filteredList = new ArrayList<>();
    private StringBuilder dialedNumber = new StringBuilder();
    private Map<Character, String> t9Map = new HashMap<>();
    private TextView dialedNumberText, nameTV, mobileTV;
    private LinearLayout topLL, bottomLL, keypadLL, nameLL;
    private ImageView clearIV;

    int selectedPos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialer);

        // Initialize T9 Mapping
        initializeT9Map();

        topLL = findViewById(R.id.topLL);
        bottomLL = findViewById(R.id.bottomLL);

        keypadLL = findViewById(R.id.keypadLL);
        nameLL = findViewById(R.id.nameLL);

        clearIV = findViewById(R.id.clearIV);
        nameTV = findViewById(R.id.nameTV);
        mobileTV = findViewById(R.id.mobileTV);

        // Initialize UI elements
        contactRecyclerView = findViewById(R.id.contactRecyclerView);
//        contactRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        NoScrollLinearLayoutManager layoutManager = new NoScrollLinearLayoutManager(this);
        contactRecyclerView.setLayoutManager(layoutManager);
        contactAdapter = new ContactAdapter(contactList);
        contactRecyclerView.setAdapter(contactAdapter);

        dialedNumberText = findViewById(R.id.dialedNumberText);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, 1);
        } else {
            getCallDetails();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        }


        // Load contacts from JSON
        loadContactsFromJson();

        // Set listeners for dialpad buttons and clear button
        setDialpadListeners();

        // Set clear button listener for single-press and long-press actions
        ImageView clearButton = findViewById(R.id.button_clear);
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

        topLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (filteredList.isEmpty()) {
                    if (selectedPos > 0) {
                        contactAdapter.setSelectedPosition(--selectedPos);
                        contactRecyclerView.scrollToPosition(selectedPos);
                    }
                    nameTV.setText(contactList.get(selectedPos).getName());
                    mobileTV.setText(contactList.get(selectedPos).getPhoneNumber());
                } else {
                    if (selectedPos > 0) {
                        contactAdapter.setSelectedPosition(--selectedPos);
                        contactRecyclerView.scrollToPosition(selectedPos);
                        nameTV.setText(filteredList.get(selectedPos).getName());
                        mobileTV.setText(filteredList.get(selectedPos).getPhoneNumber());
                    }

                }


            }
        });

        bottomLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keypadLL.setVisibility(View.GONE);
                nameLL.setVisibility(View.VISIBLE);
                if (filteredList.isEmpty()) {
                    Log.e("sizeeee", "" + contactList.size());
                    Log.e("selectedPos", "" + selectedPos);
                    Log.e("sizeeee-1", "" + (contactList.size() - 1));
                    if (selectedPos < contactList.size() - 1) {
                        contactAdapter.setSelectedPosition(++selectedPos);
                        contactRecyclerView.scrollToPosition(selectedPos);
                    }
                    nameTV.setText(contactList.get(selectedPos).getName());
                    mobileTV.setText(contactList.get(selectedPos).getPhoneNumber());
                } else {
                    Log.e("sizeeee", "" + filteredList.size());
                    Log.e("selectedPos", "" + selectedPos);
                    Log.e("sizeeee-1", "" + (filteredList.size() - 1));
                    if (selectedPos < filteredList.size() - 1) {
                        contactAdapter.setSelectedPosition(++selectedPos);
                        contactRecyclerView.scrollToPosition(selectedPos);
                    }
                    nameTV.setText(filteredList.get(selectedPos).getName());
                    mobileTV.setText(filteredList.get(selectedPos).getPhoneNumber());
                }


            }
        });


        clearIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keypadLL.setVisibility(View.VISIBLE);
                nameLL.setVisibility(View.GONE);
            }
        });

        // Set the click listener for the adapter
        contactAdapter.setOnItemClickListener(new ContactAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item) {
                dialedNumberText.setText(item);
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

                contactAdapter = new ContactAdapter(contactList);
                contactRecyclerView.setAdapter(contactAdapter);

//                contactAdapter.notifyDataSetChanged();

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
        LinearLayout[] dialButtons = {
                findViewById(R.id.button_1), findViewById(R.id.button_2),
                findViewById(R.id.button_3), findViewById(R.id.button_4),
                findViewById(R.id.button_5), findViewById(R.id.button_6),
                findViewById(R.id.button_7), findViewById(R.id.button_8),
                findViewById(R.id.button_9), findViewById(R.id.button_0)
        };

        for (LinearLayout button : dialButtons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPos = -1;
                    contactAdapter.setSelectedPosition(-1);
                    // Only add the digit to the dialed number (ignore alphabets)
                    TextView tt = (TextView) button.getChildAt(0);
                    String numberPressed = tt.getText().toString();
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

        filteredList = new ArrayList<>();
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


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("CallLog", "Permission accept to read call logs");
                // Permission granted
                getCallDetails();
            } else {
                // Permission denied
                Log.d("CallLog", "Permission denied to read call logs");
            }
        }
    }

    public void getCallDetails() {
        Uri callLogUri = CallLog.Calls.CONTENT_URI;

        // The columns to retrieve
        String[] projection = new String[]{
                CallLog.Calls.NUMBER,        // Phone number
                CallLog.Calls.TYPE,          // Type of call (Incoming, Outgoing, Missed)
                CallLog.Calls.DATE,          // Date of the call
                CallLog.Calls.DURATION,       // Call duration
                CallLog.Calls.PHONE_ACCOUNT_ID  // SIM information column
        };

        Cursor cursor = getContentResolver().query(callLogUri, projection, null, null, CallLog.Calls.DATE + " DESC");

        Log.e("enter", "before enter");
        if (cursor != null) {
            Log.e("enter", "enter");
            while (cursor.moveToNext()) {
                String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
                String callType = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE));
                String callDate = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));
                String callDuration = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION));
                String phoneAccountId = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.PHONE_ACCOUNT_ID));


                // Convert the call type (1: Incoming, 2: Outgoing, 3: Missed)
                int callTypeCode = Integer.parseInt(callType);
                String callTypeStr = "";
                switch (callTypeCode) {
                    case CallLog.Calls.INCOMING_TYPE:
                        callTypeStr = "Incoming";
                        break;
                    case CallLog.Calls.OUTGOING_TYPE:
                        callTypeStr = "Outgoing";
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        callTypeStr = "Missed";
                        break;
                }
                getSimSlotFromIccid(this,phoneAccountId);

                // Log the details (you can also store or display this information)
                Log.d("CallLog", "Number: " + phoneNumber + ", Type: " + callTypeStr +
                        ", Date: " + callDate + ", Duration: " + callDuration + " seconds" + ", SIM TYPE :" + phoneAccountId);
            }
            cursor.close();
        }
    }

    public void getSimSlotFromIccid(Context context, String targetIccid) {
        // Get the Subscription Manager and List of SIM cards (dual-SIM support)
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

        // List of subscriptions (SIM information)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();

        if (subscriptionInfoList != null) {
            // Loop through SIM slots
            for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
                int simSlotIndex = subscriptionInfo.getSimSlotIndex();  // SIM slot index (0 = SIM1, 1 = SIM2)
                String iccid = subscriptionInfo.getIccId();             // Get ICCID of the SIM

                // Compare ICCID with the target ICCID
                if (iccid != null && iccid.equals(targetIccid)) {
                    if (simSlotIndex == 0) {
                        Log.d("SIM Info", "The ICCID " + targetIccid + " belongs to SIM 1 (Slot 0)");
                    } else if (simSlotIndex == 1) {
                        Log.d("SIM Info", "The ICCID " + targetIccid + " belongs to SIM 2 (Slot 1)");
                    }
                }
            }
        } else {
            Log.d("SIM Info", "No SIM cards detected.");
        }
    }

        public class NoScrollLinearLayoutManager extends LinearLayoutManager {

        public NoScrollLinearLayoutManager(Context context) {
            super(context);
        }

        @Override
        public boolean canScrollVertically() {
            // Returning false disables vertical scrolling
            return false;
        }

        @Override
        public boolean canScrollHorizontally() {
            // If you want to disable horizontal scrolling, return false here
            return false;
        }
    }

}
