package com.example.smartair;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ChildDetailsFragment extends Fragment {

    private ParentHomeViewModel phvm;
    private ChildItem selectedChildItem;

    private TextView navigationTitle;
    private TextView firstnameField, middleNameField, lastNameField;
    private TextView birthdayField, sexField;
    private TextView noteField;

    private TextInputEditText noteEditText;

    private MaterialButton backButton, editNoteButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child_details, container, false);

        phvm = new ViewModelProvider(requireActivity()).get(ParentHomeViewModel.class);

        // initialize views
        navigationTitle = view.findViewById(R.id.screenID);
        firstnameField = view.findViewById(R.id.firstNameField);
        middleNameField = view.findViewById(R.id.middleNameField);
        lastNameField = view.findViewById(R.id.lastNameField);
        birthdayField = view.findViewById(R.id.birthdayField);
        sexField = view.findViewById(R.id.sexField);
        noteField = view.findViewById(R.id.noteField);

        backButton = view.findViewById(R.id.child_details_back_button);
        editNoteButton = view.findViewById(R.id.editNoteButton);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // load selected childItem
        phvm.getSelectedItem().observe(getViewLifecycleOwner(), childItem -> {

            if (childItem == null) {
                Toast.makeText(requireContext(),
                        "Could not load child details",
                        Toast.LENGTH_SHORT
                ).show();
                getParentFragmentManager().popBackStack();
            }

            selectedChildItem = childItem;

            // set basic information
            setBasicInformation();

        });

        editNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditNotePopup();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

    }

    private void setBasicInformation() {
        navigationTitle.setText(selectedChildItem.getFirstName());
        firstnameField.setText(String.format("First name: %s", selectedChildItem.getFirstName()));
        middleNameField.setText(String.format("Middle name: %s", selectedChildItem.getMiddleName()));
        lastNameField.setText(String.format("Last name: %s", selectedChildItem.getLastName()));
        birthdayField.setText(String.format("Date of birth: %s", selectedChildItem.getBirthday()));
        sexField.setText(String.format("Sex: %s", selectedChildItem.getSex()));
        noteField.setText(selectedChildItem.getNotes());

    }

    private void showEditNotePopup() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View popupView = inflater.inflate(R.layout.edit_note_popup, null);

        noteEditText = popupView.findViewById(R.id.noteEditText);

        noteEditText.setText(selectedChildItem.getNotes());

        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Note")
                .setView(popupView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newNote = noteEditText.getText().toString().trim();

                    noteField.setText(newNote);

                    selectedChildItem.notes = newNote;
                    phvm.updateChildNote(selectedChildItem);

                    Toast.makeText(
                            requireContext(),
                            "Note saved",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .setNegativeButton("Cancel", null)
                .show();


    }

}