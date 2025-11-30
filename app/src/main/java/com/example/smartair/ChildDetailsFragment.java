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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Comparator;
import java.util.Locale;

public class ChildDetailsFragment extends Fragment {

    private ParentHomeViewModel phvm;
    private ChildItem selectedChildItem;
    private final ChildItemHelper childItemHelper = new ChildItemHelper();
    private final TimeHelper timeHelper = new TimeHelper();

    // basic information views
    private TextView navigationTitle;
    private TextView firstnameField, middleNameField, lastNameField;
    private TextView birthdayField, sexField;
    private TextView noteField;

    // other information views
    private TextView pbField,
                    dailyCheckInField,
                    lastControllerField,
                    lastRescueField,
                    lastPefField,
                    lastTriageField;

    // input views
    private TextInputEditText noteEditText, setPbEditText;

    // button views
    private MaterialButton backButton, editNoteButton, setPbButton, historyButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child_details, container, false);

        phvm = new ViewModelProvider(requireActivity()).get(ParentHomeViewModel.class);

        /// initialize views ///
        // basic information views
        navigationTitle = view.findViewById(R.id.screenID);
        firstnameField = view.findViewById(R.id.firstNameField);
        middleNameField = view.findViewById(R.id.middleNameField);
        lastNameField = view.findViewById(R.id.lastNameField);
        birthdayField = view.findViewById(R.id.birthdayField);
        sexField = view.findViewById(R.id.sexField);
        noteField = view.findViewById(R.id.noteField);

        // other information views
        pbField = view.findViewById(R.id.personalBest);
        dailyCheckInField = view.findViewById(R.id.latestDailyCheckIn);
        lastControllerField = view.findViewById(R.id.latestControllerLog);
        lastRescueField = view.findViewById(R.id.latestRescueLog);
        lastPefField = view.findViewById(R.id.latestPefLog);
        lastTriageField = view.findViewById(R.id.latestTriageSession);

        // button views
        backButton = view.findViewById(R.id.child_details_back_button);
        editNoteButton = view.findViewById(R.id.editNoteButton);
        setPbButton = view.findViewById(R.id.buttonSetPb);
        historyButton = view.findViewById(R.id.viewHistoryButton);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        phvm.selectedItem.observe(getViewLifecycleOwner(), childItem -> {

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

            // set other information
            setOtherInformation();

        });

        editNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditNotePopup();
            }
        });

        setPbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetPbPopup();
            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(
                        requireContext(),
                        "View history",
                        Toast.LENGTH_SHORT
                ).show();
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

    private void setOtherInformation() {

        String pbText,
                checkInText,
                controllerText,
                rescueText,
                pefText,
                triageText;

        DailyCheckIn checkIn;
        ControllerLogs controller;
        RescueLogs rescue;
        PefLogs pef;
        TriageSessions triage;

        pbText = "Personal best: " + selectedChildItem.getPb();

        checkIn = childItemHelper.getLastGenericLog(selectedChildItem.DailyCheckIn,
                ChildItemHelper.getDescendingTimeComparator());

        if (checkIn == null) {
            checkInText
                    = "Last check-in:\n" +
                    "    - Activity Limit:\n" +
                    "    - Cough/Wheeze:\n" +
                    "    - Night waking:\n" +
                    "    - Triggers:";
        } else {
            checkInText = String.format(
                    Locale.getDefault(),
                    "Last check-in: %s\n" +
                            "    - Activity Limit: %s\n" +
                            "    - Cough/Wheeze: %s\n" +
                            "    - Night waking: %s\n" +
                            "    - Triggers: %d",
                    timeHelper.formatTime(AppConstants.DATE_HMMDY, checkIn.gettimestamp()),
                    checkIn.activityLimit,
                    checkIn.coughWheeze,
                    checkIn.nightWaking,
                    checkIn.triggers.size()
            );
        }

        controller = childItemHelper.getLastGenericLog(selectedChildItem.controllerLogs,
                ChildItemHelper.getDescendingTimeComparator());

        if (controller == null) {
            controllerText
                    = "Last controller log:\n" +
                    "    - Doses:";
        } else {
            controllerText = String.format(Locale.getDefault(),
                    "Last controller log: %s\n" +
                            "    - Doses: %d",
                    timeHelper.formatTime(AppConstants.DATE_HMMDY, controller.gettimestamp()),
                    controller.dose);
        }

        rescue = childItemHelper.getLastGenericLog(selectedChildItem.rescueLogs,
                ChildItemHelper.getDescendingTimeComparator());

        if (rescue == null) {
            rescueText
                    = "Last rescue log:\n" +
                    "    - Doses:\n" +
                    "    - Pre/post rating:\n" +
                    "    - Post status:";
        } else {
            rescueText = String.format(Locale.getDefault(),
                    "Last rescue log: %s\n" +
                            "    - Doses: %d\n" +
                            "    - Pre/post rating: %d / %d\n" +
                            "    - Post status: %d",
                    timeHelper.formatTime(AppConstants.DATE_HMMDY, rescue.gettimestamp()),
                    rescue.dose,
                    rescue.preBreathRating,
                    rescue.postBreathRating,
                    rescue.postStatus);
        }

        pef = childItemHelper.getLastGenericLog(selectedChildItem.pefLogs,
                ChildItemHelper.getDescendingTimeComparator());

        if (pef == null) {
            pefText
                    = "Last PEF log:\n" +
                    "    - PEF:";
        } else {
            pefText = String.format(Locale.getDefault(),
                    "Last PEF log: %s\n" +
                            "    - PEF: %d",
                    timeHelper.formatTime(AppConstants.DATE_HMMDY, pef.gettimestamp()),
                    pef.pef);
        }

        triage = childItemHelper.getLastGenericLog(selectedChildItem.triageSessions,
                ChildItemHelper.getDescendingTimeComparator());

        if (triage == null) {
            triageText
                    = "Last triage session:\n" +
                    "    - Blue lips/nails:\n" +
                    "    - Chest pulling in:\n" +
                    "    - Dizzy/scared:\n" +
                    "    - Red flag:\n" +
                    "    - Can speak in full sentences:\n" +
                    "    - Used rescue medication:";
        } else {
            triageText = String.format(Locale.getDefault(),
                    "Last triage session: %s\n" +
                            "    - Blue lips/nails: %s\n" +
                            "    - Chest pulling in: %s\n" +
                            "    - Dizzy/scared: %s\n" +
                            "    - Red flag: %s\n" +
                            "    - Can speak in full sentences: %s\n" +
                            "    - Used rescue medication: %s",
                    timeHelper.formatTime(AppConstants.DATE_HMMDY, triage.gettimestamp()),
                    triage.blue_lips_nails,
                    triage.chest_pulling_in,
                    triage.dizzy_scared,
                    triage.red_flag_detected,
                    triage.speak_full_sentences,
                    triage.used_rescue_meds);
        }

        pbField.setText(pbText);
        dailyCheckInField.setText(checkInText);
        lastControllerField.setText(controllerText);
        lastRescueField.setText(rescueText);
        lastPefField.setText(pefText);
        lastTriageField.setText(triageText);

    }

    private void showEditNotePopup() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View popupView = inflater.inflate(R.layout.popup_edit_note, null);

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

    private void showSetPbPopup() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View popupView = inflater.inflate(R.layout.popup_set_pb, null);

        setPbEditText = popupView.findViewById(R.id.setPbEditText);

        new AlertDialog.Builder(requireContext())
                .setTitle("Set New PB for" + selectedChildItem.getFirstName())
                .setView(popupView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String temp = setPbEditText.getText().toString().trim();

                    if (temp.isEmpty()) {
                        selectedChildItem.pb = 0;
                    } else {
                        selectedChildItem.pb = Integer.parseInt(temp);
                    }

                    phvm.updateChildPB(selectedChildItem);

                    Toast.makeText(
                            requireContext(),
                            "New PB set",
                            Toast.LENGTH_SHORT
                    ).show();

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

}