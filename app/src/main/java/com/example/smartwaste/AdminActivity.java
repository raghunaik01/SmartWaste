package com.example.smartwaste;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class AdminActivity extends AppCompatActivity {

    private TextView totalUsersText;
    private TextView totalWasteText;
    private TextView totalPickupsText;

    private Button viewUsersButton;
    private Button viewPickupsButton;
    private Button viewWasteButton;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin);

        // Firebase
        db = FirebaseFirestore.getInstance();

        // Connect XML views
        totalUsersText = findViewById(R.id.totalUsersText);
        totalWasteText = findViewById(R.id.totalWasteText);
        totalPickupsText = findViewById(R.id.totalPickupsText);

        viewUsersButton = findViewById(R.id.viewUsersButton);
        viewPickupsButton = findViewById(R.id.viewPickupsButton);
        viewWasteButton = findViewById(R.id.viewWasteButton);

        // Load dashboard data
        loadUsers();
        loadPickupData();

        // VIEW USERS
        viewUsersButton.setOnClickListener(v -> {
            showUsers();
        });

        // VIEW PICKUP REQUESTS
        viewPickupsButton.setOnClickListener(v -> {
            showPickupRequests();
        });

        // VIEW WASTE DATA
        viewWasteButton.setOnClickListener(v -> {
            showWasteData();
        });
    }

    // ------------------------------------------------
    // LOAD USERS COUNT
    // ------------------------------------------------

    private void loadUsers() {

        db.collection("users")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    int userCount = querySnapshot.size();

                    totalUsersText.setText(
                            "Total Users: " + userCount
                    );

                })
                .addOnFailureListener(e -> {

                    totalUsersText.setText(
                            "Total Users: Error"
                    );

                    Toast.makeText(
                            AdminActivity.this,
                            "Could not load users",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    // ------------------------------------------------
    // LOAD PICKUP COUNT + TOTAL WASTE
    // ------------------------------------------------

    private void loadPickupData() {

        db.collection("pickup_requests")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    int pickupCount = querySnapshot.size();

                    double totalWaste = 0.0;

                    for (QueryDocumentSnapshot document : querySnapshot) {

                        Object weightObject =
                                document.get("weight");

                        if (weightObject instanceof Number) {

                            totalWaste +=
                                    ((Number) weightObject).doubleValue();
                        }
                    }

                    totalPickupsText.setText(
                            "Pickup Requests: " + pickupCount
                    );

                    totalWasteText.setText(
                            String.format(
                                    "Total Waste: %.2f kg",
                                    totalWaste
                            )
                    );

                })
                .addOnFailureListener(e -> {

                    totalPickupsText.setText(
                            "Pickup Requests: Error"
                    );

                    totalWasteText.setText(
                            "Total Waste: Error"
                    );

                    Toast.makeText(
                            AdminActivity.this,
                            "Firestore error",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // ------------------------------------------------
    // VIEW USERS
    // ------------------------------------------------

    private void showUsers() {

        db.collection("users")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {

                        showDialog(
                                "Users",
                                "No users found."
                        );

                        return;
                    }

                    StringBuilder result =
                            new StringBuilder();

                    result.append("Total Users: ")
                            .append(querySnapshot.size())
                            .append("\n\n");

                    for (QueryDocumentSnapshot document :
                            querySnapshot) {

                        result.append("User ID:\n")
                                .append(document.getId())
                                .append("\n");

                        if (document.getString("name") != null) {

                            result.append("Name: ")
                                    .append(
                                            document.getString("name")
                                    )
                                    .append("\n");
                        }

                        if (document.getString("email") != null) {

                            result.append("Email: ")
                                    .append(
                                            document.getString("email")
                                    )
                                    .append("\n");
                        }

                        result.append("--------------------\n");
                    }

                    showDialog(
                            "Users",
                            result.toString()
                    );

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            AdminActivity.this,
                            "Could not load users",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    // ------------------------------------------------
    // VIEW PICKUP REQUESTS
    // ------------------------------------------------

    private void showPickupRequests() {

        db.collection("pickup_requests")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {

                        showDialog(
                                "Pickup Requests",
                                "No pickup requests found."
                        );

                        return;
                    }

                    StringBuilder result =
                            new StringBuilder();

                    result.append("Total Pickup Requests: ")
                            .append(querySnapshot.size())
                            .append("\n\n");

                    int number = 1;

                    for (QueryDocumentSnapshot document :
                            querySnapshot) {

                        result.append("Request ")
                                .append(number)
                                .append("\n");

                        String address =
                                document.getString("address");

                        String pickupDate =
                                document.getString("pickupDate");

                        String pickupTime =
                                document.getString("pickupTime");

                        String status =
                                document.getString("status");

                        String wasteType =
                                document.getString("wasteType");

                        Object weight =
                                document.get("weight");

                        if (address != null) {
                            result.append("Address: ")
                                    .append(address)
                                    .append("\n");
                        }

                        if (pickupDate != null) {
                            result.append("Date: ")
                                    .append(pickupDate)
                                    .append("\n");
                        }

                        if (pickupTime != null) {
                            result.append("Time: ")
                                    .append(pickupTime)
                                    .append("\n");
                        }

                        if (wasteType != null) {
                            result.append("Waste Type: ")
                                    .append(wasteType)
                                    .append("\n");
                        }

                        if (weight instanceof Number) {

                            result.append("Weight: ")
                                    .append(
                                            ((Number) weight)
                                                    .doubleValue()
                                    )
                                    .append(" kg\n");
                        }

                        if (status != null) {

                            result.append("Status: ")
                                    .append(status)
                                    .append("\n");
                        }

                        result.append("--------------------\n");

                        number++;
                    }

                    showDialog(
                            "Pickup Requests",
                            result.toString()
                    );

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            AdminActivity.this,
                            "Could not load pickup requests",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    // ------------------------------------------------
    // VIEW WASTE DATA
    // ------------------------------------------------

    private void showWasteData() {

        db.collection("pickup_requests")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {

                        showDialog(
                                "Waste Data",
                                "No waste data found."
                        );

                        return;
                    }

                    StringBuilder result =
                            new StringBuilder();

                    double totalWaste = 0.0;

                    result.append("WASTE DATA\n\n");

                    int number = 1;

                    for (QueryDocumentSnapshot document :
                            querySnapshot) {

                        String wasteType =
                                document.getString("wasteType");

                        Object weight =
                                document.get("weight");

                        result.append("Waste ")
                                .append(number)
                                .append("\n");

                        if (wasteType != null) {

                            result.append("Type: ")
                                    .append(wasteType)
                                    .append("\n");
                        } else {

                            result.append("Type: Unknown\n");
                        }

                        if (weight instanceof Number) {

                            double kg =
                                    ((Number) weight)
                                            .doubleValue();

                            totalWaste += kg;

                            result.append("Weight: ")
                                    .append(
                                            String.format(
                                                    "%.2f",
                                                    kg
                                            )
                                    )
                                    .append(" kg\n");

                        } else {

                            result.append(
                                    "Weight: Unknown\n"
                            );
                        }

                        String status =
                                document.getString("status");

                        if (status != null) {

                            result.append("Status: ")
                                    .append(status)
                                    .append("\n");
                        }

                        result.append("--------------------\n");

                        number++;
                    }

                    result.append("\nTOTAL WASTE: ")
                            .append(
                                    String.format(
                                            "%.2f",
                                            totalWaste
                                    )
                            )
                            .append(" kg");

                    showDialog(
                            "Waste Data",
                            result.toString()
                    );

                    Toast.makeText(
                            AdminActivity.this,
                            "Waste Data loaded",
                            Toast.LENGTH_SHORT
                    ).show();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            AdminActivity.this,
                            "Could not load waste data",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // ------------------------------------------------
    // DIALOG
    // ------------------------------------------------

    private void showDialog(
            String title,
            String message
    ) {

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(
                        "OK",
                        null
                )
                .show();
    }
}