package com.example.individiualassignment_wanhaziq;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    ListView listViewBills;
    DatabaseHelper databaseHelper;

    ArrayList<Bill> billList;
    ArrayList<String> displayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listViewBills = findViewById(R.id.listViewBills);
        databaseHelper = new DatabaseHelper(this);

        loadBills();

        listViewBills.setOnItemClickListener((parent, view, position, id) -> {
            Bill selectedBill = billList.get(position);

            Intent intent = new Intent(HistoryActivity.this, DetailActivity.class);
            intent.putExtra("bill_id", selectedBill.getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBills();
    }

    private void loadBills() {
        billList = databaseHelper.getAllBills();
        displayList = new ArrayList<>();

        for (Bill bill : billList) {
            String item = bill.getMonth() + " - RM " + String.format("%.2f", bill.getFinalCost());
            displayList.add(item);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                displayList
        );

        listViewBills.setAdapter(adapter);

        if (displayList.isEmpty()) {
            Toast.makeText(this, "No saved records yet.", Toast.LENGTH_SHORT).show();
        }
    }
}
