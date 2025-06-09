package com.example.demo.model;

import com.example.demo.dto.*;


import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.*;
public class Statistic {

    private String mounth;
    private double totalGrossPay;
    private double totalDeductions;
    private double totalNetPay;
    public String getMounth() {
        return mounth;
    }
    public void setMounth(String mounth) {
        this.mounth = mounth;
    }
    public double getTotalGrossPay() {
        return totalGrossPay;
    }
    public void setTotalGrossPay(double totalGrossPay) {
        this.totalGrossPay = totalGrossPay;
    }
    public double getTotalDeductions() {
        return totalDeductions;
    }
    public void setTotalDeductions(double totalDeductions) {
        this.totalDeductions = totalDeductions;
    }
    public double getTotalNetPay() {
        return totalNetPay;
    }
    public void setTotalNetPay(double totalNetPay) {
        this.totalNetPay = totalNetPay;
    }
    
}
