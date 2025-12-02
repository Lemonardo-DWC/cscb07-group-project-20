package com.example.smartair;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class ShareWithProviderFragment extends Fragment {

    private RecyclerView childListRecyclerView;
    private ChildSelectableAdapter adapter;
    private List<Child> childList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share_with_provider, container, false);

        childListRecyclerView = view.findViewById(R.id.childListRecyclerView);
        childListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ChildSelectableAdapter(childList, this::openChildProviderSharePage);
        childListRecyclerView.setAdapter(adapter);

        loadChildren();

        return view;
    }

    private void loadChildren() {
        String parentId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("users")
                .child(parentId)
                .child("childrenList")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        childList.clear();

                        for (DataSnapshot s : snapshot.getChildren()) {

                            String childUid = s.getValue(String.class);

                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(childUid)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot childSnap) {
                                            Child child = childSnap.getValue(Child.class);
                                            if (child != null) {
                                                child.setUid(childUid);
                                                childList.add(child);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) { }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) { }
                });
    }



    private void openChildProviderSharePage(Child child) {

        ChildProviderShareFragment fragment = new ChildProviderShareFragment();

        Bundle args = new Bundle();

        BasicInformationProvider info = child.getBasicInformation();
        String fullName = info.getFirstName() + " " + info.getLastName();

        args.putString("childId", child.getUid());
        args.putString("childName", fullName);

        fragment.setArguments(args);

        getParentFragmentManager()
                .beginTransaction()
                .replace(((ViewGroup)getView().getParent()).getId(), fragment)
                .addToBackStack(null)
                .commit();
    }


}

