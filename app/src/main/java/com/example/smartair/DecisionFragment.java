package com.example.smartair;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class DecisionFragment extends Fragment {

    private boolean redFlag;
    private TextView resultText;
    private View backgroundContainer;
    private Button btnBackHome;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_decision, container, false);

        resultText = view.findViewById(R.id.resultText);
        backgroundContainer = view.findViewById(R.id.bgContainer);
        btnBackHome = view.findViewById(R.id.btn_back_home);

        if (getArguments() != null) {
            redFlag = getArguments().getBoolean("RED_FLAG", false);
        }

        updateUI();

        btnBackHome.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new ChildHomeFragment());
        });

        return view;
    }

    private void updateUI() {
        if (redFlag) {
            //RED
            backgroundContainer.setBackgroundColor(Color.parseColor("#F44336"));
            resultText.setText(
                    "Red Flag Detected\n\n" +
                            "You may be having serious asthma symptoms.\n" +
                            "• Use your reliever inhaler.\n" +
                            "• Tell an adult.\n" +
                            "• Seek medical help if symptoms worsen."
            );
        } else {
            //Safe
            backgroundContainer.setBackgroundColor(Color.parseColor("#4CAF50"));
            resultText.setText(
                    "No Red Flags\n\n" +
                            "Your symptoms appear mild.\n" +
                            "• Follow your asthma action plan.\n" +
                            "• You can continue to monitor symptoms."
            );
        }
    }
}
