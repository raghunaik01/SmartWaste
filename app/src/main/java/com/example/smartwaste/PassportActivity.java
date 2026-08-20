package com.example.smartwaste;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class PassportActivity extends AppCompatActivity {

    private TextView tvPlastic;
    private TextView tvPaper;
    private TextView tvEWaste;
    private TextView tvTotal;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_passport);

        tvPlastic = findViewById(R.id.tvPlastic);
        tvPaper = findViewById(R.id.tvPaper);
        tvEWaste = findViewById(R.id.tvEWaste);
        tvTotal = findViewById(R.id.tvTotal);

        db = FirebaseFirestore.getInstance();

        loadWasteData();
    }

    private void loadWasteData() {

        db.collection("pickup_requests")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    double plasticKg = 0;
                    double paperKg = 0;
                    double eWasteKg = 0;
                    double totalKg = 0;

                    for (var document : queryDocumentSnapshots.getDocuments()) {

                        String wasteType = document.getString("wasteType");
                        Double weight = document.getDouble("weight");

                        if (wasteType == null || weight == null) {
                            continue;
                        }

                        String type = wasteType.toLowerCase();

                        if (type.contains("plastic")) {
                            plasticKg += weight;
                        } else if (type.contains("paper")) {
                            paperKg += weight;
                        } else if (type.contains("e-waste")
                                || type.contains("ewaste")
                                || type.contains("e waste")) {
                            eWasteKg += weight;
                        }

                        totalKg += weight;
                    }

                    tvPlastic.setText(
                            "🧴 Plastic Recycled: "
                                    + plasticKg + " kg"
                    );

                    tvPaper.setText(
                            "📄 Paper Recycled: "
                                    + paperKg + " kg"
                    );

                    tvEWaste.setText(
                            "💻 E-Waste Recycled: "
                                    + eWasteKg + " kg"
                    );

                    tvTotal.setText(
                            "♻️ Total Waste Recycled: "
                                    + totalKg + " kg"
                    );
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            PassportActivity.this,
                            "Failed to load Firestore data",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}