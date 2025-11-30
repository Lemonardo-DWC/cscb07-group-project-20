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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChildProviderShareFragment extends Fragment {

    private String childId;
    private String childName;

    private TextView header;
    private RecyclerView providerRecyclerView;
    private Button inviteButton;

    private ProviderListAdapter providerAdapter;

    public ChildProviderShareFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_child_provider_share, container, false);

        childId = getArguments().getString("childId");
        childName = getArguments().getString("childName");

        header = view.findViewById(R.id.childProviderHeader);
        providerRecyclerView = view.findViewById(R.id.providerListRecyclerView);
        inviteButton = view.findViewById(R.id.inteProviderButton);

        header.setText("Sharing for " + childName);

        setupProviderRecycler();
        loadProvidersFromFirebase();

        inviteButton.setOnClickListener(v -> openInviteProviderPage());

        return view;
    }

    private void setupProviderRecycler() {

        providerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        providerAdapter = new ProviderListAdapter(new ArrayList<>(), provider -> {
            openProviderManagementPage(provider);
        });

        providerRecyclerView.setAdapter(providerAdapter);
    }

    private void loadProvidersFromFirebase() {

        FirebaseDatabase.getInstance().getReference("users")
                .child(childId)
                .child("providerList")   // RTDB path: /users/{childId}/providerList/{providerUid}
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        List<Provider> providerList = new ArrayList<>();

                        for (DataSnapshot s : snapshot.getChildren()) {

                            String providerUid = s.getKey();

                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(providerUid)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot providerSnap) {

                                            Provider p = providerSnap.getValue(Provider.class);
                                            if (p != null) {
                                                p.setUid(providerUid);   // FIXED: use setter
                                                providerList.add(p);
                                                providerAdapter.updateList(providerList);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {}
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load providers.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // ⭐ FIXED: provider.name → provider.getName()
    // ⭐ FIXED: provider.uid  → provider.getUid()
    private void openProviderManagementPage(Provider provider) {

        ProviderManageFragment fragment = new ProviderManageFragment();

        Bundle args = new Bundle();
        args.putString("childId", childId);
        args.putString("providerUid", provider.getUid());
        args.putString("providerName", provider.getName());

        fragment.setArguments(args);

        getParentFragmentManager()
                .beginTransaction()
                .replace(((ViewGroup)getView().getParent()).getId(), fragment)
                .addToBackStack(null)
                .commit();
    }

    private void openInviteProviderPage() {

        InviteProviderFragment fragment = new InviteProviderFragment();

        Bundle args = new Bundle();
        args.putString("childId", childId);
        args.putString("childName", childName);

        fragment.setArguments(args);

        getParentFragmentManager()
                .beginTransaction()
                .replace(((ViewGroup)getView().getParent()).getId(), fragment)
                .addToBackStack(null)
                .commit();
    }
}

