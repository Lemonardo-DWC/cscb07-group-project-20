package com.example.smartair;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ParentHomeFragment
        extends Fragment
        implements ParentHomeChildItemAdapter.OnDetailButtonClick {

    // trend chart
    private com.github.mikephil.charting.charts.LineChart pefTrendChart;

    // buttons
    private MaterialButton menuButton;
    private MaterialButtonToggleGroup trendRangeToggleGroup;

    // recycler views
    private RecyclerView childRecycler;
    private RecyclerView alertRecycler;

    // child item list
    private List<ChildItem> childItemList;
    private ParentHomeChildItemAdapter parentHomeChildItemAdapter;
    private ParentAlertAdapter parentAlertAdapter;

    // helper class objects
    private ParentHomeViewModel phvm;
    private final ChildItemHelper childItemHelper = new ChildItemHelper();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parent_home, container, false);

        phvm = new ViewModelProvider(requireActivity()).get(ParentHomeViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // trend chart
        pefTrendChart = view.findViewById(R.id.pefTrendChart);

        pefTrendChart.setDragEnabled(true); // allows panning
        pefTrendChart.setScaleEnabled(true); // allows scaling
        pefTrendChart.setPinchZoom(true); // pinch zoom
        pefTrendChart.setDoubleTapToZoomEnabled(true); // double tap zoom
        pefTrendChart.setAutoScaleMinMaxEnabled(true); // auto scaling for y axis

        Legend legend = pefTrendChart.getLegend(); // legend
        legend.setWordWrapEnabled(true);
        legend.setMaxSizePercent(1f);

        // button initialization
        menuButton = view.findViewById(R.id.menu_button);
        trendRangeToggleGroup = view.findViewById(R.id.trendRangeToggleGroup);

        //check if it is users' fist login
        String parentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference logRef = db.getReference("users").child(parentId);
        DatabaseReference firstLoginRef = logRef.child("firstLogin");

        firstLoginRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean firstLogin = snapshot.getValue(Boolean.class);

                // If ture or null → jump to Onboarding page
                if (firstLogin == null || firstLogin) {
                    // jump to Onboarding Fragment
                    ((MainActivity) requireActivity()).loadFragment(new OnboardingParentFragment());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


        // button behaviour
        menuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), menuButton);
            popupMenu.getMenuInflater().inflate(R.menu.parent_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();

                if(itemId == R.id.manageChildren) {
                    ((MainActivity) requireActivity()).loadFragment(new ManageChildFragment());
                    return true;
                } else if (itemId == R.id.shareReport) {
                    ((MainActivity) requireActivity()).loadFragment(new PDFChildSelectFragment());
                    return true;
                } else if (itemId == R.id.viewInventory) {
                    ((MainActivity) requireActivity()).loadFragment(new ParentInventoryFragment());
                    return true;
                } else if (itemId == R.id.useChildProfile) {
                    Toast.makeText(
                            requireContext(),
                            "use child profile",
                            Toast.LENGTH_SHORT
                    ).show();
                    return true;
                } else if (itemId == R.id.shareWithProvider) {
                    ((MainActivity) requireActivity()).loadFragment(new ShareWithProviderFragment());
                    return true;
                } else if (itemId == R.id.logout) {
                    phvm.logout();
                    ((MainActivity) requireActivity()).loadFragment(new LoginFragment());
                }

                return false;

            });

            popupMenu.show();

        });

        // trend chart toggle
        trendRangeToggleGroup.addOnButtonCheckedListener(
                new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup materialButtonToggleGroup,
                                        int checkedId, boolean isChecked) {
                if(isChecked){
                    if (checkedId == R.id.buttonSevenDay) {
                        showPefTrendChart(childItemList, 7);
                    } else if (checkedId == R.id.buttonThirtyDay) {
                        showPefTrendChart(childItemList, 30);
                    }
                    pefTrendChart.invalidate(); // update chart
                }
            }
        });

        // child status recycler setup
        childRecycler = view.findViewById(R.id.childRecycler);

        childItemList = new ArrayList<>();
        parentHomeChildItemAdapter = new ParentHomeChildItemAdapter(childItemList, this, requireContext());
        childRecycler.setAdapter(parentHomeChildItemAdapter);
        childRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        // alert recycler
        alertRecycler = view.findViewById(R.id.alertRecycler);
        parentAlertAdapter = new ParentAlertAdapter(childItemList);
        alertRecycler.setAdapter(parentAlertAdapter);
        alertRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        // children list listener
        phvm.trackChildListRef();

        phvm.childItemListData.observe(getViewLifecycleOwner(), newChildItemList -> {
            childItemList.clear();
            childItemList.addAll(newChildItemList);
            parentHomeChildItemAdapter.notifyDataSetChanged();
            parentAlertAdapter.updateChildList(newChildItemList);

            if (trendRangeToggleGroup.getCheckedButtonId() == R.id.buttonSevenDay) {
                showPefTrendChart(childItemList, 7);
            } else {
                showPefTrendChart(childItemList, 30);
            }
            pefTrendChart.invalidate(); // update chart
        });

        /// back button handling ///
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {

            /// home screen is navigation root after logging in, thus back button should send user
            /// out of app
            @Override
            public void handleOnBackPressed() {
                requireActivity().finish();
            }
        };
        requireActivity()
                .getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    @Override
    public void onClick(ChildItem childItem) {
        phvm.selectItem(childItem);

        ((MainActivity) requireActivity()).loadFragment(new ChildDetailsFragment());
    }

    private void showPefTrendChart(List<ChildItem> children, int days) {

        long now = System.currentTimeMillis();
        long pastMillis = now - days * AppConstants.MS_DAY;

        // x axis
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd", Locale.getDefault());

        pefTrendChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                long millis = (long) value;
                return sdf.format(new Date(millis));
            }
        });

        com.github.mikephil.charting.components.XAxis xAxis = pefTrendChart.getXAxis();
        xAxis.setPosition(com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(24 * 60 * 60 * 1000f); // one day
        xAxis.setAxisMinimum(pastMillis);
        xAxis.setAxisMaximum(now);
        xAxis.setLabelRotationAngle(-30); // rotate labels

        // y axis
        com.github.mikephil.charting.components.YAxis leftAxis = pefTrendChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        pefTrendChart.getAxisRight().setEnabled(false);

        // chart description
        pefTrendChart.getDescription().setText("PEF trend (last " + days + " days)");

        // chart data set up
        List<com.github.mikephil.charting.data.LineDataSet> dataSets = new ArrayList<>();

        for (ChildItem child : children) {

            if (child.pefLogs == null || child.pefLogs.isEmpty()) {
                // no PEF logs, skip
                continue;
            }

            List<com.github.mikephil.charting.data.Entry> entries = new ArrayList<>();

            // filter logs for selected range
            List<PefLogs> filteredLogs = childItemHelper.getRangeGenericLog(
                    child.pefLogs,
                    pastMillis, now,
                    ChildItemHelper.getAscendingTimeComparator()
            );

            if (filteredLogs.isEmpty()) {
                continue; // skip if no logs
            }

            for (PefLogs log : filteredLogs) {
                entries.add(new com.github.mikephil.charting.data.Entry(log.gettimestamp(), log.pef));
            }

            com.github.mikephil.charting.data.LineDataSet dataSet =
                    new com.github.mikephil.charting.data.LineDataSet(entries, child.getFirstName());

            dataSet.setLineWidth(2f);
            dataSet.setCircleRadius(3f);
            dataSet.setDrawValues(false); // hide numeric values on points

            int[] colors = ColorTemplate.COLORFUL_COLORS;
            int hash = Math.abs(child.getUid().hashCode());
            int color = colors[hash % colors.length];
            dataSet.setColor(color);
            dataSet.setCircleColor(color);

            dataSets.add(dataSet);
        }

        // if no datasets, clear chart
        if (dataSets.isEmpty()) {
            pefTrendChart.clear();
            pefTrendChart.invalidate();
            return;
        }

        com.github.mikephil.charting.data.LineData lineData =
                new com.github.mikephil.charting.data.LineData(
                        dataSets.toArray(new com.github.mikephil.charting.data.LineDataSet[0])
                );

        // update chart
        lineData.notifyDataChanged();
        pefTrendChart.setData(lineData);

        List<LegendEntry> legendEntries = new ArrayList<>();
        for (com.github.mikephil.charting.data.LineDataSet set : dataSets) {
            LegendEntry entry = new LegendEntry();
            entry.label = set.getLabel();
            entry.formColor = set.getColor();
            entry.form = Legend.LegendForm.LINE;
            legendEntries.add(entry);
        }
        pefTrendChart.getLegend().setCustom(legendEntries);

        pefTrendChart.notifyDataSetChanged();
        pefTrendChart.invalidate();
    }

}