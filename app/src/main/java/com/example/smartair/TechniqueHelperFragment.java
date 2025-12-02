package com.example.smartair;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


public class TechniqueHelperFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_technique_helper, container, false);

        YouTubePlayerView youTubePlayerView = view.findViewById(R.id.youtube_player_view);


        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = "LU-pRbN7AD4";
                youTubePlayer.loadVideo(videoId, 0);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CheckBox cb1 = view.findViewById(R.id.checkBox);
        CheckBox cb2 = view.findViewById(R.id.checkBox2);
        CheckBox cb3 = view.findViewById(R.id.checkBox3);
        CheckBox cb4 = view.findViewById(R.id.checkBox4);
        CheckBox cb5 = view.findViewById(R.id.checkBox5);
        Button completeBtn = view.findViewById(R.id.button9);

        completeBtn.setOnClickListener(v -> {
            if (!cb1.isChecked() || !cb2.isChecked() || !cb3.isChecked()
                    || !cb4.isChecked() || !cb5.isChecked()) {
                Toast.makeText(getContext(), "Please complete all steps before submitting", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users")
                    .child(userId).child("techniqueSessions");

            Map<String, Object> data = new HashMap<>();
            data.put("timestamp", System.currentTimeMillis());

            ref.push().setValue(data)
                    .addOnSuccessListener(unused -> {
                        updateTechniqueStreak();
                        Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        });
    }

    private void updateTechniqueStreak() {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference sessionsRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("techniqueSessions");

        DatabaseReference streakRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("streaks")
                .child("techniqueStreak");

        sessionsRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {

                long todayStart = getStartOfDay(System.currentTimeMillis());
                long yesterdayStart = todayStart - 24L * 60L * 60L * 1000L;

                final AtomicBoolean didToday = new AtomicBoolean(false);
                final AtomicBoolean didYesterday = new AtomicBoolean(false);

                for (com.google.firebase.database.DataSnapshot s : snapshot.getChildren()) {
                    Long ts = s.child("timestamp").getValue(Long.class);
                    if (ts == null) continue;

                    if (ts >= todayStart) {
                        didToday.set(true);
                    } else if (ts >= yesterdayStart && ts < todayStart) {
                        didYesterday.set(true);
                    }
                }

                streakRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot2) {

                        Integer currentStreakObj = snapshot2.getValue(Integer.class);
                        int currentStreak = (currentStreakObj != null ? currentStreakObj : 0);

                        int newStreak;

                        if (didToday.get()) {
                            if (didYesterday.get()) {
                                newStreak = currentStreak + 1;
                            } else {
                                newStreak = 1;
                            }
                        } else {
                            newStreak = 0;
                        }

                        streakRef.setValue(newStreak);
                    }

                    @Override
                    public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {}
                });
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {}
        });
    }

    private long getStartOfDay(long ts) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTimeInMillis(ts);
        c.set(java.util.Calendar.HOUR_OF_DAY, 0);
        c.set(java.util.Calendar.MINUTE, 0);
        c.set(java.util.Calendar.SECOND, 0);
        c.set(java.util.Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

}

