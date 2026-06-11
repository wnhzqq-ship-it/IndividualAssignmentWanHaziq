package com.example.individiualassignment_wanhaziq;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    TextView textDetailMonth, textDetailUnit, textDetailTotal, textDetailRebate, textDetailFinal;
    Button buttonEdit, buttonDelete;

    DatabaseHelper databaseHelper;
    int billId;
    Bill currentBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        databaseHelper = new DatabaseHelper(this);

        textDetailMonth = findViewById(R.id.textDetailMonth);
        textDetailUnit = findViewById(R.id.textDetailUnit);
        textDetailTotal = findViewById(R.id.textDetailTotal);
        textDetailRebate = findViewById(R.id.textDetailRebate);
        textDetailFinal = findViewById(R.id.textDetailFinal);

        buttonEdit = findViewById(R.id.buttonEdit);
        buttonDelete = findViewById(R.id.buttonDelete);

        billId = getIntent().getIntExtra("bill_id", -1);

        if (billId == -1) {
            Toast.makeText(this, "Record not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadBillDetails();

        buttonEdit.setOnClickListener(v -> showEditDialog());

        buttonDelete.setOnClickListener(v -> confirmDelete());
    }

    private void loadBillDetails() {
        currentBill = databaseHelper.getBillById(billId);

        if (currentBill == null) {
            Toast.makeText(this, "Record not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        textDetailMonth.setText("Month: " + currentBill.getMonth());
        textDetailUnit.setText("Unit Used: " + currentBill.getUnit() + " kWh");
        textDetailTotal.setText(String.format("Total Charges: RM %.2f", currentBill.getTotalCharges()));
        textDetailRebate.setText("Rebate: " + currentBill.getRebate() + "%");
        textDetailFinal.setText(String.format("Final Cost: RM %.2f", currentBill.getFinalCost()));
    }

    private void showEditDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        Spinner spinnerMonth = new Spinner(this);
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                months
        );
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        for (int i = 0; i < months.length; i++) {
            if (months[i].equals(currentBill.getMonth())) {
                spinnerMonth.setSelection(i);
                break;
            }
        }

        EditText editUnit = new EditText(this);
        editUnit.setHint("Enter unit from 1 to 1000");
        editUnit.setInputType(InputType.TYPE_CLASS_NUMBER);
        editUnit.setText(String.valueOf(currentBill.getUnit()));

        TextView textRebate = new TextView(this);
        textRebate.setText("Selected Rebate: " + currentBill.getRebate() + "%");

        SeekBar seekBarRebate = new SeekBar(this);
        seekBarRebate.setMax(5);
        seekBarRebate.setProgress(currentBill.getRebate());

        final int[] selectedRebate = {currentBill.getRebate()};

        seekBarRebate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedRebate[0] = progress;
                textRebate.setText("Selected Rebate: " + selectedRebate[0] + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        layout.addView(spinnerMonth);
        layout.addView(editUnit);
        layout.addView(textRebate);
        layout.addView(seekBarRebate);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit Bill Record")
                .setMessage("Update the month, unit, and rebate.")
                .setView(layout)
                .setPositiveButton("Update", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button updateButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

            updateButton.setOnClickListener(v -> {
                String newMonth = spinnerMonth.getSelectedItem().toString();
                String unitText = editUnit.getText().toString().trim();

                if (unitText.isEmpty()) {
                    Toast.makeText(this, "Please enter electricity unit used.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int newUnit = Integer.parseInt(unitText);

                if (newUnit < 1) {
                    Toast.makeText(this, "Unit must be at least 1 kWh.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newUnit > 1000) {
                    Toast.makeText(this, "Unit cannot exceed 1000 kWh.", Toast.LENGTH_SHORT).show();
                    return;
                }

                double newTotalCharges = calculateTotalCharges(newUnit);
                double newFinalCost = newTotalCharges - (newTotalCharges * selectedRebate[0] / 100);

                boolean isUpdated = databaseHelper.updateBill(
                        billId,
                        newMonth,
                        newUnit,
                        newTotalCharges,
                        selectedRebate[0],
                        newFinalCost
                );

                if (isUpdated) {
                    Toast.makeText(this, "Record updated successfully.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadBillDetails();
                } else {
                    Toast.makeText(this, "Failed to update record.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Record")
                .setMessage("Are you sure you want to delete this bill record?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean isDeleted = databaseHelper.deleteBill(billId);

                    if (isDeleted) {
                        Toast.makeText(this, "Record deleted successfully.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to delete record.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
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