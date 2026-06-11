package com.example.individiualassignment_wanhaziq;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Spinner spinnerMonth;
    EditText editTextUnit;
    SeekBar seekBarRebate;
    TextView textRebateValue, textTotalCharges, textFinalCost;
    Button buttonCalculate, buttonSave, buttonViewHistory, buttonAbout;

    DatabaseHelper databaseHelper;

    int selectedRebate = 0;
    int currentUnit = 0;
    String currentMonth = "";

    double totalCharges = 0;
    double finalCost = 0;

    boolean hasCalculated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        spinnerMonth = findViewById(R.id.spinnerMonth);
        editTextUnit = findViewById(R.id.editTextUnit);
        seekBarRebate = findViewById(R.id.seekBarRebate);
        textRebateValue = findViewById(R.id.textRebateValue);
        textTotalCharges = findViewById(R.id.textTotalCharges);
        textFinalCost = findViewById(R.id.textFinalCost);

        buttonCalculate = findViewById(R.id.buttonCalculate);
        buttonSave = findViewById(R.id.buttonSave);
        buttonViewHistory = findViewById(R.id.buttonViewHistory);
        buttonAbout = findViewById(R.id.buttonAbout);

        setupMonthSpinner();
        setupRebateSeekBar();

        buttonCalculate.setOnClickListener(v -> calculateBill());

        buttonSave.setOnClickListener(v -> {
            if (!hasCalculated) {
                Toast.makeText(this, "Please calculate the bill before saving.", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isInserted = databaseHelper.insertBill(
                    currentMonth,
                    currentUnit,
                    totalCharges,
                    selectedRebate,
                    finalCost
            );

            if (isInserted) {
                Toast.makeText(this, "Record saved successfully.", Toast.LENGTH_SHORT).show();
                hasCalculated = false;
            } else {
                Toast.makeText(this, "Failed to save record.", Toast.LENGTH_SHORT).show();
            }
        });

        buttonViewHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        buttonAbout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });
    }

    private void setupMonthSpinner() {
        String[] months = {
                "Select Month",
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                months
        );

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);
    }

    private void setupRebateSeekBar() {
        seekBarRebate.setMax(5);
        seekBarRebate.setProgress(0);

        seekBarRebate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedRebate = progress;
                textRebateValue.setText("Selected Rebate: " + selectedRebate + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not needed
            }
        });
    }

    private void calculateBill() {
        String selectedMonth = spinnerMonth.getSelectedItem().toString();
        String unitText = editTextUnit.getText().toString().trim();

        if (selectedMonth.equals("Select Month")) {
            Toast.makeText(this, "Please select a month.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (unitText.isEmpty()) {
            Toast.makeText(this, "Please enter electricity unit used.", Toast.LENGTH_SHORT).show();
            return;
        }

        int unit = Integer.parseInt(unitText);

        if (unit < 1) {
            Toast.makeText(this, "Unit must be at least 1 kWh.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (unit > 1000) {
            Toast.makeText(this, "Unit cannot exceed 1000 kWh.", Toast.LENGTH_SHORT).show();
            return;
        }

        currentMonth = selectedMonth;
        currentUnit = unit;

        totalCharges = calculateTotalCharges(unit);
        finalCost = totalCharges - (totalCharges * selectedRebate / 100);

        textTotalCharges.setText(String.format("Total Charges: RM %.2f", totalCharges));
        textFinalCost.setText(String.format("Final Cost After Rebate: RM %.2f", finalCost));

        hasCalculated = true;

        Toast.makeText(this, "Calculation completed successfully.", Toast.LENGTH_SHORT).show();
    }

    private double calculateTotalCharges(int unit) {
        double total;

        if (unit <= 200) {
            total = unit * 0.218;
        } else if (unit <= 300) {
            total = (200 * 0.218) + ((unit - 200) * 0.334);
        } else if (unit <= 600) {
            total = (200 * 0.218) + (100 * 0.334) + ((unit - 300) * 0.516);
        } else {
            total = (200 * 0.218) + (100 * 0.334) + (300 * 0.516) + ((unit - 600) * 0.546);
        }

        return total;
    }
}