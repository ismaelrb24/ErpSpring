package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class AllHRDataDTO {
    @JsonProperty("employees")
    private List<EmployeeDTO> employees;
    
    @JsonProperty("gender")
    private List<GenderDTO> genders;
    
    
    @JsonProperty("salary_slips")
    private List<SalarySlipDTO> salarys;
    
    @JsonProperty("details")
    private List<SalaryDetailDTO> salarydetail;
    
    @JsonProperty("statistic")
    private List<StatisticDTO> statistics;
    
    // Getters and Setters
    public List<EmployeeDTO> getEmployees() { return employees; }
    public void setEmployees(List<EmployeeDTO> employees) { this.employees = employees; }
    public List<GenderDTO> getGenders() {
        return genders;
    }
    public void setGenders(List<GenderDTO> genders) {
        this.genders = genders;
    }
    public List<SalarySlipDTO> getSalarys() {
        return salarys;
    }
    public void setSalarys(List<SalarySlipDTO> salarys) {
        this.salarys = salarys;
    }
    public List<SalaryDetailDTO> getSalarydetail() {
        return salarydetail;
    }
    public void setSalarydetail(List<SalaryDetailDTO> salarydetail) {
        this.salarydetail = salarydetail;
    }
     public List<StatisticDTO> getStatistics() {
        return statistics;
    }
    public void setStatistics(List<StatisticDTO> statistics) {
        this.statistics = statistics;
    }
    
}
