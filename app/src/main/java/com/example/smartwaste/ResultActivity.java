package com.example.smartwaste;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    private TextView txtResult;
    private TextView txtPoints;
    private Button btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_result);

        // Connect XML views
        txtResult = findViewById(R.id.txtResult);
        txtPoints = findViewById(R.id.txtPoints);
        btnDone = findViewById(R.id.btnDone);

        // Get waste type from ScanActivity
        String wasteType = getIntent().getStringExtra("wasteType");

        if (wasteType == null || wasteType.trim().isEmpty()) {
            wasteType = "Unknown";
        }

        // Calculate points
        int points = 0;

        if (wasteType.equalsIgnoreCase("Plastic")) {
            points = 10;
        } else if (wasteType.equalsIgnoreCase("Paper")) {
            points = 15;
        } else if (wasteType.equalsIgnoreCase("Metal")) {
            points = 25;
        } else if (wasteType.equalsIgnoreCase("Organic")) {
            points = 20;
        } else {
            points = 0;
        }

        // Show result
        txtResult.setText("♻ " + wasteType);
        txtPoints.setText("🏆 Green Points: " + points);

        // Save scan data
        saveWasteData(wasteType, points);

        // Done button
        btnDone.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ResultActivity.this,
                    MainActivity.class
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            startActivity(intent);
            finish();
        });
    }

    private void saveWasteData(String wasteType, int points) {

        SharedPreferences prefs =
                getSharedPreferences("WasteData", MODE_PRIVATE);

        SharedPreferences.Editor editor =
                prefs.edit();

        // Existing values
        float plastic = prefs.getFloat("plastic", 0);
        float paper = prefs.getFloat("paper", 0);
        float metal = prefs.getFloat("metal", 0);
        float organic = prefs.getFloat("organic", 0);

        float total = prefs.getFloat("total", 0);

        int greenPoints = prefs.getInt("greenPoints", 0);

        // Demo: each successful scan = 1 kg
        float scanWeight = 1.0f;

        // Add weight according to waste type
        if (wasteType.equalsIgnoreCase("Plastic")) {

            plastic += scanWeight;

        } else if (wasteType.equalsIgnoreCase("Paper")) {

            paper += scanWeight;

        } else if (wasteType.equalsIgnoreCase("Metal")) {

            metal += scanWeight;

        } else if (wasteType.equalsIgnoreCase("Organic")) {

            organic += scanWeight;
        }

        // Calculate total
        total = plastic + paper + metal + organic;

        // Add points
        greenPoints += points;

        // Save everything
        editor.putFloat("plastic", plastic);
        editor.putFloat("paper", paper);
        editor.putFloat("metal", metal);
        editor.putFloat("organic", organic);
        editor.putFloat("total", total);
        editor.putInt("greenPoints", greenPoints);

        editor.apply();
    }
}