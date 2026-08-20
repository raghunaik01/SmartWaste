package com.example.smartwaste;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PickupActivity extends AppCompatActivity {

    private EditText etAddress;
    private EditText etWasteType;
    private EditText etWeight;
    private EditText etPickupDate;
    private EditText etPickupTime;
    private Button btnSubmitPickup;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pickup);

        etAddress = findViewById(R.id.etAddress);
        etWasteType = findViewById(R.id.etWasteType);
        etWeight = findViewById(R.id.etWeight);
        etPickupDate = findViewById(R.id.etPickupDate);
        etPickupTime = findViewById(R.id.etPickupTime);
        btnSubmitPickup = findViewById(R.id.btnSubmitPickup);

        db = FirebaseFirestore.getInstance();

        btnSubmitPickup.setOnClickListener(v -> submitPickupRequest());
    }

    private void submitPickupRequest() {

        String address = etAddress.getText().toString().trim();
        String wasteType = etWasteType.getText().toString().trim();
        String weightText = etWeight.getText().toString().trim();
        String pickupDate = etPickupDate.getText().toString().trim();
        String pickupTime = etPickupTime.getText().toString().trim();

        if (address.isEmpty() ||
                wasteType.isEmpty() ||
                weightText.isEmpty() ||
                pickupDate.isEmpty() ||
                pickupTime.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        double weight;

        try {
            weight = Double.parseDouble(weightText);
        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Enter a valid weight",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (weight <= 0) {

            Toast.makeText(
                    this,
                    "Weight must be greater than 0",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Map<String, Object> pickupRequest = new HashMap<>();

        pickupRequest.put("address", address);
        pickupRequest.put("wasteType", wasteType);
        pickupRequest.put("weight", weight);
        pickupRequest.put("pickupDate", pickupDate);
        pickupRequest.put("pickupTime", pickupTime);
        pickupRequest.put("timestamp",
                com.google.firebase.firestore.FieldValue.serverTimestamp());

        db.collection("pickup_requests")
                .add(pickupRequest)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(
                            PickupActivity.this,
                            "Pickup request submitted successfully!",
                            Toast.LENGTH_LONG
                    ).show();

                    etAddress.setText("");
                    etWasteType.setText("");
                    etWeight.setText("");
                    etPickupDate.setText("");
                    etPickupTime.setText("");
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            PickupActivity.this,
                            "Failed to save request: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}