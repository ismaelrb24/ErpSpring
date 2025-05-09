package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.PurchaseDataService;
import com.example.demo.model.ApiResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ApiController {

    @GetMapping("/fournisseurs")
    public String showFournisseursPage(Model model,HttpSession session,RestTemplate restTemplate) {
        String token = (String) session.getAttribute("accessToken");

        if (token == null) {
            return "redirect:/api/auth/";
        }
        else{    
            PurchaseDataService purchaseDataService = new PurchaseDataService(restTemplate);
            List<SupplierDTO> fournisseurs = purchaseDataService.getAllPurchaseData(session).getSuppliers();
            model.addAttribute("fournisseurs", fournisseurs);
            return "fournisseurs";
        }
    }

    @GetMapping("/accounting")
    public String showAccountingPage(Model model,HttpSession session,RestTemplate restTemplate) {
        String token = (String) session.getAttribute("accessToken");

        if (token == null) {
            return "redirect:/api/auth/";
        }
        else{    
            PurchaseDataService purchaseDataService = new PurchaseDataService(restTemplate);
            List<PurchaseInvoiceDTO> invoices = purchaseDataService.getAllPurchaseData(session).getPurchaseInvoices();
            model.addAttribute("invoices", invoices);
            return "accounting"; 
        }
    }
    @GetMapping("/accountingsearch")
    public String showAccountingPage(Model model,HttpSession session,RestTemplate restTemplate,@RequestParam(name = "status", required = false) String status) {
        String token = (String) session.getAttribute("accessToken");

        if (token == null) {
            return "redirect:/api/auth/";
        }
        else{    
            PurchaseDataService purchaseDataService = new PurchaseDataService(restTemplate);
            List<PurchaseInvoiceDTO> invoices = purchaseDataService.getAllPurchaseData(session).getPurchaseInvoices();
            List<PurchaseInvoiceDTO> invoiceconcerned=PurchaseInvoiceDTO.getBystatus(invoices, status);
            model.addAttribute("invoices", invoiceconcerned);
            return "accounting"; 
        }
    }

    @GetMapping("/quotation")
    public String afficherDetailFournisseur(@RequestParam("name") String name, Model model, HttpSession session, RestTemplate restTemplate) {
        String token = (String) session.getAttribute("accessToken");
        if (token == null) {
            return "redirect:/api/auth/";
        }

        PurchaseDataService purchaseDataService = new PurchaseDataService(restTemplate);
        List<RequestForQuotationDTO> listrfq = purchaseDataService.getAllPurchaseData(session).getRequestForQuotations();
        List<RequestForQuotationSupplierDTO> listrfqs = purchaseDataService.getAllPurchaseData(session).getRequestForQuotationSuppliers();
        if (listrfq == null || listrfqs == null) {
            model.addAttribute("error", "Aucune donnée disponible.");
            return "request_quotation"; 
        }

        List<RequestForQuotationDTO> listeconcerned = RequestForQuotationDTO.getrequestsupplier(name, listrfqs, listrfq);
        model.addAttribute("requestquotation", listeconcerned);
        model.addAttribute("name", name);
        return "request_quotation";
    }
    @PostMapping("/generate")
    public String update(@RequestParam("itemName") String itemName,@RequestParam("rfqname") String rfqname, 
                         @RequestParam("rate") double rate, Model model, HttpSession session, RestTemplate restTemplate) {
        String token = (String) session.getAttribute("accessToken");
        String sid = (String) session.getAttribute("sid");
    
        if (token == null || sid == null) {
            return "redirect:/api/auth/";
        }
        PurchaseDataService purchaseDataService = new PurchaseDataService(restTemplate);
        ApiResponse apiResponse = purchaseDataService.generateSupplierQuotation(session, itemName,rfqname,rate);
    
           return "redirect:/api/auth/accueil";
    }
    
    @GetMapping("/detailquotation")
    public String afficherdetailquotation(@RequestParam("name") String name, Model model, HttpSession session, RestTemplate restTemplate) {
        String token = (String) session.getAttribute("accessToken");
        if (token == null) {
            return "redirect:/api/auth/";
        }

        PurchaseDataService purchaseDataService = new PurchaseDataService(restTemplate);
        List<SupplierQuotationDTO> quotation = purchaseDataService.getAllPurchaseData(session).getSupplierQuotations();
        List<SupplierQuotationItemDTO> quotationitems = purchaseDataService.getAllPurchaseData(session).getSupplierQuotationItems();
        if (quotation == null || quotationitems == null) {
            model.addAttribute("error", "Aucune donnée disponible.");
            return "quotation_items"; 
        }

        List<SupplierQuotationItemDTO> listeconcerned = SupplierQuotationItemDTO.getquotationitembyrequest(name,quotationitems);
        model.addAttribute("quotationitems", listeconcerned);
        model.addAttribute("name", name);
        return "quotation_items";
    }
    @GetMapping("/update-quotation-item-rate")
    public String showUpdateForm(@RequestParam("itemName") String itemName, Model model, HttpSession session, RestTemplate restTemplate) {
        PurchaseDataService purchaseDataService = new PurchaseDataService(restTemplate);
        List<SupplierQuotationItemDTO> quotationitems = purchaseDataService.getAllPurchaseData(session).getSupplierQuotationItems();
        String token = (String) session.getAttribute("accessToken");
        if (token == null) {
            return "redirect:/api/auth/";
        }
        SupplierQuotationItemDTO item = SupplierQuotationItemDTO.getByName(itemName, quotationitems);
        model.addAttribute("item", item);
        return "update_quotation_item_rate";
    }

    @PostMapping("/update-quotation-item-rate")
    public String updateQuotationItemRate(
            @RequestParam String itemName, 
            @RequestParam double newRate, 
            HttpSession session, Model model) {
        
        String token = (String) session.getAttribute("accessToken");
        if (token == null) {
            return "redirect:/api/auth/";
        }

        try {
            PurchaseDataService purchaseDataService = new PurchaseDataService(new RestTemplate());
            ApiResponse apiResponse = purchaseDataService.updateSupplierQuotationItemRate(session, itemName, newRate);

            model.addAttribute("message", apiResponse.getMessage());  // Affiche le message de succès
            return "redirect:/api/auth/accueil";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Erreur lors de la mise à jour du taux : " + e.getMessage());
            return "quotation_items";
        }
    }
    @GetMapping("/order")
    public String afficherlistecommande(@RequestParam("name") String name, Model model, HttpSession session, RestTemplate restTemplate) {
        String token = (String) session.getAttribute("accessToken");
        if (token == null) {
            return "redirect:/api/auth/";
        }

        PurchaseDataService purchaseDataService = new PurchaseDataService(restTemplate);
        List<PurchaseOrderDTO> orders = purchaseDataService.getAllPurchaseData(session).getPurchaseOrders();
        List<PurchaseOrderDTO> ordersconcerned= PurchaseOrderDTO.getBySupplier(name, orders);
        if (ordersconcerned == null || orders == null) {
            model.addAttribute("error", "Aucune donnée disponible.");
            return "orders_liste"; 
        }
        model.addAttribute("ordersconcerned", ordersconcerned);
        model.addAttribute("name",name);
        return "orders_liste";
    }
    @GetMapping("/ordersearch")
    public String listOrders(@RequestParam(name = "etat", required = false) String etat,@RequestParam(name = "name") String name, Model model,HttpSession session, RestTemplate restTemplate) {
   
        String token = (String) session.getAttribute("accessToken");
        if (token == null) {
            return "redirect:/api/auth/";
        }

        PurchaseDataService purchaseDataService = new PurchaseDataService(restTemplate);
        List<PurchaseOrderDTO> orders = purchaseDataService.getAllPurchaseData(session).getPurchaseOrders();
        List<PurchaseOrderDTO> ordersconcerned= PurchaseOrderDTO.getBystatus(name, orders,etat);
        if (ordersconcerned == null || orders == null) {
            model.addAttribute("error", "Aucune donnée disponible.");
            return "orders_liste"; 
        }
        model.addAttribute("ordersconcerned", ordersconcerned);
        model.addAttribute("name",name);
        return "orders_liste";
    }
    @PostMapping("/pay")
    public String paidinvoice(@RequestParam("invoiceId") String invoiceId, Model model, HttpSession session, RestTemplate restTemplate) {
        String token = (String) session.getAttribute("accessToken");
        if (token == null) {
            return "redirect:/api/auth/";
        }

        PurchaseDataService purchaseDataService = new PurchaseDataService(restTemplate);
        purchaseDataService.payPurchaseInvoice(session, invoiceId);
        return "redirect:/accounting";
    }
    @GetMapping("/paypart")
    public String showformpayement(@RequestParam("invoiceId") String invoiceId, Model model, HttpSession session, RestTemplate restTemplate){
        PurchaseDataService purchaseDataService = new PurchaseDataService(restTemplate);
        List<PurchaseInvoiceDTO> invoices = purchaseDataService.getAllPurchaseData(session).getPurchaseInvoices();
        PurchaseInvoiceDTO invoice=PurchaseInvoiceDTO.getByname(invoices, invoiceId);
        model.addAttribute("invoice", invoice);
        return "payement_part";
    }
    @PostMapping("/payvalue")
    public String payerPartiellement(@RequestParam("invoiceName") String invoiceName,@RequestParam("newRate") double montantAPayer,HttpSession session,
            RestTemplate restTemplate,Model model) {
        
        String token = (String) session.getAttribute("accessToken");
        if (token == null) {
            return "redirect:/api/auth/";
        }

        try {
            PurchaseDataService purchaseDataService = new PurchaseDataService(restTemplate);
            purchaseDataService.payerPartiellementFacture(session, invoiceName, montantAPayer);
            return "redirect:/accounting";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du paiement partiel : " + e.getMessage());
            return "payement_part";
        }
    }

}
