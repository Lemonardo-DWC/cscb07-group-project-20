package com.example.smartair;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;

public class ProviderHomeFragment extends Fragment {

    private MaterialButton menuButton;
    private RecyclerView providerChildRecycler;
    private MaterialButtonToggleGroup trendRangeToggleGroup;
    private final UserManager userManager = new UserManager();

    public ProviderHomeFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_provider_home, container, false);

        // Bind UI components
        menuButton = view.findViewById(R.id.menu_button);
        providerChildRecycler = view.findViewById(R.id.providerChildRecycler);
        trendRangeToggleGroup = view.findViewById(R.id.trendRangeToggleGroup);


        setupMenuActions();
        setupRecyclerView();

        return view;
    }

    private void setupMenuActions() {
        menuButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getContext(), v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.provider_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.logout) {
                    userManager.logout();
                    ((MainActivity) requireActivity()).loadFragment(new LoginFragment());

                }
                else if (id == R.id.enter_invitation_code) {
                    showInvitationCodeDialog();
                    return true;
                }
                return false;
            });

            popup.show();
        });
    }

    private void setupRecyclerView() {
        // TODO: Connect to Firebase or data layer
        // providerChildRecycler.setAdapter(new ProviderChildAdapter(...));
    }
    private void showInvitationCodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter Invitation Code");

        final EditText input = new EditText(getContext());
        input.setHint("e.g. ABC123");
        builder.setView(input);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String code = input.getText().toString().trim();
            if (!code.isEmpty()) {
                fetchSharedChildByCode(code);
            } else {
                Toast.makeText(getContext(), "Code cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void fetchSharedChildByCode(String code) {
        // TODO: Replace with Firebase logic
        Toast.makeText(getContext(), "Searching for child with code: " + code, Toast.LENGTH_SHORT).show();
    }


}
