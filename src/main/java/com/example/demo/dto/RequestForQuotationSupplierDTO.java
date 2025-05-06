package com.example.demo.dto;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestForQuotationSupplierDTO {

    @JsonProperty("name")
    private String name;

    @JsonProperty("parent")
    private String parent;

    @JsonProperty("supplier")
    private String supplier;

    @JsonProperty("contact")
    private String contact;

    @JsonProperty("quote_status")
    private String quoteStatus;

    @JsonProperty("supplier_name")
    private String supplierName;

    @JsonProperty("email_id")
    private String emailId;

    @JsonProperty("send_email")
    private Integer sendEmail;

    @JsonProperty("email_sent")
    private Integer emailSent;

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getQuoteStatus() {
        return quoteStatus;
    }

    public void setQuoteStatus(String quoteStatus) {
        this.quoteStatus = quoteStatus;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public Integer getSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(Integer sendEmail) {
        this.sendEmail = sendEmail;
    }

    public Integer getEmailSent() {
        return emailSent;
    }

    public void setEmailSent(Integer emailSent) {
        this.emailSent = emailSent;
    }
    public static List<RequestForQuotationSupplierDTO> getquotationbysupplier(String name, List<RequestForQuotationSupplierDTO> getall) {
        List<RequestForQuotationSupplierDTO> listes = new ArrayList<>();
        for (RequestForQuotationSupplierDTO q : getall) {
            if (q.getSupplier() != null && q.getSupplier().trim().equalsIgnoreCase(name.trim())) {
                listes.add(q);
            }
        }
        return listes;
    }
}
