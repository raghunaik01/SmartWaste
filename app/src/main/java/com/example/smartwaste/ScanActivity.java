package com.example.smartwaste;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.util.List;
import java.util.Locale;

public class ScanActivity extends AppCompatActivity {

    private TextView resultText;
    private ImageLabeler labeler;

    // Camera
    private final ActivityResultLauncher<Void> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.TakePicturePreview(),
                    bitmap -> {

                        if (bitmap != null) {

                            resultText.setText("🔍 Recognizing waste...");

                            recognizeWaste(bitmap);

                        } else {

                            Toast.makeText(
                                    ScanActivity.this,
                                    "Camera cancelled",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ==========================================
        // MAIN LAYOUT
        // ==========================================

        LinearLayout layout = new LinearLayout(this);

        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(30, 40, 30, 40);

        layout.setBackgroundColor(
                android.graphics.Color.rgb(18, 15, 22)
        );

        // ==========================================
        // TITLE
        // ==========================================

        TextView title = new TextView(this);

        title.setText("♻ Scan Waste");
        title.setTextSize(32);
        title.setTextColor(android.graphics.Color.WHITE);
        title.setGravity(Gravity.CENTER);
        title.setPadding(20, 30, 20, 30);

        // ==========================================
        // RESULT TEXT
        // ==========================================

        resultText = new TextView(this);

        resultText.setText(
                "📸 Take a photo of your waste"
        );

        resultText.setTextSize(20);
        resultText.setTextColor(android.graphics.Color.WHITE);
        resultText.setGravity(Gravity.CENTER);
        resultText.setPadding(20, 40, 20, 40);

        // ==========================================
        // CAMERA BUTTON
        // ==========================================

        Button cameraButton = new Button(this);

        cameraButton.setText("📷 CAPTURE WASTE");
        cameraButton.setTextSize(18);

        // ==========================================
        // PASSPORT BUTTON
        // ==========================================

        Button passportButton = new Button(this);

        passportButton.setText("♻ VIEW WASTE PASSPORT");
        passportButton.setTextSize(18);

        // ==========================================
        // BACK BUTTON
        // ==========================================

        Button backButton = new Button(this);

        backButton.setText("← BACK");
        backButton.setTextSize(18);

        // ==========================================
        // ADD VIEWS
        // ==========================================

        layout.addView(title);
        layout.addView(resultText);
        layout.addView(cameraButton);
        layout.addView(passportButton);
        layout.addView(backButton);

        setContentView(layout);

        // ==========================================
        // ML KIT
        // ==========================================

        labeler = ImageLabeling.getClient(
                ImageLabelerOptions.DEFAULT_OPTIONS
        );

        // ==========================================
        // CAMERA BUTTON
        // ==========================================

        cameraButton.setOnClickListener(v -> {

            resultText.setText(
                    "📷 Opening camera..."
            );

            cameraLauncher.launch(null);
        });

        // ==========================================
        // PASSPORT BUTTON
        // ==========================================

        passportButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ScanActivity.this,
                    WastePassportActivity.class
            );

            startActivity(intent);
        });

        // ==========================================
        // BACK BUTTON
        // ==========================================

