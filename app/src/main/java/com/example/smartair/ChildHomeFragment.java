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


    public ChildHomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            childId = getArguments().getString("childId");
        }
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

        Button controllerLogs = view.findViewById(R.id.buttonControllerLogs);
//        Button SymptomCheckFragment = view.findViewById(R.id.button4);
//        Button ChildPEFFragment = view.findViewById(R.id.button5);
//        Button rescueLogs = view.findViewById(R.id.button2);



        childId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        controllerLogs.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new ControllerLogsFragment());
        });

//        SymptomCheckFragment.setOnClickListener(v -> {
//            SymptomCheckFragment next = new SymptomCheckFragment();
//            ((MainActivity) requireActivity()).loadFragment(next);
//        });
//
//        ChildPEFFragment.setOnClickListener(v -> {
//            ChildPEFFragment next = new ChildPEFFragment();
//            ((MainActivity) requireActivity()).loadFragment(next);
//        });


    }

}
