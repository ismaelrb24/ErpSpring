package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class SupplierDTO {

    @JsonProperty("name")
    private String name;

    @JsonProperty("supplier_name")
    private String supplierName;

    @JsonProperty("supplier_type")
    private String supplierType;

    @JsonProperty("creation")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS") // format exact du JSON
    private LocalDateTime creation;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierType() {
        return supplierType;
    }

    public void setSupplierType(String supplierType) {
        this.supplierType = supplierType;
    }

    public LocalDateTime getCreation() {
        return creation;
    }

    public void setCreation(LocalDateTime creation) {
        this.creation = creation;
    }
}