        backButton.setOnClickListener(v -> finish());
    }

    // =========================================================
    // RECOGNIZE WASTE
    // =========================================================

    private void recognizeWaste(Bitmap bitmap) {

        InputImage image =
                InputImage.fromBitmap(bitmap, 0);

        labeler.process(image)

                .addOnSuccessListener(labels -> {

                    String detectedWaste =
                            detectWasteType(labels);

                    if (detectedWaste.equals("unknown")) {

                        resultText.setText(
                                "❓ Waste not recognized\n\n" +
                                        "Please try another photo."
                        );

                        Toast.makeText(
                                ScanActivity.this,
                                "Could not identify waste",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {

                        resultText.setText(
                                "✅ Detected: " +
                                        detectedWaste.toUpperCase()
                        );

                        askWeight(detectedWaste);
                    }
                })

                .addOnFailureListener(e -> {

                    resultText.setText(
                            "❌ Recognition failed"
                    );

                    Toast.makeText(
                            ScanActivity.this,
                            "Recognition error: " +
                                    e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // =========================================================
    // DETECT WASTE TYPE
    // =========================================================

    private String detectWasteType(
            List<ImageLabel> labels
    ) {

        for (ImageLabel label : labels) {

            String text =
                    label.getText().toLowerCase(
                            Locale.getDefault()
                    );

            // PLASTIC
            if (text.contains("plastic")
                    || text.contains("bottle")
                    || text.contains("container")
                    || text.contains("packaging")) {

                return "plastic";
            }

            // PAPER
            if (text.contains("paper")
                    || text.contains("cardboard")
                    || text.contains("book")
                    || text.contains("document")) {

                return "paper";
            }

            // METAL
            if (text.contains("metal")
                    || text.contains("can")
                    || text.contains("aluminium")
                    || text.contains("steel")) {

                return "metal";
            }

            // ORGANIC
            if (text.contains("food")
                    || text.contains("fruit")
                    || text.contains("vegetable")
                    || text.contains("plant")
                    || text.contains("flower")) {

                return "organic";
            }
        }

        return "unknown";
    }

    // =========================================================
    // ASK WEIGHT
    // =========================================================

    private void askWeight(String wasteType) {

        EditText weightInput =
                new EditText(this);

        weightInput.setHint(
                "Enter weight in kg"
        );

        weightInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        LinearLayout container =
                new LinearLayout(this);

        container.setPadding(
                50,
                10,
                50,
                10
        );

        container.addView(weightInput);

        new AlertDialog.Builder(this)

                .setTitle("⚖ Waste Weight")

                .setMessage(
                        "Detected waste: " +
                                wasteType.toUpperCase() +
                                "\n\nEnter the weight:"
                )

                .setView(container)

                .setPositiveButton(
                        "SAVE",
                        (dialog, which) -> {

                            String value =
                                    weightInput
                                            .getText()
                                            .toString()
                                            .trim();

                            if (value.isEmpty()) {

                                Toast.makeText(
                                        ScanActivity.this,
                                        "Please enter weight",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            try {

                                float weight =
                                        Float.parseFloat(value);

                                if (weight <= 0) {

                                    Toast.makeText(
                                            ScanActivity.this,
                                            "Weight must be greater than 0",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }

                                saveWaste(
                                        wasteType,
                                        weight
                                );

                            } catch (
                                    NumberFormatException e
                            ) {

                                Toast.makeText(
                                        ScanActivity.this,
                                        "Enter a valid weight",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )

                .setNegativeButton(
                        "CANCEL",
                        null
                )

                .show();
    }

    // =========================================================
    // SAVE WASTE
    // =========================================================

    private void saveWaste(
            String wasteType,
            float weight
    ) {

        SharedPreferences prefs =
                getSharedPreferences(
                        "WasteData",
                        MODE_PRIVATE
                );

        // Get previous values

        float plastic =
                prefs.getFloat(
                        "plastic",
                        0f
                );

        float paper =
                prefs.getFloat(
                        "paper",
                        0f
                );

        float metal =
                prefs.getFloat(
                        "metal",
                        0f
                );

        float organic =
                prefs.getFloat(
                        "organic",
                        0f
                );

        // Add new weight

        if (wasteType.equals("plastic")) {

            plastic += weight;

        } else if (wasteType.equals("paper")) {

            paper += weight;

        } else if (wasteType.equals("metal")) {

            metal += weight;

        } else if (wasteType.equals("organic")) {

            organic += weight;
        }

        // Calculate total

        float total =
                plastic
                        + paper
                        + metal
                        + organic;

        // ==========================================
        // SAVE WASTE DATA
        // ==========================================

        prefs.edit()

                .putFloat(
                        "plastic",
                        plastic
                )

                .putFloat(
                        "paper",
                        paper
                )

                .putFloat(
                        "metal",
                        metal
                )

                .putFloat(
                        "organic",
                        organic
                )

                .putFloat(
                        "total",
                        total
                )

                .apply();

        // ==========================================
        // GREEN POINTS
        // ==========================================

        int points =
                prefs.getInt(
                        "greenPoints",
                        0
                );

        // IMPORTANT:
        // Correct Math.round syntax

        int earnedPoints =
                Math.round(weight * 10);

        points += earnedPoints;

        prefs.edit()

                .putInt(
                        "greenPoints",
                        points
                )

                .apply();

        // ==========================================
        // SHOW SUCCESS
        // ==========================================

        resultText.setText(

                "✅ Waste Saved!\n\n" +

                        "Type: " +
                        wasteType.toUpperCase() +

                        "\nWeight: " +
                        weight +
                        " kg\n\n" +

                        "⭐ +" +
                        earnedPoints +
                        " Green Points"
        );

        Toast.makeText(
                this,
                "♻ Waste saved successfully!",
                Toast.LENGTH_LONG
        ).show();
    }

    // =========================================================
    // CLOSE ML KIT
    // =========================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (labeler != null) {

            labeler.close();
        }
    }
}