package com.example.demo.model;

import java.util.List;

public class ChartData {
    private List<String> months;
    private List<Double> grossPay;
    private List<Double> deductions;
    private List<Double> netPay;

    public ChartData(List<String> months, List<Double> grossPay, List<Double> deductions, List<Double> netPay) {
        this.months = months;
        this.grossPay = grossPay;
        this.deductions = deductions;
        this.netPay = netPay;
    }

    // Getters et setters
    public List<String> getMonths() {
        return months;
    }

    public void setMonths(List<String> months) {
        this.months = months;
    }

    public List<Double> getGrossPay() {
        return grossPay;
    }

    public void setGrossPay(List<Double> grossPay) {
        this.grossPay = grossPay;
    }

    public List<Double> getDeductions() {
        return deductions;
    }

    public void setDeductions(List<Double> deductions) {
        this.deductions = deductions;
    }

    public List<Double> getNetPay() {
        return netPay;
    }

    public void setNetPay(List<Double> netPay) {
        this.netPay = netPay;
    }
}