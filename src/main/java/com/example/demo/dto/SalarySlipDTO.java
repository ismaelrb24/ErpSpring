package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SalarySlipDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("employee")
    private String employee;
    
    @JsonProperty("posting_date")
    private LocalDate postingDate;

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("end_date")
    private LocalDate endDate;

    @JsonProperty("gross_pay")
    private Double grossPay;

    @JsonProperty("total_deduction")
    private Double totalDeduction;

    
    @JsonProperty("net_pay")
    private Double netPay;
    
    @JsonProperty("absent_days")
    private Double absentday;
    
    @JsonProperty("payment_days")
    private Double payementday;

    @JsonProperty("total_earnings")
    private Double totalEarnings;
    
    private List<SalaryDetailDTO> correspond;
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { 
        this.name = name; 
    }

    public String getEmployee() {
        return employee;
    }
    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public LocalDate getPostingDate() { 
        return postingDate; 
    }
    public void setPostingDate(LocalDate postingDate) { 
        this.postingDate = postingDate; 
    }
    public LocalDate getStartDate() { 
        return startDate; 
    }
    public void setStartDate(LocalDate startDate) { 
        this.startDate = startDate; 
    }
    public LocalDate getEndDate() { 
        return endDate; 
    }
    public void setEndDate(LocalDate endDate) { 
        this.endDate = endDate; 
    }
    public Double getGrossPay() { 
        return grossPay; 
    }
    public void setGrossPay(Double grossPay) { 
        this.grossPay = grossPay; 
    }
    public Double getTotalDeduction() { 
        return totalDeduction; 
    }
    public void setTotalDeduction(Double totalDeduction) { 
        this.totalDeduction = totalDeduction; 
    }
    public Double getNetPay() {
         return netPay; 
    }
    public void setNetPay(Double netPay) { 
        this.netPay = netPay; 
    }
    public Double getAbsentday() {
        return absentday;
    }
    public void setAbsentday(Double absentday) {
        this.absentday = absentday;
    }
    public Double getPayementday() {
        return payementday;
    }
    public void setPayementday(Double payementday) {
        this.payementday = payementday;
    }
    public Double getTotalEarnings() {
        return totalEarnings;
    }
    public void setTotalEarnings(Double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }
    public List<SalaryDetailDTO> getCorrespond() {
        return correspond;
    }
    public void setCorrespond(List<SalaryDetailDTO> alllist) {
        this.correspond=SalaryDetailDTO.getbyslip(alllist, this.name);
    }
    public static List<SalarySlipDTO> getbyemployee(List<SalarySlipDTO> alllist,String employee){
        List<SalarySlipDTO> list=new ArrayList<>();
        for(int i=0;i<alllist.size();i++){
            if(alllist.get(i).getEmployee().equals(employee)){
                list.add(alllist.get(i));
            }
        }
        return list;
    } 
    public static SalarySlipDTO getbyID(List<SalarySlipDTO> alllist, String salaryid){
        SalarySlipDTO salary=new SalarySlipDTO();
        for(int i=0;i<alllist.size();i++){
            if(alllist.get(i).getName().equals(salaryid)){
                salary=alllist.get(i);
                return salary;
            }
        }
        return salary;
    }
}