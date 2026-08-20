package com.example.smartwaste;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CollectorActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout requestLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        // ==========================================
        // MAIN LAYOUT
        // ==========================================

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(30, 30, 30, 30);
        mainLayout.setBackgroundColor(Color.rgb(18, 15, 22));

        // ==========================================
        // TITLE
        // ==========================================

        TextView title = new TextView(this);
        title.setText("🚛 Collector Dashboard");
        title.setTextSize(30);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 20, 0, 20);

        // ==========================================
        // HEADING
        // ==========================================

        TextView heading = new TextView(this);
        heading.setText("♻ Pickup Requests");
        heading.setTextSize(24);
        heading.setTextColor(Color.WHITE);
        heading.setPadding(0, 10, 0, 15);

        // ==========================================
        // REFRESH BUTTON
        // ==========================================

        Button refreshButton = new Button(this);
        refreshButton.setText("🔄 REFRESH REQUESTS");
        refreshButton.setTextSize(16);

        // ==========================================
        // REQUEST CONTAINER
        // ==========================================

        requestLayout = new LinearLayout(this);
        requestLayout.setOrientation(LinearLayout.VERTICAL);

        // ==========================================
        // SCROLL VIEW
        // ==========================================

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(requestLayout);

        // ==========================================
        // ADD VIEWS
        // ==========================================

        mainLayout.addView(title);
        mainLayout.addView(heading);
        mainLayout.addView(refreshButton);
        mainLayout.addView(scrollView);

        setContentView(mainLayout);

        // ==========================================
        // LOAD REQUESTS
        // ==========================================

        loadPickupRequests();

        // ==========================================
        // REFRESH
        // ==========================================

        refreshButton.setOnClickListener(v -> {
            loadPickupRequests();
        });
    }

    // =========================================================
    // LOAD PICKUP REQUESTS FROM FIRESTORE
    // =========================================================

    private void loadPickupRequests() {

        requestLayout.removeAllViews();

        TextView loading = new TextView(this);
        loading.setText("⏳ Loading pickup requests...");
        loading.setTextSize(20);
        loading.setTextColor(Color.WHITE);
        loading.setGravity(Gravity.CENTER);
        loading.setPadding(20, 30, 20, 30);

        requestLayout.addView(loading);

        db.collection("pickup_requests")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    requestLayout.removeAllViews();

                    if (querySnapshot.isEmpty()) {

                        TextView empty = new TextView(this);

                        empty.setText(
                                "📭 No pickup requests found."
                        );

                        empty.setTextSize(20);
                        empty.setTextColor(Color.WHITE);
                        empty.setGravity(Gravity.CENTER);
                        empty.setPadding(20, 40, 20, 40);

                        requestLayout.addView(empty);

                        return;
                    }

                    // Number of requests
                    TextView countText = new TextView(this);

                    countText.setText(
                            "Total Requests: "
                                    + querySnapshot.size()
                    );

                    countText.setTextSize(21);
                    countText.setTextColor(Color.WHITE);
                    countText.setPadding(0, 10, 0, 20);

                    requestLayout.addView(countText);

                    // Display every request
                    for (DocumentSnapshot document
                            : querySnapshot.getDocuments()) {

                        createRequestCard(document);
                    }

                })
                .addOnFailureListener(e -> {

                    requestLayout.removeAllViews();

                    TextView errorText = new TextView(this);

                    errorText.setText(
                            "❌ Failed to load pickup requests\n\n"
                                    + e.getMessage()
                    );

                    errorText.setTextSize(18);
                    errorText.setTextColor(Color.WHITE);
                    errorText.setPadding(20, 30, 20, 30);

                    requestLayout.addView(errorText);

                    Toast.makeText(
                            CollectorActivity.this,
                            "Firebase error: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // =========================================================
    // CREATE REQUEST CARD
    // =========================================================

    private void createRequestCard(
            DocumentSnapshot document
    ) {

        // ==========================================
        // GET FIRESTORE DATA
        // ==========================================

        String address =
                document.getString("address");

        String wasteType =
                document.getString("wasteType");

        String pickupDate =
                document.getString("pickupDate");

        String pickupTime =
                document.getString("pickupTime");

        String status =
                document.getString("status");

        Double weight =
                document.getDouble("weight");

        // ==========================================
        // DEFAULT VALUES
        // ==========================================

        if (address == null) {
            address = "Not provided";
        }

        if (wasteType == null) {
            wasteType = "Not provided";
        }

        if (pickupDate == null) {
            pickupDate = "Not provided";
        }

        if (pickupTime == null) {
            pickupTime = "Not provided";
        }

        if (status == null) {
            status = "Pending";
        }

        // ==========================================
        // WEIGHT
        // ==========================================

        String weightText;

        if (weight != null) {

            weightText =
                    String.format(
                            "%.1f kg",
                            weight
                    );

        } else {

            weightText = "Not provided";
        }

        // ==========================================
        // CARD
        // ==========================================

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                25,
                25,
                25,
                25
        );

        card.setBackgroundColor(
                Color.rgb(55, 52, 60)
        );

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                0,
                0,
                25
        );

        card.setLayoutParams(cardParams);

        // ==========================================
        // REQUEST TITLE
        // ==========================================

        TextView requestTitle =
                new TextView(this);

        requestTitle.setText(
                "♻ Pickup Request"
        );

        requestTitle.setTextSize(23);
        requestTitle.setTextColor(Color.WHITE);
        requestTitle.setPadding(
                0,
                0,
                0,
                20
        );

        // ==========================================
        // DETAILS
        // ==========================================

        TextView details =
                new TextView(this);

        details.setText(
                "📍 Address: "
                        + address

                        + "\n\n♻ Waste Type: "
                        + wasteType

                        + "\n\n⚖ Weight: "
                        + weightText

                        + "\n\n📅 Pickup Date: "
                        + pickupDate

                        + "\n\n⏰ Pickup Time: "
                        + pickupTime

                        + "\n\n📌 Status: "
                        + status
        );

        details.setTextSize(19);
        details.setTextColor(Color.WHITE);

        card.addView(requestTitle);
        card.addView(details);

        // ==========================================
        // STATUS
        // ==========================================

        String currentStatus =
                status.trim().toLowerCase();

        // ==========================================
        // PENDING
        // ==========================================

        if (currentStatus.equals("pending")) {

            Button acceptButton =
                    new Button(this);

            acceptButton.setText(
                    "✅ ACCEPT PICKUP"
            );

            acceptButton.setTextSize(17);

            card.addView(acceptButton);

            acceptButton.setOnClickListener(v -> {

                document.getReference()
                        .update(
                                "status",
                                "Accepted"
                        )

                        .addOnSuccessListener(unused -> {

                            Toast.makeText(
                                    CollectorActivity.this,
                                    "Pickup accepted ✅",
                                    Toast.LENGTH_SHORT
                            ).show();

                            loadPickupRequests();
                        })

                        .addOnFailureListener(e -> {

                            Toast.makeText(
                                    CollectorActivity.this,
                                    "Error: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        });
            });
        }

        // ==========================================
        // ACCEPTED
        // ==========================================

        else if (
                currentStatus.equals("accepted")
        ) {

            Button collectButton =
                    new Button(this);

            collectButton.setText(
                    "🚛 MARK COLLECTED"
            );

            collectButton.setTextSize(17);

            card.addView(collectButton);

            collectButton.setOnClickListener(v -> {

                document.getReference()
                        .update(
                                "status",
                                "Collected"
                        )

                        .addOnSuccessListener(unused -> {

                            Toast.makeText(
                                    CollectorActivity.this,
                                    "Waste collected ♻️",
                                    Toast.LENGTH_SHORT
                            ).show();

                            loadPickupRequests();
                        })

                        .addOnFailureListener(e -> {

                            Toast.makeText(
                                    CollectorActivity.this,
                                    "Error: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        });
            });
        }

        // ==========================================
        // COLLECTED
        // ==========================================

        else if (
                currentStatus.equals("collected")
        ) {

            TextView collectedText =
                    new TextView(this);

            collectedText.setText(
                    "✅ WASTE COLLECTED"
            );

            collectedText.setTextSize(19);
            collectedText.setTextColor(Color.WHITE);
            collectedText.setGravity(
                    Gravity.CENTER
            );

            collectedText.setPadding(
                    0,
                    20,
                    0,
                    10
            );

            card.addView(collectedText);
        }

        // ==========================================
        // OTHER STATUS
        // ==========================================

        else {

            TextView statusText =
                    new TextView(this);

            statusText.setText(
                    "📌 Status: "
                            + status
            );

            statusText.setTextSize(18);
            statusText.setTextColor(Color.WHITE);

            card.addView(statusText);
        }

        // ==========================================
        // ADD CARD
        // ==========================================

        requestLayout.addView(card);
    }
}