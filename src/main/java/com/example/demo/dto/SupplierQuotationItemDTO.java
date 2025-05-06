package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class SupplierQuotationItemDTO {

    @JsonProperty("name")
    private String name;

    @JsonProperty("parent")
    private String parent;

    @JsonProperty("item_code")
    private String itemCode;
    
    @JsonProperty("request_for_quotation")
    private String request;

    @JsonProperty("item_name")
    private String itemName;

    @JsonProperty("qty")
    private Double qty;

    @JsonProperty("rate")
    private Double rate;

    @JsonProperty("amount")
    private Double amount;

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

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }
    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public static List<SupplierQuotationItemDTO> getquotationitembysupplier(String name, List<SupplierQuotationDTO> quotations,List<SupplierQuotationItemDTO> items) {
        List<SupplierQuotationItemDTO> listes = new ArrayList<>();
        List<SupplierQuotationDTO> listquotation = SupplierQuotationDTO.getquotationbysupplier(name, quotations);

        for (SupplierQuotationDTO quotation : listquotation) {
            for (SupplierQuotationItemDTO item : items) {
                if (quotation.getName().equals(item.getParent())) {
                    listes.add(item);
                }
            }
        }

        return listes;
    }
    public static List<SupplierQuotationItemDTO> getquotationitembyrequest(String name,List<SupplierQuotationItemDTO> items) {
        List<SupplierQuotationItemDTO> listes = new ArrayList<>();
            for (SupplierQuotationItemDTO item : items) {
                if (item.getRequest().equals(name)) {
                    listes.add(item);
                }
        }

        return listes;
    }
    public static SupplierQuotationItemDTO getByName(String name, List<SupplierQuotationItemDTO> items) {
        for (SupplierQuotationItemDTO item : items) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null; 
    }
    
}
