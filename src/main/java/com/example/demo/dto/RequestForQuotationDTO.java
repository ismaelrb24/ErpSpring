package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class RequestForQuotationDTO {

    @JsonProperty("name")
    private String name;

    @JsonProperty("transaction_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate transactionDate;

    @JsonProperty("schedule_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduleDate;

    @JsonProperty("status")
    private String status;

    @JsonProperty("company")
    private String company;

    @JsonProperty("vendor")
    private String vendor;

    @JsonProperty("incoterm")
    private String incoterm;

    @JsonProperty("named_place")
    private String namedPlace;

    @JsonProperty("docstatus")
    private int docstatus;

    @JsonProperty("creation")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime creation;

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public LocalDate getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getIncoterm() {
        return incoterm;
    }

    public void setIncoterm(String incoterm) {
        this.incoterm = incoterm;
    }

    public String getNamedPlace() {
        return namedPlace;
    }

    public void setNamedPlace(String namedPlace) {
        this.namedPlace = namedPlace;
    }

    public int getDocstatus() {
        return docstatus;
    }

    public void setDocstatus(int docstatus) {
        this.docstatus = docstatus;
    }

    public LocalDateTime getCreation() {
        return creation;
    }

    public void setCreation(LocalDateTime creation) {
        this.creation = creation;
    }
    public static List<RequestForQuotationDTO> getrequestsupplier(String name, List<RequestForQuotationSupplierDTO> getallS,List<RequestForQuotationDTO> getall) {
        List<RequestForQuotationDTO> listes = new ArrayList<>();
        List<RequestForQuotationSupplierDTO> listsupplier=RequestForQuotationSupplierDTO.getquotationbysupplier(name, getallS);
        for (RequestForQuotationDTO rfq : getall) {
            for (RequestForQuotationSupplierDTO rfqs : listsupplier) {
                if (rfq.getName().equals(rfqs.getParent())) {
                    listes.add(rfq);
                }
            }
        }
        return listes;
    }
    public static RequestForQuotationDTO getByName(String name, List<RequestForQuotationDTO> listrfq) {
        for (RequestForQuotationDTO rfq : listrfq) {
            if (rfq.getName().equals(name)) {
                return rfq;
            }
        }
        return null; 
    }
}
