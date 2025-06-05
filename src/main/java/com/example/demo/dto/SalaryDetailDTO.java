package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SalaryDetailDTO {
    @JsonProperty("salary_component")
    private String salaryComponent;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("parent")
    private String parent;

    // Getters and Setters
    public String getSalaryComponent() { 
        return salaryComponent; 
    }
    public void setSalaryComponent(String salaryComponent) { 
        this.salaryComponent = salaryComponent; 
    }
    public Double getAmount() { 
        return amount; 
    }
    public void setAmount(Double amount) { 
        this.amount = amount; 
    }
    public String getParent() {
        return parent;
    }
    public void setParent(String parent) {
        this.parent = parent;
    }
    public static List<SalaryDetailDTO> getbyslip(List<SalaryDetailDTO> alllist,String salaryslipname){
        List<SalaryDetailDTO> list=new ArrayList<>();
        for(int i=0;i<alllist.size();i++){
            if(alllist.get(i).getParent().equals(salaryslipname)){
                list.add(alllist.get(i));
            }
        }
        return list;
    } 
}