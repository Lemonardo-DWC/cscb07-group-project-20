package com.example.smartair;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProviderChildDetailFragment extends Fragment {

    private String childUid;
    private String childName;
    private TextView childNameText;
    private TextView sharedFieldsText;

    public ProviderChildDetailFragment() {
        // Required empty public constructor
    }

    public static ProviderChildDetailFragment newInstance(String childUid, String childName) {
        ProviderChildDetailFragment fragment = new ProviderChildDetailFragment();
        Bundle args = new Bundle();
        args.putString("childUid", childUid);
        args.putString("childName", childName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_provider_child_detail, container, false);

        childNameText = view.findViewById(R.id.childNameText);
        sharedFieldsText = view.findViewById(R.id.sharedFieldsText);

        if (getArguments() != null) {
            childUid = getArguments().getString("childUid");
            childName = getArguments().getString("childName");
            childNameText.setText(childName);
        }

        fetchSharedFields();

        return view;
    }

    private void fetchSharedFields() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String providerUid = currentUser.getUid();
        DatabaseReference sharedRef = FirebaseDatabase.getInstance().getReference("providerShares")
                .child(providerUid)
                .child(childUid)
                .child("sharedFields");

        sharedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StringBuilder result = new StringBuilder();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Boolean isShared = item.getValue(Boolean.class);
                    if (Boolean.TRUE.equals(isShared)) {
                        result.append("• ").append(item.getKey()).append("\n");
                    }
                }
                sharedFieldsText.setText(result.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Failed to load shared fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
}