package com.example.myapplication.sectionpickersample.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.sectionpickersample.model.Contact;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<Contact> contacts;
    private int selectedPosition = -1;
    private OnItemClickListener onItemClickListener;

    public ContactAdapter(List<Contact> contacts) {
        this.contacts = contacts;
    }

    // Interface for item click
    public interface OnItemClickListener {
        void onItemClick(String item);  // Sends the clicked item to the activity
    }

    // Setter for the click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.row_recyclerview_country, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.firstLetterLL.setVisibility(View.GONE);
        holder.profileIV.setVisibility(View.VISIBLE);
        holder.textViewCountry.setText(contact.getName());
        holder.despName.setText(contact.getPhoneNumber());

        // Highlight selected item
        if (selectedPosition == position) {
            holder.itemView.setBackgroundResource(R.drawable.selected_contact);
            holder.itemView.setPadding(12,0,0,0);
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
            holder.itemView.setPadding(12,0,0,0);
        }

        // Set the click listener on the itemView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(contact.getPhoneNumber());  // Pass the clicked item back to the activity
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged(); // Refresh the adapter
    }

    public void updateList(List<Contact> updatedContacts) {
        contacts = updatedContacts;
        notifyDataSetChanged();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

//        TextView nameTextView;
//        TextView phoneTextView;

        TextView textViewCountry, despName, firstLetter;
        LinearLayout firstLetterLL;
        ImageView profileIV;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCountry = itemView.findViewById(R.id.textview_country);
            despName = itemView.findViewById(R.id.despName);
            firstLetter = itemView.findViewById(R.id.firstLetter);
            firstLetterLL = itemView.findViewById(R.id.firstLetterLL);
            profileIV = itemView.findViewById(R.id.profileIV);

//            nameTextView = itemView.findViewById(android.R.id.text1);
//            phoneTextView = itemView.findViewById(android.R.id.text2);
        }
    }
}
