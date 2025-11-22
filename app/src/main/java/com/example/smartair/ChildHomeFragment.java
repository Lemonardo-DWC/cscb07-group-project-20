package com.example.smartair;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChildHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChildHomeFragment extends Fragment {


    private String childId;


    public ChildHomeFragment() {
        // Required empty public constructor
    }

    public static ChildHomeFragment newInstance(String childId) {
        ChildHomeFragment fragment = new ChildHomeFragment();
        Bundle args = new Bundle();
        args.putString("childId", childId);
        fragment.setArguments(args);
        return fragment;
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



        String finalChildId = (childId != null) ? childId : "testChild002";   // test case


        controllerLogs.setOnClickListener(v -> {
            ControllerLogsFragment next = new ControllerLogsFragment();
            Bundle args = new Bundle();
            args.putString("childId", finalChildId);
            next.setArguments(args);
            ((MainActivity) requireActivity()).loadFragment(next);
        });

//        SymptomCheckFragment.setOnClickListener(v -> {
//            SymptomCheckFragment next = new SymptomCheckFragment();
//            Bundle args = new Bundle();
//            args.putString("childId", finalChildId);
//            next.setArguments(args);
//            ((MainActivity) requireActivity()).loadFragment(next);
//        });
//
//        ChildPEFFragment.setOnClickListener(v -> {
//            ChildPEFFragment next = new ChildPEFFragment();
//            Bundle args = new Bundle();
//            args.putString("childId", finalChildId);
//            next.setArguments(args);
//            ((MainActivity) requireActivity()).loadFragment(next);
//        });


    }

}
