package com.example.individiualassignment_wanhaziq;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ElectricityBill.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "bills";
    public static final String COL_ID = "id";
    public static final String COL_MONTH = "month";
    public static final String COL_UNIT = "unit";
    public static final String COL_TOTAL_CHARGES = "total_charges";
    public static final String COL_REBATE = "rebate";
    public static final String COL_FINAL_COST = "final_cost";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MONTH + " TEXT, " +
                COL_UNIT + " INTEGER, " +
                COL_TOTAL_CHARGES + " REAL, " +
                COL_REBATE + " INTEGER, " +
                COL_FINAL_COST + " REAL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertBill(String month, int unit, double totalCharges, int rebate, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_MONTH, month);
        values.put(COL_UNIT, unit);
        values.put(COL_TOTAL_CHARGES, totalCharges);
        values.put(COL_REBATE, rebate);
        values.put(COL_FINAL_COST, finalCost);

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    public ArrayList<Bill> getAllBills() {
        ArrayList<Bill> billList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_ID + " DESC",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                String month = cursor.getString(cursor.getColumnIndexOrThrow(COL_MONTH));
                int unit = cursor.getInt(cursor.getColumnIndexOrThrow(COL_UNIT));
                double totalCharges = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_TOTAL_CHARGES));
                int rebate = cursor.getInt(cursor.getColumnIndexOrThrow(COL_REBATE));
                double finalCost = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_FINAL_COST));

                Bill bill = new Bill(id, month, unit, totalCharges, rebate, finalCost);
                billList.add(bill);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return billList;
    }

    public Bill getBillById(int billId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_ID + " = ?",
                new String[]{String.valueOf(billId)}
        );

        Bill bill = null;

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
            String month = cursor.getString(cursor.getColumnIndexOrThrow(COL_MONTH));
            int unit = cursor.getInt(cursor.getColumnIndexOrThrow(COL_UNIT));
            double totalCharges = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_TOTAL_CHARGES));
            int rebate = cursor.getInt(cursor.getColumnIndexOrThrow(COL_REBATE));
            double finalCost = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_FINAL_COST));

            bill = new Bill(id, month, unit, totalCharges, rebate, finalCost);
        }

        cursor.close();
        return bill;
    }

    public boolean updateBill(int id, String month, int unit, double totalCharges, int rebate, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_MONTH, month);
        values.put(COL_UNIT, unit);
        values.put(COL_TOTAL_CHARGES, totalCharges);
        values.put(COL_REBATE, rebate);
        values.put(COL_FINAL_COST, finalCost);

        int result = db.update(
                TABLE_NAME,
                values,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }

    public boolean deleteBill(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TABLE_NAME,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }
}