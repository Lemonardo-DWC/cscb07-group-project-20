package com.example.smartair;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProviderHomeFragment extends Fragment {

    private MaterialButton menuButton;
    private RecyclerView providerChildRecycler;

    private ProviderChildAdapter adapter;
    private ArrayList<ChildModel> childList = new ArrayList<>();

    private FirebaseUser currentUser;
    private DatabaseReference usersRef;
    private DatabaseReference invitesRef;

    private ProgressDialog loadingDialog;
    private final List<ChildModel> sharedChildren = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_provider_home, container, false);

        menuButton = view.findViewById(R.id.menu_button);
        providerChildRecycler = view.findViewById(R.id.providerChildRecycler);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        invitesRef = FirebaseDatabase.getInstance().getReference("invites");

        //setupRecycler();
        setupMenuButton();
        ListenToProviderShares();

        return view;
    }


    private void setupRecycler() {
        providerChildRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProviderChildAdapter(sharedChildren, (childUid, childName) -> {
            Fragment fragment = ProviderChildDetailFragment.newInstance(childUid, childName);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_view, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        providerChildRecycler.setAdapter(adapter);
    }


    private void ListenToProviderShares() {
        if (currentUser == null) return;

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sharedChildren.clear();

                for (DataSnapshot childSnap : snapshot.getChildren()) {

                    String type = childSnap.child("accountType").getValue(String.class);
                    if (type == null || !type.equals("child")) continue;

                    if (childSnap.child("providerShares").hasChild(currentUser.getUid())) {

                        String childId = childSnap.getKey();

                        String firstName = childSnap.child("basicInformation")
                                .child("firstName").getValue(String.class);

                        String lastName = childSnap.child("basicInformation")
                                .child("lastName").getValue(String.class);

                        sharedChildren.add(new ChildModel(childId, firstName, lastName));
                    }
                }

                setupRecycler();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load shared children.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupMenuButton() {

        menuButton.setOnClickListener(v -> {

            PopupMenu popup = new PopupMenu(getContext(), v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.provider_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {

                if (item.getItemId() == R.id.enter_invitation_code) {
                    showInviteInputDialog();
                    return true;
                }

                if (item.getItemId() == R.id.logout) {

                    FirebaseAuth.getInstance().signOut();

                    Intent intent = new Intent(requireContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    requireActivity().finish();

                    return true;
                }


                return false;
            });

            popup.show();
        });
    }


    private void showInviteInputDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter Invitation Code");

        final EditText input = new EditText(getContext());
        input.setHint("e.g. ABC123");
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        builder.setView(input);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String code = input.getText().toString().trim();
            if (!code.isEmpty()) {
                validateInviteCode(code);
            } else {
                Toast.makeText(getContext(), "Please enter a code.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }


    private void validateInviteCode(String code) {

        if (currentUser == null) {
            Toast.makeText(getContext(), "Not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading("Verifying...");

        invitesRef.child(code).get().addOnCompleteListener(task -> {

            if (!task.isSuccessful() || !task.getResult().exists()) {
                hideLoading();
                Toast.makeText(getContext(), "Invalid or expired code.", Toast.LENGTH_SHORT).show();
                return;
            }

            DataSnapshot snap = task.getResult();

            long createdAt = snap.child("createdAt").getValue(Long.class);
            long now = System.currentTimeMillis();
            long sevenDays = 7L * 24 * 60 * 60 * 1000;

            if (now - createdAt > sevenDays) {
                hideLoading();
                Toast.makeText(getContext(), "Invite code expired.", Toast.LENGTH_SHORT).show();
                return;
            }

            String providerEmail = snap.child("providerEmail").getValue(String.class);

            if (!currentUser.getEmail().equals(providerEmail)) {
                hideLoading();
                Toast.makeText(getContext(), "This code was not sent to your email.", Toast.LENGTH_SHORT).show();
                return;
            }

            String childId = snap.child("childId").getValue(String.class);

            Object dataObj = snap.child("sharedData").getValue();
            Map<String, Object> sharedData = new HashMap<>();
            if (dataObj instanceof Map) {
                sharedData.putAll((Map<String, Object>) dataObj);
            }

            // Write providerShares
            usersRef.child(childId)
                    .child("providerShares")
                    .child(currentUser.getUid())
                    .setValue(sharedData)
                    .addOnSuccessListener(a -> {

                        invitesRef.child(code).removeValue();

                        hideLoading();
                        Toast.makeText(getContext(), "Invite accepted!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        hideLoading();
                        Toast.makeText(getContext(), "Failed to accept invite.", Toast.LENGTH_SHORT).show();
                    });
        });
    }


    private void showLoading(String msg) {
        if (loadingDialog == null) {
            loadingDialog = new ProgressDialog(getContext());
            loadingDialog.setCancelable(false);
        }
        loadingDialog.setMessage(msg);
        loadingDialog.show();
    }

    private void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}
