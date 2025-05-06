package com.example.demo.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AllPurchaseDataDTO {

    private List<SupplierDTO> suppliers;

    @JsonProperty("supplier_quotations")
    private List<SupplierQuotationDTO> supplierQuotations;

    @JsonProperty("supplier_quotation_items")
    private List<SupplierQuotationItemDTO> supplierQuotationItems;

    @JsonProperty("purchase_orders")
    private List<PurchaseOrderDTO> purchaseOrders;

    @JsonProperty("purchase_order_items")
    private List<PurchaseOrderItemDTO> purchaseOrderItems;

    @JsonProperty("purchase_invoices")
    private List<PurchaseInvoiceDTO> purchaseInvoices;

    @JsonProperty("purchase_invoice_items")
    private List<PurchaseInvoiceItemDTO> purchaseInvoiceItems;

    @JsonProperty("request_for_quotations")
    private List<RequestForQuotationDTO> requestForQuotations;

    @JsonProperty("request_for_quotation_suppliers")
    private List<RequestForQuotationSupplierDTO> requestForQuotationSuppliers;

    // Getters and setters

    public List<SupplierDTO> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<SupplierDTO> suppliers) {
        this.suppliers = suppliers;
    }

    public List<SupplierQuotationDTO> getSupplierQuotations() {
        return supplierQuotations;
    }

    public void setSupplierQuotations(List<SupplierQuotationDTO> supplierQuotations) {
        this.supplierQuotations = supplierQuotations;
    }

    public List<SupplierQuotationItemDTO> getSupplierQuotationItems() {
        return supplierQuotationItems;
    }

    public void setSupplierQuotationItems(List<SupplierQuotationItemDTO> supplierQuotationItems) {
        this.supplierQuotationItems = supplierQuotationItems;
    }

    public List<PurchaseOrderDTO> getPurchaseOrders() {
        return purchaseOrders;
    }

    public void setPurchaseOrders(List<PurchaseOrderDTO> purchaseOrders) {
        this.purchaseOrders = purchaseOrders;
    }

    public List<PurchaseOrderItemDTO> getPurchaseOrderItems() {
        return purchaseOrderItems;
    }

    public void setPurchaseOrderItems(List<PurchaseOrderItemDTO> purchaseOrderItems) {
        this.purchaseOrderItems = purchaseOrderItems;
    }

    public List<PurchaseInvoiceDTO> getPurchaseInvoices() {
        return purchaseInvoices;
    }

    public void setPurchaseInvoices(List<PurchaseInvoiceDTO> purchaseInvoices) {
        this.purchaseInvoices = purchaseInvoices;
    }

    public List<PurchaseInvoiceItemDTO> getPurchaseInvoiceItems() {
        return purchaseInvoiceItems;
    }

    public void setPurchaseInvoiceItems(List<PurchaseInvoiceItemDTO> purchaseInvoiceItems) {
        this.purchaseInvoiceItems = purchaseInvoiceItems;
    }

    public List<RequestForQuotationDTO> getRequestForQuotations() {
        return requestForQuotations;
    }

    public void setRequestForQuotations(List<RequestForQuotationDTO> requestForQuotations) {
        this.requestForQuotations = requestForQuotations;
    }

    public List<RequestForQuotationSupplierDTO> getRequestForQuotationSuppliers() {
        return requestForQuotationSuppliers;
    }

    public void setRequestForQuotationSuppliers(List<RequestForQuotationSupplierDTO> requestForQuotationSuppliers) {
        this.requestForQuotationSuppliers = requestForQuotationSuppliers;
    }
}
