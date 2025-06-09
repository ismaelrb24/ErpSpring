package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatisticDTO {

    @JsonProperty("month")
    private String month;

    @JsonProperty("total_gross_pay")
    private Double totalGrossPay;

    @JsonProperty("total_deductions")
    private Double totalDeductions;

    @JsonProperty("total_net_pay")
    private Double totalNetPay;

    // Constructeurs, getters et setters
    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Double getTotalGrossPay() {
        return totalGrossPay;
    }

    public void setTotalGrossPay(Double totalGrossPay) {
        this.totalGrossPay = totalGrossPay;
    }

    public Double getTotalDeductions() {
        return totalDeductions;
    }

    public void setTotalDeductions(Double totalDeductions) {
        this.totalDeductions = totalDeductions;
    }

    public Double getTotalNetPay() {
        return totalNetPay;
    }

    public void setTotalNetPay(Double totalNetPay) {
        this.totalNetPay = totalNetPay;
    }
    public static List<StatisticDTO> getByYear(List<StatisticDTO> statistics, String year) {
        List<StatisticDTO> filteredStatistics = new ArrayList<>();
        if (statistics == null || year == null || year.isEmpty()) {
            return filteredStatistics;
        }

        for (StatisticDTO stat : statistics) {
            if (stat != null && stat.getMonth() != null && stat.getMonth().startsWith(year)) {
                filteredStatistics.add(stat);
            }
        }

        return filteredStatistics;
    }
    public static StatisticDTO getbymonth(List<StatisticDTO> alllist,String month){
        StatisticDTO stat=new StatisticDTO();
        for(StatisticDTO statist : alllist){
            if(statist.getMonth().equals(month)){
                stat=statist;
                break;
            }
        }
        return stat;
    }
}