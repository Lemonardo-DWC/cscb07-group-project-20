package com.example.smartair;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChildProviderShareFragment extends Fragment {

    private RecyclerView providerRecycler;
    private ProviderListAdapter providerAdapter;
    private ArrayList<Provider> providerList = new ArrayList<>();

    private String childId;

    private DatabaseReference userRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_child_provider_share, container, false);

        providerRecycler = view.findViewById(R.id.providerListRecyclerView);
        providerRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        providerAdapter = new ProviderListAdapter(providerList, providerClickListener);
        providerRecycler.setAdapter(providerAdapter);

        Button inviteButton = view.findViewById(R.id.inviteProviderButton);

        userRef = FirebaseDatabase.getInstance().getReference("users");

        if (getArguments() != null) {
            childId = getArguments().getString("childId");
        }

        inviteButton.setOnClickListener(v -> {
            InviteProviderFragment fragment = new InviteProviderFragment();
            Bundle args = new Bundle();
            args.putString("childId", childId);
            fragment.setArguments(args);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_view, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        loadProvidersForChild();

        return view;
    }


    private void loadProvidersForChild() {

        providerList.clear();

        userRef.child(childId).child("providerShares")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot sharesSnap) {

                        providerList.clear();

                        for (DataSnapshot share : sharesSnap.getChildren()) {

                            String providerId = share.getKey();

                            userRef.child(providerId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot pSnap) {

                                            String first = pSnap.child("firstName").getValue(String.class);
                                            String last = pSnap.child("lastName").getValue(String.class);
                                            String email = pSnap.child("email").getValue(String.class);

                                            providerList.add(
                                                    new Provider(providerId, first, last, email)
                                            );

                                            providerAdapter.notifyDataSetChanged();
                                        }

                                        @Override public void onCancelled(@NonNull DatabaseError error) {}
                                    });
                        }
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }


    private ProviderListAdapter.OnProviderClickListener providerClickListener = provider -> {

        ProviderManageFragment fragment = new ProviderManageFragment();
        Bundle bundle = new Bundle();

        bundle.putString("providerId", provider.getUid());
        bundle.putString("providerName", provider.getFullName());
        bundle.putString("providerEmail", provider.getEmail());
        bundle.putString("childId", childId);

        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_view, fragment)
                .addToBackStack(null)
                .commit();
    };
}

