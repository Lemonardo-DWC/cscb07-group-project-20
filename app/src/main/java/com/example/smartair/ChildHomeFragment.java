package com.example.smartair;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;


public class ChildHomeFragment extends Fragment {


    private String childId;
    private final UserManager userManager = new UserManager();

    public ChildHomeFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_child_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button childLogout = view.findViewById(R.id.button8);
        Button controllerLogs = view.findViewById(R.id.buttonControllerLogs);
        Button rescueLogs = view.findViewById(R.id.button3);
        Button dailyCheck = view.findViewById(R.id.button1);
        Button SymptomCheckFragment = view.findViewById(R.id.button4);
        Button ChildPEFFragment = view.findViewById(R.id.button5);
        Button TechniqueHelper = view.findViewById(R.id.button7);
//



        childId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        controllerLogs.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new ControllerLogsFragment());
        });
        rescueLogs.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new RescueLogsFragment());
        });
        dailyCheck.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new DailyCheckinHistoryFragment());
        });
        TechniqueHelper.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new TechniqueHelperFragment());
        });


        SymptomCheckFragment.setOnClickListener(v -> {
            SymptomCheckFragment next = new SymptomCheckFragment();
            ((MainActivity) requireActivity()).loadFragment(next);
        });

        ChildPEFFragment.setOnClickListener(v -> {
            ChildPEFFragment next = new ChildPEFFragment();
            ((MainActivity) requireActivity()).loadFragment(next);
        });

        childLogout.setOnClickListener(v -> {
                    userManager.logout();
                    ((MainActivity) requireActivity()).loadFragment(new LoginFragment());
            });
    }



}
