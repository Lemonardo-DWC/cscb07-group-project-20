package com.example.smartair;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class AddChildFragment extends Fragment {

    AddChildViewModel acvm;
    EditText usernameEntry, passwordEntry, firstNameEntry, middleNameEntry, lastNameEntry;

    SpinnerHelper spinnerHelper = new SpinnerHelper();
    Spinner daySpinner, monthSpinner, yearSpinner, sexSpinner;

    ArrayAdapter<String> dayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_child, container, false);

        // instantiate AddChildViewModel
        AddChildViewModelFactory factory = new AddChildViewModelFactory(requireContext());
        acvm = new ViewModelProvider(this, factory).get(AddChildViewModel.class);

        // editText variables
        usernameEntry = view.findViewById(R.id.usernameEntry);
        passwordEntry = view.findViewById(R.id.passwordEntry);
        firstNameEntry = view.findViewById(R.id.firstNameEntry);
        middleNameEntry = view.findViewById(R.id.middleNameEntry);
        lastNameEntry = view.findViewById(R.id.lastNameEntry);

        // spinner set up
        yearSpinner = view.findViewById(R.id.yearSpinner);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                spinnerHelper.getYearRange()
        );
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        monthSpinner = view.findViewById(R.id.monthSpinner);
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                spinnerHelper.getMonthRange());
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        daySpinner = view.findViewById(R.id.daySpinner);
        dayAdapter = new ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                spinnerHelper.getDayRange((String) yearSpinner.getSelectedItem(),
                                            (String) monthSpinner.getSelectedItem())
        );
        daySpinner.setAdapter(dayAdapter);

        sexSpinner = view.findViewById(R.id.sexSpinner);
        ArrayAdapter<CharSequence> sexAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.sexOptions,
                android.R.layout.simple_spinner_item
        );
        sexSpinner.setAdapter(sexAdapter);

        // spinner behaviour
        AdapterView.OnItemSelectedListener monthYearListener
                = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMonth = (String) monthSpinner.getSelectedItem();
                String selectedYear = (String) yearSpinner.getSelectedItem();
                String selectedDay = (String) daySpinner.getSelectedItem();
                ArrayList<String> newDayRange
                        = spinnerHelper.getDayRange(selectedYear, selectedMonth);

                dayAdapter.clear();
                dayAdapter.addAll(newDayRange);
                dayAdapter.notifyDataSetChanged();

                if (Integer.parseInt(selectedDay)
                        > spinnerHelper.getMaxDayIndex(selectedYear, selectedMonth)){

                    daySpinner.setSelection(
                            spinnerHelper.getMaxDayIndex(selectedYear, selectedMonth)
                    );

                } else {
                    daySpinner.setSelection(Integer.parseInt(selectedDay) - 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        monthSpinner.setOnItemSelectedListener(monthYearListener);
        yearSpinner.setOnItemSelectedListener(monthYearListener);

        // button variables
        Button buttonAddChild = view.findViewById(R.id.exampleButton);
        buttonAddChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEntry.getText().toString().trim();
                String password = passwordEntry.getText().toString();
                String firstName = firstNameEntry.getText().toString().trim();
                String middleName = middleNameEntry.getText().toString().trim();
                String lastName = lastNameEntry.getText().toString().trim();

                acvm.createChildRequest(username, password, firstName, middleName, lastName);

            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        acvm.usernameError.observe(getViewLifecycleOwner(), msg -> {
            usernameEntry.setError(msg);
        });

        acvm.passwordError.observe(getViewLifecycleOwner(), msg -> {
            passwordEntry.setError(msg);
        });

        acvm.firstNameError.observe(getViewLifecycleOwner(), msg -> {
            firstNameEntry.setError(msg);
        });

        acvm.middleNameError.observe(getViewLifecycleOwner(), msg -> {
            middleNameEntry.setError(msg);
        });

        acvm.lastNameError.observe(getViewLifecycleOwner(), msg -> {
            lastNameEntry.setError(msg);
        });

        acvm.formValidity.observe(getViewLifecycleOwner(), valid -> {
            if (valid) {

                String username = usernameEntry.getText().toString().trim();
                String password = passwordEntry.getText().toString();
                String firstName = firstNameEntry.getText().toString().trim();
                String middleName = middleNameEntry.getText().toString().trim();
                String lastName = lastNameEntry.getText().toString().trim();
                String birthday = (
                        daySpinner.getSelectedItem()
                        + "/"
                        + monthSpinner.getSelectedItem()
                        + "/"
                        + yearSpinner.getSelectedItem()
                );
                String sex = sexSpinner.getSelectedItem().toString();

                acvm.createChild(username, password,
                        firstName, middleName, lastName,
                        birthday, sex);

            }
        });

        acvm.createChildResult.observe(getViewLifecycleOwner(), result -> {
            if (result.equals(AppConstants.SUCCESS)) {
                Toast.makeText(
                        requireContext(),
                        "Child account for " + usernameEntry.getText()
                                + " created",
                        Toast.LENGTH_SHORT
                ).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                Toast.makeText(
                        requireContext(),
                        "Could not create child account",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

    }
}