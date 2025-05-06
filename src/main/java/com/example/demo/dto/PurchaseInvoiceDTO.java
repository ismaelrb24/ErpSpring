package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PurchaseInvoiceDTO {

    @JsonProperty("name") 
    private String name;

    @JsonProperty("supplier") 
    private String supplier;

    @JsonProperty("posting_date")
    private String postingDate;
    private Double paidamount;
    @JsonProperty("outstanding_amount")
    private Double outsidepayement;
    @JsonProperty("grand_total")
    private Double grandTotal;

    @JsonProperty("status")
    private String status;

    // Getters et Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(String postingDate) {
        this.postingDate = postingDate;
    }
    public Double getOutsidepayement() {
        return outsidepayement;
    }

    public void setOutsidepayement(Double outsidepayement) {
        this.outsidepayement = outsidepayement;
    }
    public Double getPaidamount() {
        this.paidamount=this.grandTotal-this.outsidepayement;
        return paidamount;
    }

    public void setPaidamount(Double paidamount) {
        this.paidamount = paidamount;
    }
    public Double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(Double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public static List<PurchaseInvoiceDTO> getBystatus(List<PurchaseInvoiceDTO> invoices,String status) {
        List<PurchaseInvoiceDTO> listes=new ArrayList<>();
       if(status == null || status.isEmpty()){
            return invoices;
       }
       else{
            for (PurchaseInvoiceDTO invoice : invoices) {
                if (invoice.getStatus().equals(status)) {
                    listes.add(invoice);
                }
            }
       }
        return listes; 
    }
    public static PurchaseInvoiceDTO getByname(List<PurchaseInvoiceDTO> invoices,String name) {
        for (PurchaseInvoiceDTO invoice : invoices) {
            if (invoice.getName().equals(name)) {
                return invoice;
            }
        }
        return null;
    }
}
