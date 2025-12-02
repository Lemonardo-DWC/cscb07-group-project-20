package com.example.smartair;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ChildHistoryFragment extends Fragment {

    MaterialButton backButton;

    TextView screenId;

    Spinner logTypeSpinner, rangeSpinner;
    MaterialButton symptomFilterButton, triggerFilterButton;

    RecyclerView logRecycler;

    private ParentHomeViewModel phvm;
    private final ChildItemHelper childItemHelper = new ChildItemHelper();
    private final TimeHelper timeHelper = new TimeHelper();

    private List<String> symptomFilterList = new ArrayList<>();
    private List<String> triggerFilterList = new ArrayList<>();

    private ChildItem selectedChildItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_child_history, container, false);

        phvm = new ViewModelProvider(requireActivity()).get(ParentHomeViewModel.class);
        selectedChildItem = phvm.selectedItem.getValue();

        // Button initialization
        backButton = view.findViewById(R.id.history_back_button);

        // TextView initialization
        screenId = view.findViewById(R.id.screenID);

        // Spinner initialization
        logTypeSpinner = view.findViewById(R.id.logTypeSpinner);
        rangeSpinner = view.findViewById(R.id.rangeSpinner);

        // MaterialButton initialization
        symptomFilterButton = view.findViewById(R.id.symptomFilterButton);
        triggerFilterButton = view.findViewById(R.id.triggerFilterButton);

        // Recycler initialization
        logRecycler = view.findViewById(R.id.historyListRecycler);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        // screen title
        String title = selectedChildItem.getFirstName() + "'s History";
        screenId.setText(title);

        // log type spinner
        ArrayAdapter<CharSequence> logTypeAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.logTypeArray,
                android.R.layout.simple_spinner_item
        );
        logTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        logTypeSpinner.setAdapter(logTypeAdapter);

        logTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateLogHistory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // range spinner
        ArrayAdapter<CharSequence> rangeAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.historyRangeArray,
                android.R.layout.simple_spinner_item
        );
        rangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rangeSpinner.setAdapter(rangeAdapter);

        // log recycler
        logRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        // livedata observer
        phvm.selectedItem.observe(getViewLifecycleOwner(), childItem -> {
            selectedChildItem = childItem;
            updateLogHistory();
        });

        // symptom filter button
        symptomFilterButton.setText("Filters: 0");
        symptomFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] filter = getResources().getStringArray(R.array.symptomFilters);
                showMultiSelectDialog("Filter by symptoms", filter, symptomFilterList);
                updateLogHistory();
            }
        });

        // trigger filter button
        triggerFilterButton.setText("Filters: 0");
        triggerFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] filter = getResources().getStringArray(R.array.triggerFilters);
                showMultiSelectDialog("Filter by triggers", filter, triggerFilterList);
                updateLogHistory();
            }
        });

    }

    private <T extends SystemTimeTimestamp> void setLogAdapter(
            List<T> logs, HistoryAdapter.LogFormatter formatter) {

        HistoryAdapter<T> adapter = new HistoryAdapter<>(logs, formatter);
        logRecycler.setAdapter(adapter);
        logRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void updateLogHistory() {
        if (selectedChildItem == null) return;

        String range = rangeSpinner.getSelectedItem().toString();
        int days = 0;

        switch (range) {
            case "Last 30 days":
                days = 30;
                break;
            case "Last 60 days":
                days = 60;
                break;
            case "Last 90 days":
                days = 90;
                break;
            case "Last 120 days":
                days = 120;
                break;
            case "Last 150 days":
                days = 150;
                break;
            case "Last 180 days":
                days = 180;
                break;
        }

        long todayMillis = System.currentTimeMillis();
        long pastMillis = todayMillis - days * AppConstants.MS_DAY;

        String type = logTypeSpinner.getSelectedItem().toString();

        switch (type) {
            case "Daily Check-In":
                List<DailyCheckIn> dailyCheckIns = childItemHelper.getRangeGenericLog(
                        selectedChildItem.DailyCheckIn,
                        pastMillis, todayMillis,
                        ChildItemHelper.getDescendingTimeComparator()
                );

                filterDailyCheckIn(dailyCheckIns);

                setLogAdapter(
                        dailyCheckIns,
                        log -> {
                            String entry =
                                    timeHelper.formatTime(
                                            AppConstants.DATE_HMMDY,
                                            ((DailyCheckIn) log).gettimestamp()
                                    ) + "\n"
                                    + "Author: " + ((DailyCheckIn) log).author + "\n\n"
                                    + "Activity limit: " + ((DailyCheckIn) log).activityLimit + "\n"
                                    + "Cough/wheeze: " + ((DailyCheckIn) log).coughWheeze + "\n"
                                    + "Night/waking: " + ((DailyCheckIn) log).nightWaking + "\n"
                                    + "Triggers: "
                                    + String.join(", ", ((DailyCheckIn) log).triggers);

                            return entry;
                        });
                break;
            case "Controller":
                List<ControllerLogs> controllerLogs = childItemHelper.getRangeGenericLog(
                        selectedChildItem.controllerLogs,
                        pastMillis, todayMillis,
                        ChildItemHelper.getDescendingTimeComparator()
                );

                setLogAdapter(
                        controllerLogs,
                        log -> {
                            String entry =
                                    timeHelper.formatTime(
                                            AppConstants.DATE_HMMDY,
                                            ((ControllerLogs) log).gettimestamp()
                                    ) + "\n\n"
                                    + "Doses: " + ((ControllerLogs) log).dose;

                            return entry;
                        }
                );
                break;
            case "Rescue":
                List<RescueLogs> rescueLogs = childItemHelper.getRangeGenericLog(
                        selectedChildItem.rescueLogs,
                        pastMillis, todayMillis,
                        ChildItemHelper.getDescendingTimeComparator()
                );

                setLogAdapter(
                        rescueLogs,
                        log -> {
                            String entry =
                                    timeHelper.formatTime(
                                            AppConstants.DATE_HMMDY,
                                            ((RescueLogs) log).gettimestamp()
                                    ) + "\n\n"
                                    + "Doses: " + ((RescueLogs) log).dose + "\n\n"
                                    + "Breath rating scale:\n"
                                    + "0 = easy to breathe, 3 = hard to breathe\n\n"
                                    + "Pre-breath rating: " + ((RescueLogs) log).preBreathRating + "\n"
                                    + "Post-breath rating: " + ((RescueLogs) log).postBreathRating + "\n"
                                    + "Post status: " + ((RescueLogs) log).parsePostStatus();

                            return entry;
                        }
                );
                break;
            case "PEF":
                List<PefLogs> pefLogs = childItemHelper.getRangeGenericLog(
                        selectedChildItem.pefLogs,
                        pastMillis, todayMillis,
                        ChildItemHelper.getDescendingTimeComparator()
                );

                setLogAdapter(
                        pefLogs,
                        log -> {
                            String entry =
                                    timeHelper.formatTime(
                                            AppConstants.DATE_HMMDY,
                                            ((PefLogs) log).gettimestamp()
                                    ) + "\n\n"
                                    + "PEF: " + ((PefLogs) log).pef;

                            return entry;
                        }
                );
                break;
            case "Triage":
                List<TriageSessions> triageSessions = childItemHelper.getRangeGenericLog(
                        selectedChildItem.triageSessions,
                        pastMillis, todayMillis,
                        ChildItemHelper.getDescendingTimeComparator()
                );

                setLogAdapter(
                        triageSessions,
                        log -> {
                            String entry =
                                    timeHelper.formatTime(
                                            AppConstants.DATE_HMMDY,
                                            ((TriageSessions) log).gettimestamp()
                                    ) + "\n\n"
                                    + "Discolored lips/nails: " + ((TriageSessions) log).blue_lips_nails + "\n"
                                    + "Chest pulling in: " + ((TriageSessions) log).chest_pulling_in + "\n"
                                    + "Dizzy/scared: " + ((TriageSessions) log).dizzy_scared + "\n"
                                    + "Red flag detected: " + ((TriageSessions) log).red_flag_detected + "\n"
                                    + "Can speak full sentences: " + ((TriageSessions) log).speak_full_sentences + "\n"
                                    + "Used rescue meds: " + ((TriageSessions) log).used_rescue_meds;

                            return entry;
                        }
                );
        }
    }

    private void filterDailyCheckIn(List<DailyCheckIn> dailyCheckIns) {
        if (symptomFilterList.isEmpty() && triggerFilterList.isEmpty()) return;

        List<DailyCheckIn> filteredLogs = new ArrayList<>();

        for (DailyCheckIn log : dailyCheckIns) {
            boolean include = true;

            // Check symptoms
            if (symptomFilterList.contains("Night waking") && log.nightWaking.equals("No")) {
                include = false;
            }
            if (symptomFilterList.contains("Cough/wheeze") && log.coughWheeze.equals("None")) {
                include = false;
            }

            // Check triggers
            if (!triggerFilterList.isEmpty()) {
                for (String filter : triggerFilterList) {
                    if (!log.triggers.contains(filter)) {
                        include = false; // exclude if any filter is missing
                        break;
                    }
                }
            }

            if (include) {
                filteredLogs.add(log);
            }
        }

        dailyCheckIns.clear();
        dailyCheckIns.addAll(filteredLogs);
    }


    private void showMultiSelectDialog(String title, String[] options, List<String> selectedItems) {
        boolean[] checkedItems = new boolean[options.length];
        for (int i = 0; i < options.length; i++) {
            checkedItems[i] = selectedItems.contains(options[i]);
        }

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMultiChoiceItems(options, checkedItems, (dialog, which, isChecked) -> {
                    if (isChecked) {
                        if (!selectedItems.contains(options[which])) {
                            selectedItems.add(options[which]);
                        }
                    } else {
                        selectedItems.remove(options[which]);
                    }
                })
                .setPositiveButton("Apply filters", (dialog, which) -> {
                    String symptomFilterCountText = "Filters: " + symptomFilterList.size();
                    String triggerFilterCountText = "Filters: " + triggerFilterList.size();

                    symptomFilterButton.setText(symptomFilterCountText);
                    triggerFilterButton.setText(triggerFilterCountText);

                    updateLogHistory();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


}