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
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;

import com.applandeo.materialcalendarview.CalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChildDetailsFragment extends Fragment {

    private ParentHomeViewModel phvm;
    private ChildItem selectedChildItem;
    private final ChildItemHelper childItemHelper = new ChildItemHelper();
    private final TimeHelper timeHelper = new TimeHelper();
    private final DataManager dataManager = new DataManager();

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
                    lastTriageField,
                    adherenceField;

    // input views
    private TextInputEditText noteEditText, setPbEditText;

    // button views
    private MaterialButton backButton, editNoteButton, setPbButton, historyButton, checkInButton,
                            setAdherenceScheduleButton;

    // parent assisted check in
    MaterialButtonToggleGroup nwToggleGroup, alToggleGroup, cwToggleGroup;
    String nwText = "";
    String alText = "";
    String cwText = "";

    CheckBox exerciseTrigger, coldAirTrigger, dustTrigger, petsTrigger, smokeTrigger,
            illnessTrigger, cleanerTrigger, smellTrigger;

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
        adherenceField = view.findViewById(R.id.adherenceInfo);

        // button views
        backButton = view.findViewById(R.id.child_details_back_button);
        editNoteButton = view.findViewById(R.id.editNoteButton);
        setPbButton = view.findViewById(R.id.buttonSetPb);
        historyButton = view.findViewById(R.id.viewHistoryButton);
        checkInButton = view.findViewById(R.id.logCheckInButton);
        setAdherenceScheduleButton = view.findViewById(R.id.setAdherenceSchedule);

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
                ((MainActivity) requireActivity()).loadFragment(new ChildHistoryFragment());
            }
        });

        checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCheckInPopup();
            }
        });

        setAdherenceScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdherencePicker();
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
                triageText,
                adherenceText;

        DailyCheckIn checkIn;
        ControllerLogs controller;
        RescueLogs rescue;
        PefLogs pef;
        TriageSessions triage;
        List<Long> plannedControllerSchedule;


        // pb
        pbText = "Personal best: " + selectedChildItem.getPb();

        // daily check in
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

            int triggerCount;

            if (checkIn.triggers != null) {
                triggerCount = checkIn.triggers.size();
            } else {
                triggerCount = 0;
            }

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
                    triggerCount
            );
        }

        // controller
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

        // rescue
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
                            "    - Post status: %s",
                    timeHelper.formatTime(AppConstants.DATE_HMMDY, rescue.gettimestamp()),
                    rescue.dose,
                    rescue.preBreathRating,
                    rescue.postBreathRating,
                    rescue.parsePostStatus());
        }

        // pef
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

        // triage
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

        // adherence
        plannedControllerSchedule = selectedChildItem.plannedControllerDates;

        if (plannedControllerSchedule == null) {
            adherenceText = "Controller schedule adherence:";
        } else {
            List<Long> controllerLogMsList = getControllerLogMsList();

            adherenceText
                    = String.format(Locale.getDefault(),
                    "Controller schedule adherence:\n" +
                            "    %.0f%%",
                    calculateAdherence(controllerLogMsList, plannedControllerSchedule));
        }

        pbField.setText(pbText);
        dailyCheckInField.setText(checkInText);
        lastControllerField.setText(controllerText);
        lastRescueField.setText(rescueText);
        lastPefField.setText(pefText);
        lastTriageField.setText(triageText);
        adherenceField.setText(adherenceText);

    }

    private List<Long> getControllerLogMsList() {

        List<Long> output = new ArrayList<>();
        List<ControllerLogs> controllerLogs
                = childItemHelper.getGenericLog(selectedChildItem.controllerLogs,
                ChildItemHelper.getAscendingTimeComparator());

        for (ControllerLogs log : controllerLogs) {
            output.add(log.gettimestamp());
        }

        return output;

    }

    private double calculateAdherence(List<Long> controllerLogMs, List<Long> plannedSchedule) {

        int upToPresentScheduledDates = 0;
        long currTime = System.currentTimeMillis();

        for (long ms : plannedSchedule) {
            if (ms <= currTime) {
                upToPresentScheduledDates++;
            }
        }

        if (upToPresentScheduledDates == 0) return 0.0;

        List<Long> uniqueDayAdjustedLogs = new ArrayList<>();
        int controllerMatchScheduledDates = 0;

        for (long logMs : controllerLogMs) {

            logMs = logMs - logMs % AppConstants.MS_DAY;

            if (!uniqueDayAdjustedLogs.contains(logMs) && plannedSchedule.contains(logMs)) {
                uniqueDayAdjustedLogs.add(logMs);
                controllerMatchScheduledDates++;
            }

        }

        return (double) controllerMatchScheduledDates / (double) upToPresentScheduledDates * 100;
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
                .setTitle("Set New PB for " + selectedChildItem.getFirstName())
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

    private void showCheckInPopup() {

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View popupView = inflater.inflate(R.layout.popup_parent_check_in, null);

        nwToggleGroup = popupView.findViewById(R.id.nightWakingGroup);
        alToggleGroup = popupView.findViewById(R.id.activityLimitGroup);
        cwToggleGroup = popupView.findViewById(R.id.coughWheezeGroup);

        nwToggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    if (checkedId == R.id.nightWakingYes){
                        nwText = "Yes";
                    } else if (checkedId == R.id.nightWakingNo) {
                        nwText = "No";
                    }
                }
            }
        });

        alToggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    if (checkedId == R.id.activityLimitYes){
                        alText = "Yes";
                    } else if (checkedId == R.id.activityLimitNo) {
                        alText = "No";
                    }
                }
            }
        });

        cwToggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {

                    if (checkedId == R.id.coughWheezeNone){
                        cwText = "None";
                    } else if (checkedId == R.id.coughWheezeMild) {
                        cwText = "Mild";
                    } else if (checkedId == R.id.coughWheezeModerate){
                        cwText = "Moderate";
                    } else if (checkedId == R.id.coughWheezeSevere) {
                        cwText = "Severe";
                    }
                }
            }
        });

        exerciseTrigger = popupView.findViewById(R.id.triggerExercise);
        coldAirTrigger = popupView.findViewById(R.id.triggerColdAir);
        dustTrigger = popupView.findViewById(R.id.triggerDust);
        petsTrigger = popupView.findViewById(R.id.triggerPets);
        smokeTrigger = popupView.findViewById(R.id.triggerSmoke);
        illnessTrigger = popupView.findViewById(R.id.triggerIllness);
        cleanerTrigger = popupView.findViewById(R.id.triggerCleaners);
        smellTrigger = popupView.findViewById(R.id.triggerOdors);

        new AlertDialog.Builder(requireContext())
                .setTitle("Parent-assisted Check-In")
                .setView(popupView)
                .setPositiveButton("Save", ((dialog, which) -> {

                    List<String> triggers = new ArrayList<>();
                    String childUid = selectedChildItem.getUid();
                    DatabaseReference logRef
                            = dataManager.getUserReference(childUid).child("DailyCheckIn");

                    if (exerciseTrigger.isChecked()) triggers.add("Exercise");
                    if (coldAirTrigger.isChecked()) triggers.add("Cold air");
                    if (dustTrigger.isChecked()) triggers.add("Dust");
                    if (petsTrigger.isChecked()) triggers.add("Pets");
                    if (smokeTrigger.isChecked()) triggers.add("Smoke");
                    if (illnessTrigger.isChecked()) triggers.add("Illness");
                    if (cleanerTrigger.isChecked()) triggers.add("Cleaners");
                    if (smellTrigger.isChecked()) triggers.add("Perfume / Strong odors");

                    Map<String, Object> data = new HashMap<>();
                    data.put("nightWaking", nwText);
                    data.put("activityLimit", alText);
                    data.put("coughWheeze", cwText);
                    data.put("triggers", triggers);
                    data.put("author", "Parent");
                    data.put("timestamp", System.currentTimeMillis());

                    logRef.push().setValue(data)
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                }))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAdherencePicker() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View popupView = inflater.inflate(R.layout.popup_set_adherence_schedule, null);

        CalendarView calendarView = popupView.findViewById(R.id.calendarView);

        new AlertDialog.Builder(requireContext())
                .setTitle("Set planned controller dates")
                .setView(popupView)
                .setPositiveButton("Save", (dialog, which) -> {

                    List<Calendar> selectedDates = calendarView.getSelectedDates();
                    List<Long> selectedDatesMS = new ArrayList<>();

                    for (Calendar date : selectedDates) {
                        long dateMS = date.getTimeInMillis();
                        selectedDatesMS.add(dateMS);
                    }

                    DatabaseReference plannedControllerDatesRef
                            = dataManager.getUserReference(selectedChildItem.uid)
                            .child("plannedControllerDates");

                    dataManager.writeTo(plannedControllerDatesRef, selectedDatesMS);

                    Toast.makeText(
                            requireContext(),
                            "New controller schedule for "
                                    + selectedChildItem.getFirstName()
                                    + " set",
                            Toast.LENGTH_SHORT
                    ).show();

                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}