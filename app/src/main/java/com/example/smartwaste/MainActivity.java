package com.example.smartwaste;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Firebase
        db = FirebaseFirestore.getInstance();

        // Save user to Firestore
        saveUserToFirestore();

        // ------------------------------------------------
        // MAIN LAYOUT
        // ------------------------------------------------

        LinearLayout mainLayout = new LinearLayout(this);

        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.CENTER);
        mainLayout.setPadding(40, 40, 40, 40);

        mainLayout.setBackgroundColor(
                Color.rgb(18, 15, 22)
        );

        // ------------------------------------------------
        // TITLE
        // ------------------------------------------------

        TextView title = new TextView(this);

        title.setText("♻ Waste2Worth");
        title.setTextSize(32);
        title.setTextColor(Color.WHITE);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        // ------------------------------------------------
        // SUBTITLE
        // ------------------------------------------------

        TextView subtitle = new TextView(this);

        subtitle.setText("Smart Waste Management");
        subtitle.setTextSize(18);
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 15, 0, 30);

        // ------------------------------------------------
        // SCAN WASTE
        // ------------------------------------------------

        Button scanButton = new Button(this);

        scanButton.setText("♻ SCAN WASTE");
        scanButton.setTextSize(18);

        // ------------------------------------------------
        // REQUEST PICKUP
        // ------------------------------------------------

        Button pickupButton = new Button(this);

        pickupButton.setText("🚛 REQUEST PICKUP");
        pickupButton.setTextSize(18);

        // ------------------------------------------------
        // WASTE PASSPORT
        // ------------------------------------------------

        Button passportButton = new Button(this);

        passportButton.setText("♻ WASTE PASSPORT");
        passportButton.setTextSize(18);

        // ------------------------------------------------
        // GREEN POINTS
        // ------------------------------------------------

        Button pointsButton = new Button(this);

        pointsButton.setText("⭐ GREEN POINTS");
        pointsButton.setTextSize(18);

        // ------------------------------------------------
        // COLLECTOR DASHBOARD
        // ------------------------------------------------

        Button collectorButton = new Button(this);

        collectorButton.setText("🚛 COLLECTOR DASHBOARD");
        collectorButton.setTextSize(18);

        // ------------------------------------------------
        // ADMIN DASHBOARD
        // ------------------------------------------------

        Button adminButton = new Button(this);

        adminButton.setText("🛡 ADMIN DASHBOARD");
        adminButton.setTextSize(18);

        // ------------------------------------------------
        // ADD VIEWS
        // ------------------------------------------------

        mainLayout.addView(title);
        mainLayout.addView(subtitle);

        mainLayout.addView(scanButton);
        mainLayout.addView(pickupButton);
        mainLayout.addView(passportButton);
        mainLayout.addView(pointsButton);
        mainLayout.addView(collectorButton);
        mainLayout.addView(adminButton);

        // ------------------------------------------------
        // SCAN BUTTON
        // ------------------------------------------------

        scanButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent intent =
                                new Intent(
                                        MainActivity.this,
                                        ScanActivity.class
                                );

                        startActivity(intent);
                    }
                }
        );

        // ------------------------------------------------
        // PICKUP BUTTON
        // ------------------------------------------------

        pickupButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent intent =
                                new Intent(
                                        MainActivity.this,
                                        PickupActivity.class
                                );

                        startActivity(intent);
                    }
                }
        );

        // ------------------------------------------------
        // PASSPORT BUTTON
        // ------------------------------------------------

        passportButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent intent =
                                new Intent(
                                        MainActivity.this,
                                        WastePassportActivity.class
                                );

                        startActivity(intent);
                    }
                }
        );

        // ------------------------------------------------
        // GREEN POINTS BUTTON
        // ------------------------------------------------

        pointsButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent intent =
                                new Intent(
                                        MainActivity.this,
                                        GreenPointsActivity.class
                                );

                        startActivity(intent);
                    }
                }
        );

        // ------------------------------------------------
        // COLLECTOR DASHBOARD BUTTON
        // ------------------------------------------------

        collectorButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent intent =
                                new Intent(
                                        MainActivity.this,
                                        CollectorActivity.class
                                );

                        startActivity(intent);
                    }
                }
        );

        // ------------------------------------------------
        // ADMIN DASHBOARD BUTTON
        // ------------------------------------------------

        adminButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent intent =
                                new Intent(
                                        MainActivity.this,
                                        AdminActivity.class
                                );

                        startActivity(intent);
                    }
                }
        );

        // ------------------------------------------------
        // SHOW SCREEN
        // ------------------------------------------------

        setContentView(mainLayout);
    }

    // ====================================================
    // SAVE USER TO FIRESTORE
    // ====================================================

    private void saveUserToFirestore() {

        // Get unique ID for this phone
        String userId =
                Settings.Secure.getString(
                        getContentResolver(),
                        Settings.Secure.ANDROID_ID
                );

        // User information
        Map<String, Object> user =
                new HashMap<>();

        user.put(
                "name",
                "Waste2Worth Citizen"
        );

        user.put(
                "email",
                "citizen@waste2worth.com"
        );

        user.put(
                "createdAt",
                Timestamp.now()
        );

        // Save to Firestore
        db.collection("users")
                .document(userId)
                .set(user)
                .addOnSuccessListener(
                        unused -> {

                            Toast.makeText(
                                    MainActivity.this,
                                    "User registered successfully",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                )
                .addOnFailureListener(
                        e -> {

                            Toast.makeText(
                                    MainActivity.this,
                                    "User registration failed",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }
}
