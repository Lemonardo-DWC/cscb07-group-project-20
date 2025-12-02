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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;



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

        DatabaseReference streakBaseRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("streaks");

        sessionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                long now = System.currentTimeMillis();
                long todayStart = getStartOfDay(now);

                AtomicBoolean didToday = new AtomicBoolean(false);

                for (DataSnapshot s : snapshot.getChildren()) {
                    Long ts = s.child("timestamp").getValue(Long.class);
                    if (ts != null && ts >= todayStart) {
                        didToday.set(true);
                        break;
                    }
                }


                streakBaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot streakSnap) {

                        Integer streak = streakSnap.child("techniqueStreak").getValue(Integer.class);
                        Integer lastUpdated = streakSnap.child("techniqueLastUpdated").getValue(Integer.class);

                        if (streak == null) streak = 0;

                        int todayInt = getDayInt(now);

                        if (lastUpdated != null && lastUpdated != todayInt) {
                            streak = 0;
                        }


                        if (didToday.get()) {
                            if (lastUpdated == null || lastUpdated != todayInt) {
                                streak += 1;
                                streakBaseRef.child("techniqueLastUpdated").setValue(todayInt);
                            }
                        }

                        streakBaseRef.child("techniqueStreak").setValue(streak);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private int getDayInt(long ts) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTimeInMillis(ts);
        int y = c.get(java.util.Calendar.YEAR);
        int m = c.get(java.util.Calendar.MONTH) + 1;
        int d = c.get(java.util.Calendar.DAY_OF_MONTH);
        return y * 10000 + m * 100 + d;
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

