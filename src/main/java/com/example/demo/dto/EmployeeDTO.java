package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.web.client.RestTemplate;

import com.example.demo.model.Statistic;
import com.example.demo.service.*;

public class EmployeeDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("modified_by")
    private String modifiedBy;

    @JsonProperty("owner")
    private String owner;

    @JsonProperty("docstatus")
    private Integer docstatus;

    @JsonProperty("idx")
    private Integer idx;

    @JsonProperty("employee")
    private String employee;

    @JsonProperty("naming_series")
    private String namingSeries;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("middle_name")
    private String middleName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("employee_name")
    private String employeeName;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;

    @JsonProperty("salutation")
    private String salutation;

    @JsonProperty("date_of_joining")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfJoining;

    @JsonProperty("image")
    private String image;

    @JsonProperty("status")
    private String status;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("create_user_permission")
    private Integer createUserPermission;

    @JsonProperty("company")
    private String company;

    @JsonProperty("department")
    private String department;

    @JsonProperty("employee_number")
    private String employeeNumber;

    @JsonProperty("designation")
    private String designation;

    @JsonProperty("company_email")
    private String companyEmail;
    
    private List<SalarySlipDTO> salarySlip;

    // Fonction de recherche par critères
    public boolean searchByCriteria(String search, String gender, LocalDate startDate, LocalDate endDate) {
        boolean matches = true;

        // Recherche textuelle
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            boolean textMatch = false;
            if (employeeName != null && employeeName.toLowerCase().contains(searchLower)) textMatch = true;
            if (firstName != null && firstName.toLowerCase().contains(searchLower)) textMatch = true;
            if (lastName != null && lastName.toLowerCase().contains(searchLower)) textMatch = true;
            if (department != null && department.toLowerCase().contains(searchLower)) textMatch = true;
            if (designation != null && designation.toLowerCase().contains(searchLower)) textMatch = true;
            if (companyEmail != null && companyEmail.toLowerCase().contains(searchLower)) textMatch = true;
            matches = matches && textMatch;
        }

        // Filtre par genre
        if (gender != null && !gender.trim().isEmpty()) {
            matches = matches && (this.gender != null && this.gender.equalsIgnoreCase(gender));
        }

        // Filtre par période d'embauche
        if (startDate != null) {
            matches = matches && (dateOfJoining != null && !dateOfJoining.isBefore(startDate));
        }
        if (endDate != null) {
            matches = matches && (dateOfJoining != null && !dateOfJoining.isAfter(endDate));
        }

        return matches;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getModifiedBy() { return modifiedBy; }
    public void setModifiedBy(String modifiedBy) { this.modifiedBy = modifiedBy; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    public Integer getDocstatus() { return docstatus; }
    public void setDocstatus(Integer docstatus) { this.docstatus = docstatus; }

    public Integer getIdx() { return idx; }
    public void setIdx(Integer idx) { this.idx = idx; }

    public String getEmployee() { return employee; }
    public void setEmployee(String employee) { this.employee = employee; }

    public String getNamingSeries() { return namingSeries; }
    public void setNamingSeries(String namingSeries) { this.namingSeries = namingSeries; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getSalutation() { return salutation; }
    public void setSalutation(String salutation) { this.salutation = salutation; }

    public LocalDate getDateOfJoining() { return dateOfJoining; }
    public void setDateOfJoining(LocalDate dateOfJoining) { this.dateOfJoining = dateOfJoining; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Integer getCreateUserPermission() { return createUserPermission; }
    public void setCreateUserPermission(Integer createUserPermission) { this.createUserPermission = createUserPermission; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getEmployeeNumber() { return employeeNumber; }
    public void setEmployeeNumber(String employeeNumber) { this.employeeNumber = employeeNumber; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getCompanyEmail() { return companyEmail; }
    public void setCompanyEmail(String companyEmail) { this.companyEmail = companyEmail; }
    
    public List<SalarySlipDTO> getSalarySlip() {
        return salarySlip;
    }

    public void setSalarySlip(List<SalarySlipDTO> alllist) {
        this.salarySlip=SalarySlipDTO.getbyemployee(alllist, this.employee);
    }
    public static EmployeeDTO getbyID(List<EmployeeDTO> alllist, String employeeid){
        EmployeeDTO employee=new EmployeeDTO();
        for(int i=0;i<alllist.size();i++){
            if(alllist.get(i).getEmployee().equals(employeeid)){
                employee=alllist.get(i);
                return employee;
            }
        }
        return employee;
    }
    public static List<EmployeeDTO> filterEmployeesByMonth(String month, RestTemplate restTemplate, HttpSession session) {
    List<EmployeeDTO> employeeconcerned = new ArrayList<>();
    HrService rhservice = new HrService(restTemplate);
    List<EmployeeDTO> employlist = rhservice.getAllHr(session).getEmployees();
    List<SalarySlipDTO> salaryslip = rhservice.getAllHr(session).getSalarys();
    List<SalaryDetailDTO> salarydetails = rhservice.getAllHr(session).getSalarydetail();
    YearMonth yearMonth = YearMonth.parse(month);
    LocalDate startOfMonth = yearMonth.atDay(1);
    LocalDate endOfMonth = yearMonth.atEndOfMonth();
    if (month == null || month.isEmpty()) {
        return employlist;
    }
    for (int i = 0; i < employlist.size(); i++) {
        List<SalarySlipDTO> salaryconcerned = new ArrayList<>(); 
        employlist.get(i).setSalarySlip(salaryslip);
        for (int a = 0; a < employlist.get(i).getSalarySlip().size(); a++) {
            employlist.get(i).getSalarySlip().get(a).setCorrespond(salarydetails);
            if (!employlist.get(i).getSalarySlip().get(a).getStartDate().isBefore(startOfMonth) &&
                !employlist.get(i).getSalarySlip().get(a).getEndDate().isAfter(endOfMonth)) {
                salaryconcerned.add(employlist.get(i).getSalarySlip().get(a));        
            }
        }
        if (!salaryconcerned.isEmpty()) {
            employlist.get(i).setSalarySlip(salaryconcerned);
            employeeconcerned.add(employlist.get(i));
        }
    }
    return employeeconcerned;
}
    public static Double getotalNetPay(List<EmployeeDTO> alllist){
        Double totalnet=0.0;
        for(int i=0;i<alllist.size();i++){
            for(int a=0;a<alllist.get(i).getSalarySlip().size();a++){    
                SalarySlipDTO salary=alllist.get(i).getSalarySlip().get(a);
                totalnet=totalnet+salary.getNetPay();
            }
        }
        return totalnet;
    }
    public static Double getotalGrosspay(List<EmployeeDTO> alllist){
        Double totalgross=0.0;
        for(int i=0;i<alllist.size();i++){
            for(int a=0;a<alllist.get(i).getSalarySlip().size();a++){    
                SalarySlipDTO salary=alllist.get(i).getSalarySlip().get(a);
                totalgross=totalgross+salary.getGrossPay();
            }
        }
        return totalgross;
    }
    public static Double getotalDeduction(List<EmployeeDTO> alllist){
        Double totaldeduction=0.0;
        for(int i=0;i<alllist.size();i++){
            for(int a=0;a<alllist.get(i).getSalarySlip().size();a++){    
                SalarySlipDTO salary=alllist.get(i).getSalarySlip().get(a);
                totaldeduction=totaldeduction+salary.getTotalDeduction();
            }
        }
        return totaldeduction;
    }
    public  static List<EmployeeDTO> getByPeriod(List<EmployeeDTO> allList, String targetMonth) {
        List<EmployeeDTO> concerned = new ArrayList<>();
        if (allList == null || targetMonth == null) {
            return concerned; 
        }
        for (EmployeeDTO employee : allList) {
            if (employee != null && employee.getSalarySlip() != null) {
                for (SalarySlipDTO slip : employee.getSalarySlip()) {
                    if (slip != null && slip.getStartDate() != null) {
                        String slipMonth = slip.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM"));
                        if (targetMonth.equals(slipMonth)) {
                            List<SalarySlipDTO> salaryconcerned=new ArrayList<>();
                            salaryconcerned.add(slip);
                            employee.setSalarySlip(salaryconcerned);
                            concerned.add(employee);
                            break; 
                        }
                    }
                }
            }
        }

        return concerned;
    }
}