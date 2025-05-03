package com.example.demo.service;

import com.example.demo.dto.AllPurchaseDataDTO;
import com.example.demo.model.ApiResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PurchaseDataService {

    private final RestTemplate restTemplate;
    private final String apiUrl = "http://erpnext.localhost:8000/api/method/erpnext.controllers.api.data_api.get_all_data";
    private final String updateRateApiUrl = "http://erpnext.localhost:8000/api/method/erpnext.controllers.api.data_api.update_supplier_quotation_item_rate";
    public PurchaseDataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public AllPurchaseDataDTO getAllPurchaseData(HttpSession session) {
        String token = (String) session.getAttribute("accessToken");
        String sid = (String) session.getAttribute("sid");

        if (token == null || sid == null) {
            throw new RuntimeException("Token ou SID manquant dans la session.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.add("Cookie", "sid=" + sid);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<ApiResponse<AllPurchaseDataDTO>> response = restTemplate.exchange(
            apiUrl,
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<ApiResponse<AllPurchaseDataDTO>>() {}
        );

        ApiResponse<AllPurchaseDataDTO> apiResponse = response.getBody();

        if (apiResponse != null && apiResponse.getMessage() != null) {
            return apiResponse.getMessage();
        } else {
            throw new RuntimeException("Erreur lors de la récupération des données.");
        }
    }
    public ApiResponse updateSupplierQuotationItemRate(HttpSession session, String itemName, double newRate) {
        String token = (String) session.getAttribute("accessToken");
        String sid = (String) session.getAttribute("sid");

        if (token == null || sid == null) {
            throw new RuntimeException("Token ou SID manquant dans la session.");
        }

        // Préparer les données de la requête
        String requestBody = String.format("{\"item_id\":\"%s\",\"rate\":%f}", itemName, newRate);

        // Configuration des entêtes HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Création de l'entité avec les données et entêtes
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // Envoi de la requête POST à l'API
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
                updateRateApiUrl,
                HttpMethod.POST,
                entity,
                ApiResponse.class
        );

        // Traitement de la réponse de l'API
        ApiResponse apiResponse = response.getBody();
        if (apiResponse != null && apiResponse.getMessage() != null) {
            return apiResponse; // Retourne la réponse de l'API si tout est OK
        } else {
            throw new RuntimeException("Erreur lors de la mise à jour du taux.");
        }
    }
    public ApiResponse payPurchaseInvoice(HttpSession session, String invoiceName) {
        String token = (String) session.getAttribute("accessToken");
        String sid = (String) session.getAttribute("sid");
    
        if (token == null || sid == null) {
            throw new RuntimeException("Token ou SID manquant dans la session.");
        }
    
        // URL de l'endpoint avec le paramètre GET
        String url = "http://erpnext.localhost:8000/api/method/erpnext.controllers.api.data_api.pay_purchase_invoice?invoice_name=" + invoiceName;
    
        // Configuration des entêtes HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);
    
        // Création de l'entité sans body car l'appel se fait par GET avec paramètre dans l’URL
        HttpEntity<String> entity = new HttpEntity<>(headers);
    
        // Envoi de la requête
        ResponseEntity<ApiResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                ApiResponse.class
        );
    
        ApiResponse apiResponse = response.getBody();
        if (apiResponse != null ) {
            return apiResponse;
        } else {
            throw new RuntimeException("Échec du paiement de la facture : " + (apiResponse != null ? apiResponse.getMessage() : "Erreur inconnue"));
        }
    }
    
}
