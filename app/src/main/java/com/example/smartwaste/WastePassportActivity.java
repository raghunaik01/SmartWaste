package com.example.smartwaste;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Locale;

public class WastePassportActivity extends AppCompatActivity {

    private TextView plasticText;
    private TextView paperText;
    private TextView metalText;
    private TextView organicText;
    private TextView totalText;
    private TextView pointsText;
    private TextView statusText;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --------------------------------
        // FIRESTORE
        // --------------------------------

        db = FirebaseFirestore.getInstance();

        // --------------------------------
        // MAIN LAYOUT
        // --------------------------------

        LinearLayout layout = new LinearLayout(this);

        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        layout.setPadding(40, 50, 40, 40);

        layout.setBackgroundColor(
                Color.rgb(18, 15, 22)
        );

        // --------------------------------
        // TITLE
        // --------------------------------

        TextView title = new TextView(this);

        title.setText("♻ Waste Passport");

        title.setTextSize(32);

        title.setTextColor(Color.WHITE);

        title.setTypeface(null, Typeface.BOLD);

        title.setGravity(Gravity.CENTER);

        title.setPadding(10, 20, 10, 40);

        layout.addView(title);

        // --------------------------------
        // STATUS
        // --------------------------------

        statusText = new TextView(this);

        statusText.setText("🔄 Loading waste data...");

        statusText.setTextSize(18);

        statusText.setTextColor(Color.LTGRAY);

        statusText.setGravity(Gravity.CENTER);

        statusText.setPadding(10, 10, 10, 30);

        layout.addView(statusText);

        // --------------------------------
        // PLASTIC
        // --------------------------------

        plasticText = createWasteText(
                "🧴 Plastic: 0 kg"
        );

        layout.addView(plasticText);

        // --------------------------------
        // PAPER
        // --------------------------------

        paperText = createWasteText(
                "📄 Paper: 0 kg"
        );

        layout.addView(paperText);

        // --------------------------------
        // METAL
        // --------------------------------

        metalText = createWasteText(
                "🥫 Metal: 0 kg"
        );

        layout.addView(metalText);

        // --------------------------------
        // ORGANIC
        // --------------------------------

        organicText = createWasteText(
                "🌱 Organic: 0 kg"
        );

        layout.addView(organicText);

        // --------------------------------
        // TOTAL
        // --------------------------------

        totalText = createWasteText(
                "♻ Total Waste: 0 kg"
        );

        totalText.setTypeface(
                null,
                Typeface.BOLD
        );

        layout.addView(totalText);

        // --------------------------------
        // GREEN POINTS
        // --------------------------------

        pointsText = createWasteText(
                "⭐ Green Points: 0"
        );

        pointsText.setTypeface(
                null,
                Typeface.BOLD
        );

        layout.addView(pointsText);

        // --------------------------------
        // SHOW SCREEN
        // --------------------------------

        setContentView(layout);

        // --------------------------------
        // LOAD FIRESTORE DATA
        // --------------------------------

        loadPickupWaste();
    }

    // ============================================================
    // CREATE TEXT VIEW
    // ============================================================

    private TextView createWasteText(String text) {

        TextView view = new TextView(this);

        view.setText(text);

        view.setTextSize(20);

        view.setTextColor(Color.WHITE);

        view.setGravity(Gravity.CENTER_VERTICAL);

        view.setPadding(
                30,
                25,
                30,
                25
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                0,
                8,
                0,
                8
        );

        view.setLayoutParams(params);

        return view;
    }

    // ============================================================
    // LOAD REQUEST PICKUP DATA
    // ============================================================

    private void loadPickupWaste() {

        db.collection("pickup_requests")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    double plastic = 0;
                    double paper = 0;
                    double metal = 0;
                    double organic = 0;

                    // --------------------------------
                    // READ EVERY PICKUP REQUEST
                    // --------------------------------

                    for (QueryDocumentSnapshot document :
                            queryDocumentSnapshots) {

                        String wasteType =
                                document.getString("wasteType");

                        Object weightObject =
                                document.get("weight");

                        double weight = 0;

                        // Firestore can return Double,
                        // Long, Integer etc.
                        if (weightObject instanceof Number) {

                            weight =
                                    ((Number) weightObject)
                                            .doubleValue();
                        }

                        if (wasteType == null) {
                            continue;
                        }

                        wasteType =
                                wasteType.toLowerCase(
                                        Locale.getDefault()
                                );

                        // --------------------------------
                        // CALCULATE CATEGORY
                        // --------------------------------

                        if (wasteType.contains("plastic")) {

                            plastic += weight;

                        } else if (wasteType.contains("paper")) {

                            paper += weight;

                        } else if (wasteType.contains("metal")) {

                            metal += weight;

                        } else if (wasteType.contains("organic")) {

                            organic += weight;
                        }
                    }

                    // --------------------------------
                    // TOTAL WASTE
                    // --------------------------------

                    double total =
                            plastic +
                                    paper +
                                    metal +
                                    organic;

                    // --------------------------------
                    // GREEN POINTS
                    // 10 POINTS / KG
                    // --------------------------------

                    int points =
                            (int) Math.round(total * 10);

                    // --------------------------------
                    // SHOW RESULTS
                    // --------------------------------

                    plasticText.setText(
                            "🧴 Plastic: " +
                                    formatKg(plastic) +
                                    " kg"
                    );

                    paperText.setText(
                            "📄 Paper: " +
                                    formatKg(paper) +
                                    " kg"
                    );

                    metalText.setText(
                            "🥫 Metal: " +
                                    formatKg(metal) +
                                    " kg"
                    );

                    organicText.setText(
                            "🌱 Organic: " +
                                    formatKg(organic) +
                                    " kg"
                    );

                    totalText.setText(
                            "♻ Total Waste: " +
                                    formatKg(total) +
                                    " kg"
                    );

                    pointsText.setText(
                            "⭐ Green Points: " +
                                    points
                    );

                    // --------------------------------
                    // STATUS
                    // --------------------------------

                    if (total > 0) {

                        statusText.setText(
                                "✅ Waste data calculated successfully"
                        );

                    } else {

                        statusText.setText(
                                "ℹ️ No waste records found yet"
                        );
                    }
                })
                .addOnFailureListener(e -> {

                    statusText.setText(
                            "❌ Unable to load waste data"
                    );

                    Toast.makeText(
                            WastePassportActivity.this,
                            "Firestore error: " +
                                    e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // ============================================================
    // FORMAT KG
    // ============================================================

    private String formatKg(double value) {

        if (value == (long) value) {

            return String.format(
                    Locale.getDefault(),
                    "%d",
                    (long) value
            );

        } else {

            return String.format(
                    Locale.getDefault(),
                    "%.2f",
                    value
            );
        }
    }
}