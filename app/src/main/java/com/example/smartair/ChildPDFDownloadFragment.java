
package com.example.smartair;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.*;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChildPDFDownloadFragment extends Fragment {
    private Button threeMonthBtn, sixMonthBtn;
    private String childId;
    private File latestPdfFile;
    private List<Long> plannedControllerDates = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            childId = getArguments().getString("childId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_child_pdf_download, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        threeMonthBtn = view.findViewById(R.id.threeMonthReportButton);
        sixMonthBtn = view.findViewById(R.id.sixMonthReportButton);

        threeMonthBtn.setOnClickListener(v -> generateReport(3));
        sixMonthBtn.setOnClickListener(v -> generateReport(6));
    }

    private void generateReport(int months) {
        long now = System.currentTimeMillis();
        long start = now - (long) months * 30L * 24 * 60 * 60 * 1000L;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(childId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int rescueCount = countSince(snapshot.child("rescueLogs"), start);
                int controllerCount = countSince(snapshot.child("controllerLogs"), start);

                plannedControllerDates.clear();
                for (DataSnapshot d : snapshot.child("plannedControllerDates").getChildren()) {
                    Long ts = d.getValue(Long.class);
                    if (ts != null && ts >= start) {
                        plannedControllerDates.add(ts);
                    }
                }

                int planDays = plannedControllerDates.size();

                int symptomCount = 0;
                List<Integer> symptomTrend = new ArrayList<>();
                for (DataSnapshot s : snapshot.child("DailyCheckIn").getChildren()) {
                    long ts = parseTimestamp(s.child("timestamp").getValue());
                    if (ts >= 0 && ts >= start) {
                        boolean c = parseBoolean(s.child("coughWheeze").getValue());
                        boolean n = parseBoolean(s.child("nightWaking").getValue());
                        boolean a = parseBoolean(s.child("activityLimit").getValue());
                        int daily = (c ? 1 : 0) + (n ? 1 : 0) + (a ? 1 : 0);
                        symptomTrend.add(daily);
                        if (daily > 0) symptomCount++;
                    }
                }

                int pb = snapshot.child("PB").getValue(Integer.class) != null ? snapshot.child("PB").getValue(Integer.class) : 100;
                int green = 0, yellow = 0, red = 0;
                for (DataSnapshot p : snapshot.child("pefLogs").getChildren()) {
                    long ts = parseTimestamp(p.child("timestamp").getValue());
                    Integer val = p.child("pef").getValue(Integer.class);
                    if (ts >= 0 && ts >= start) {
                        double ratio = val * 1.0 / pb;
                        if (ratio >= 0.8) green++;
                        else if (ratio >= 0.5) yellow++;
                        else red++;
                    }
                }

                List<String> triageSummaries = new ArrayList<>();
                for (DataSnapshot t : snapshot.child("triageSessions").getChildren()) {
                    long ts = parseTimestamp(t.child("SymptomCheckTimestamp").getValue());
                    if (ts <= 0 || ts < start) continue;

                    DataSnapshot redFlags = t.child("redFlags");
                    List<String> flags = new ArrayList<>();
                    if (redFlags.exists()) {
                        for (DataSnapshot f : redFlags.getChildren()) {
                            Boolean val = f.getValue(Boolean.class);
                            if (val != null && val) {
                                flags.add(f.getKey());
                            }
                        }
                    }

                    triageSummaries.add("Triage on " + new Date(ts) + ": " + String.join(", ", flags));
                }

                createPDF(months, rescueCount, controllerCount, planDays, symptomCount, green, yellow, red, triageSummaries, symptomTrend);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int countSince(DataSnapshot snapshot, long start) {
        int count = 0;
        for (DataSnapshot s : snapshot.getChildren()) {
            long ts = parseTimestamp(s.child("timestamp").getValue());
            if (ts >= 0 && ts >= start)  count++;
        }
        return count;
    }

    private void createPDF(int months, int rescue, int controller, int planDays, int symptoms, int g, int y, int r, List<String> triage, List<Integer> symptomTrend) {
        try {
            File file = new File(requireContext().getExternalFilesDir(null), "SMARTAIR_Report_" + months + "M.pdf");
            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);

            doc.add(new Paragraph("SMART AIR Report (" + months + " months)").setBold().setFontSize(18));
            doc.add(new Paragraph("Date: " + new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())));
            doc.add(new Paragraph("\nRescue uses: " + rescue));
            doc.add(new Paragraph("Controller uses: " + controller));
            double adherence = planDays == 0 ? 0 : (controller * 100.0 / planDays);
            doc.add(new Paragraph(String.format(Locale.getDefault(),
                    "Adherence: %.1f%% (%d of %d planned days)",
                    adherence, controller, planDays)));
            doc.add(new Paragraph("\nSymptom days: " + symptoms));
            doc.add(new Paragraph("Zones → Green: " + g + ", Yellow: " + y + ", Red: " + r));
            doc.add(new Paragraph("\nTriage incidents:"));
            for (String s : triage) doc.add(new Paragraph("• " + s));

            doc.add(new Paragraph("\nController Adherence Chart:"));
            insertChart(doc, generateBarChart(controller, planDays));
            doc.add(new Paragraph("\nZone Distribution Pie Chart:"));
            insertChart(doc, generateZonePie(g, y, r));
            doc.add(new Paragraph("\nSymptom Trend Chart:"));
            insertChart(doc, generateSymptomLine(symptomTrend));

            doc.close();
            Toast.makeText(getContext(), "Saved to: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            openPDF(file);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "PDF creation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void insertChart(Document doc, Bitmap bitmap) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        ImageData data = ImageDataFactory.create(stream.toByteArray());
        doc.add(new Image(data).scaleToFit(500, 300));
    }

    private Bitmap generateBarChart(int used, int plan) {
        if (!isAdded()) return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        BarChart chart = new BarChart(requireContext());
        List<BarEntry> entries = Arrays.asList(new BarEntry(0, used), new BarEntry(1, plan - used));
        BarDataSet set = new BarDataSet(entries, "Controller Usage");
        set.setColors(Color.GREEN, Color.LTGRAY);
        chart.setData(new BarData(set));
        return renderChart(chart);
    }

    private Bitmap generateZonePie(int g, int y, int r) {
        if (!isAdded()) return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        PieChart chart = new PieChart(requireContext());
        List<PieEntry> entries = new ArrayList<>();
        if (g > 0) entries.add(new PieEntry(g, "Green"));
        if (y > 0) entries.add(new PieEntry(y, "Yellow"));
        if (r > 0) entries.add(new PieEntry(r, "Red"));
        PieDataSet set = new PieDataSet(entries, "Zone Distribution");
        set.setColors(Color.GREEN, Color.YELLOW, Color.RED);
        chart.setData(new PieData(set));
        return renderChart(chart);
    }

    private Bitmap generateSymptomLine(List<Integer> trend) {
        if (!isAdded()) return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        BarChart chart = new BarChart(requireContext());
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < trend.size(); i++) entries.add(new BarEntry(i, trend.get(i)));
        BarDataSet set = new BarDataSet(entries, "Symptom Severity");
        set.setColor(Color.BLUE);
        chart.setData(new BarData(set));
        return renderChart(chart);
    }

    private Bitmap renderChart(View chart) {
        chart.measure(View.MeasureSpec.makeMeasureSpec(800, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(400, View.MeasureSpec.EXACTLY));
        chart.layout(0, 0, chart.getMeasuredWidth(), chart.getMeasuredHeight());
        Bitmap b = Bitmap.createBitmap(chart.getWidth(), chart.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        chart.draw(c);
        return b;
    }

    private void openPDF(File file) {
        Uri uri;
        try {
            uri = FileProvider.getUriForFile(requireContext(), "com.smartair.fileprovider", file);
        } catch (IllegalArgumentException e) {
            Toast.makeText(getContext(), "Failed to open PDF: FileProvider error", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }
    private boolean parseBoolean(Object value) {
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof String) {
            String str = ((String) value).toLowerCase();
            return str.equals("yes") || str.equals("true") || str.equals("1");
        }
        return false;
    }
    private long parseTimestamp(Object value) {
        if (value instanceof Long) return (Long) value;
        if (value instanceof String) {
            try {
                String str = (String) value;
                if (str.matches("\\d+")) {
                    return Long.parseLong(str);
                } else {

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    return sdf.parse(str).getTime();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
}