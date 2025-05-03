package com.example.demo.dto;
import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonProperty;
public class SupplierQuotationDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("supplier")
    private String supplier;

    @JsonProperty("transaction_date")
    private LocalDate transactionDate;

    @JsonProperty("conversion_rate")
    private double conversionRate;

    @JsonProperty("total_qty")
    private double totalQty;

    @JsonProperty("grand_total")
    private double grandTotal;


    // Getters and setters
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

    public LocalDate getTransactionDate() {
        return transactionDate;
    }
    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Double getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(Double conversionRate) {
        this.conversionRate = conversionRate;
    }

    public Double getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(Double totalQty) {
        this.totalQty = totalQty;
    }

    public Double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(Double grandTotal) {
        this.grandTotal = grandTotal;
    }
    public static List<SupplierQuotationDTO> getquotationbysupplier(String name, List<SupplierQuotationDTO> getall) {
        List<SupplierQuotationDTO> listes = new ArrayList<>();
        for (SupplierQuotationDTO q : getall) {
            if (q.getSupplier() != null && q.getSupplier().trim().equalsIgnoreCase(name.trim())) {
                listes.add(q);
            }
        }
        return listes;
    }
    
}
