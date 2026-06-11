package com.example.individiualassignment_wanhaziq;

public class Bill {
    private int id;
    private String month;
    private int unit;
    private double totalCharges;
    private int rebate;
    private double finalCost;

    public Bill(int id, String month, int unit, double totalCharges, int rebate, double finalCost) {
        this.id = id;
        this.month = month;
        this.unit = unit;
        this.totalCharges = totalCharges;
        this.rebate = rebate;
        this.finalCost = finalCost;
    }

    public int getId() {
        return id;
    }

    public String getMonth() {
        return month;
    }

    public int getUnit() {
        return unit;
    }

    public double getTotalCharges() {
        return totalCharges;
    }

    public int getRebate() {
        return rebate;
    }

    public double getFinalCost() {
        return finalCost;
    }
}