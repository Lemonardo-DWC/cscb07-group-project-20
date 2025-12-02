package com.example.smartair;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.text.SimpleDateFormat;


public class RewardsFragment extends Fragment {

    private TextView tvControllerStreak, tvTechniqueStreak;
    private LinearLayout badgesContainer;
    private DatabaseReference childRef;
    private String childId;

    // Badge thresholds
    private final int LOW_RESCUE_THRESHOLD = 4;
    private final int PERFECT_WEEK_DAYS = 7;
    private final int TECHNIQUE_SESSIONS = 10;

    public RewardsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rewards, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvControllerStreak = view.findViewById(R.id.tvControllerStreak);
        tvTechniqueStreak = view.findViewById(R.id.tvTechniqueStreak);
        badgesContainer = view.findViewById(R.id.badgesContainer);

        childId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        childRef = FirebaseDatabase.getInstance().getReference("users").child(childId).child("streaks");

        loadStreaksAndBadges();
    }

    private void loadStreaksAndBadges() {
        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int controllerStreak = 0;
                int techniqueStreak = 0;

                if (snapshot.child("controllerStreak").exists()) {
                    controllerStreak = snapshot.child("controllerStreak").getValue(Integer.class);
                }

                if (snapshot.child("techniqueStreak").exists()) {
                    techniqueStreak = snapshot.child("techniqueStreak").getValue(Integer.class);
                }

                tvControllerStreak.setText(controllerStreak + " days");
                tvTechniqueStreak.setText(techniqueStreak + " days");

                List<BadgeItem> badges = new ArrayList<>();

                // --- Perfect week badge ---
                boolean perfectWeek = controllerStreak >= PERFECT_WEEK_DAYS;
                badges.add(new BadgeItem("First Perfect Week", perfectWeek));


                // --- Technique badge  ---
                DatabaseReference techniqueRef = FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(childId)
                        .child("techniqueSessions");

                techniqueRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot techniqueSnap) {

                        int totalSessions = (int) techniqueSnap.getChildrenCount();
                        boolean techniqueBadge = totalSessions >= TECHNIQUE_SESSIONS;

                        badges.add(new BadgeItem("10 High-Quality Technique Sessions", techniqueBadge));

                        // --- Low Rescue Badge  ---
                        DatabaseReference rescueRef = FirebaseDatabase.getInstance()
                                .getReference("users")
                                .child(childId)
                                .child("rescueLogs");

                        rescueRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot rescueSnapshot) {
                                //Low Rescue in last 30 days
                                long now = System.currentTimeMillis();
                                long thirtyDaysAgo = now - (30L * 24 * 60 * 60 * 1000);

                                HashSet<String> rescueDays = new HashSet<>();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                                for (DataSnapshot s : rescueSnapshot.getChildren()) {
                                    Long ts = s.child("timestamp").getValue(Long.class);
                                    if (ts != null && ts >= thirtyDaysAgo) {
                                        String day = sdf.format(new java.util.Date(ts));
                                        rescueDays.add(day);
                                    }
                                }
                                int rescueDayCount = rescueDays.size();
                                boolean lowRescueBadge = rescueDayCount <= LOW_RESCUE_THRESHOLD;

                                badges.add(new BadgeItem("Low Rescue Month", lowRescueBadge));
                                // --- Update UI ---
                                //=========================
                                badgesContainer.removeAllViews();
                                LayoutInflater inflater = LayoutInflater.from(getContext());

                                for (BadgeItem badge : badges) {
                                    View item = inflater.inflate(R.layout.item_badge, badgesContainer, false);
                                    ImageView ivBadge = item.findViewById(R.id.ivBadge);
                                    TextView tvTitle = item.findViewById(R.id.tvBadgeTitle);

                                    tvTitle.setText(badge.title);

                                    if (badge.unlocked) {
                                        switch (badge.title) {
                                            case "First Perfect Week":
                                                ivBadge.setImageResource(R.drawable.ic_badge_perfect_week);
                                                break;
                                            case "10 High-Quality Technique Sessions":
                                                ivBadge.setImageResource(R.drawable.ic_badge_technique_10);
                                                break;
                                            case "Low Rescue Month":
                                                ivBadge.setImageResource(R.drawable.ic_badge_low_rescue);
                                                break;
                                        }
                                        item.setAlpha(1f);
                                    } else {
                                        ivBadge.setImageResource(R.drawable.ic_badge_locked);
                                        item.setAlpha(0.45f);
                                    }

                                    badgesContainer.addView(item);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load streaks.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
