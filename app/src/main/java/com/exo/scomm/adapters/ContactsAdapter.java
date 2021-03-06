package com.exo.scomm.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.exo.scomm.R;
import com.exo.scomm.data.models.Contact;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> implements Filterable {
    private final List<Contact> contactList;
    private final Context mCtxt;
    private final Set<Contact> contactListAll;
    private final ContactsAdapterListener listener;
    public Set<Contact> mSelectedContactsSet = new HashSet<>();

    public ContactsAdapter(Context context, Set<Contact> joinedContacts, ContactsAdapterListener listener) {
        this.contactList = new ArrayList<>(joinedContacts);
        this.mCtxt = context;
        this.contactListAll = new HashSet<>(joinedContacts);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == R.layout.contact_item) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_app_button, parent, false);
        }

        return new ContactsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position) {
        if (position == contactList.size()) {
            holder.button.setOnClickListener(view -> inviteUser()
            );
        } else {
            List<Contact> list = new ArrayList<>(contactList);
            Contact contact = list.get(position);
            holder.name.setText(contact.getName());
            holder.phoneNumber.setText(contact.getPhoneNumber());

            holder.cardView.setOnClickListener(v -> {
                if (holder.cardView.isChecked()) {
                    holder.cardView.setChecked(false);
                    mSelectedContactsSet.remove(contact);
                } else {
                    mSelectedContactsSet.add(contact);
                    listener.onContactSelected(contact);
                    holder.cardView.setChecked(true);
                }
            });
        }
    }

    private void inviteUser() {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This Will be link pto the app");
        sendIntent.setType("text/plain");
        Intent chooser = Intent.createChooser(sendIntent, "Share SComm with friends");
        if (sendIntent.resolveActivity(mCtxt.getPackageManager()) != null) {
            mCtxt.startActivity(chooser);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == contactList.size()) ? R.layout.share_app_button : R.layout.contact_item;
    }

    @Override
    public int getItemCount() {
        return contactList.size() + 1;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                List<Contact> filteredList = new ArrayList<>();
                if (charSequence.toString().isEmpty()) {
                    filteredList.addAll(contactListAll);
                } else {
                    for (Contact row : contactListAll) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charSequence.toString().toLowerCase()) || row.getPhoneNumber().contains(charSequence.toString())) {
                            filteredList.add(row);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactList.clear();
                contactList.addAll((Collection<? extends Contact>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }

    public interface ContactsAdapterListener {
        void onContactSelected(Contact contact);
    }

    public class ContactsViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView phoneNumber;
        MaterialCardView cardView;
        MaterialButton button;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.contact_item_name);
            phoneNumber = itemView.findViewById(R.id.contact_item_phone);
            cardView = itemView.findViewById(R.id.contact_card);
            button = (MaterialButton) itemView.findViewById(R.id.share_app_button);

            itemView.setOnClickListener(view -> {
                // send selected contact in callback
                listener.onContactSelected(contactList.get(getAdapterPosition()));
            });
        }

    }
}



