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
import android.os.CountDownTimer;
import androidx.appcompat.app.AlertDialog;

public class DecisionFragment extends Fragment {

    private boolean redFlag;
    private TextView resultText;
    private View backgroundContainer;
    private Button btnBackHome;
    private CountDownTimer triageTimer;
    private boolean isInitialRed;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_decision, container, false);
        TextView tvTimer = view.findViewById(R.id.tv_timer);

        resultText = view.findViewById(R.id.resultText);
        backgroundContainer = view.findViewById(R.id.bgContainer);
        btnBackHome = view.findViewById(R.id.btn_back_home);


        if (getArguments() != null) {
            redFlag = getArguments().getBoolean("RED_FLAG", false);
        }
        isInitialRed = redFlag;

        updateUI();

        btnBackHome.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new ChildHomeFragment());
        });

        if (!isInitialRed) {
            startRecheckTimer(tvTimer);
        }

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
                            "• You can continue to monitor symptoms.\n\n\n\n\n\n" +
                            "Reminder: Exiting this page will stop the 10-minute re-check timer!"
            );
        }
    }
    private void startRecheckTimer(TextView tvTimer) {
    //10 m
        triageTimer = new CountDownTimer(10 * 60 * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 1000 / 60;
                long seconds = (millisUntilFinished / 1000) % 60;

                String timeFormatted = String.format("%02d:%02d", minutes, seconds);
                tvTimer.setText(timeFormatted);
            }

            @Override
            public void onFinish() {
                tvTimer.setText("00:00");
                showRecheckDialog();
            }

        }.start();
    }

    private void showRecheckDialog() {
        if (getActivity() == null) return;

        new AlertDialog.Builder(getActivity())
                .setTitle("Time for a Re-check")
                .setMessage("Are you still having trouble breathing?\nPlease check again.")
                .setPositiveButton("Re-check now", (dialog, which) -> {
                    goToRecheck();
                })
                .setNegativeButton("I'm OK", (dialog, which) -> {
                })
                .show();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (triageTimer != null) {
            triageTimer.cancel();
        }
    }
    private void goToRecheck() {
        Fragment fragment = new SymptomCheckFragment();
        Bundle b = new Bundle();
        //this is recheck!
        b.putBoolean("RECHECK_MODE", true);
        fragment.setArguments(b);

        ((MainActivity) requireActivity()).loadFragment(fragment);
    }

    //===============================================
}
