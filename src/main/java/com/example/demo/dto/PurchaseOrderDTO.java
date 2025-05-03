package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PurchaseOrderDTO {
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("supplier")
    private String supplier;
    
    @JsonProperty("transaction_date")
    private String transactionDate;
    
    @JsonProperty("grand_total")
    private Double grandTotal;
    
    @JsonProperty("status")
    private String status; 

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

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
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
     public static List<PurchaseOrderDTO> getBySupplier(String name, List<PurchaseOrderDTO> orders) {
        List<PurchaseOrderDTO> listes=new ArrayList<>();
        for (PurchaseOrderDTO order : orders) {
            if (order.getSupplier().equals(name)) {
                listes.add(order);
            }
        }
        return listes; 
    }
    public static List<PurchaseOrderDTO> getBystatus(String name, List<PurchaseOrderDTO> orders,String status) {
        List<PurchaseOrderDTO> listes=new ArrayList<>();
       List<PurchaseOrderDTO> ordersupp=PurchaseOrderDTO.getBySupplier(name, orders);
       if(status == null || status.isEmpty()){
            return ordersupp;
       }
       else{
            for (PurchaseOrderDTO order : ordersupp) {
                if (order.getStatus().equals(status)) {
                    listes.add(order);
                }
            }
       }
        return listes; 
    }
}
