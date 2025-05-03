// package com.example.demo.service;

// import com.example.demo.model.ApiResponse;
// import com.example.demo.model.Client;
// import org.springframework.core.ParameterizedTypeReference;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.HttpClientErrorException;
// import org.springframework.web.client.RestTemplate;
// import java.util.List;
// import jakarta.servlet.http.HttpSession;
// import org.springframework.http.*;

// @Service
// public class ClientApiService {

//     private final RestTemplate restTemplate;
//     private final String baseUrl = "http://erpnext.localhost:8000/api/method/erpnext.perso.api.client";

//     public ClientApiService(RestTemplate restTemplate) {
//         this.restTemplate = restTemplate;
//     }
//     public List<Client> listAllClients(HttpSession session) {
//         String url = baseUrl + ".get_clients";
        
//          String token = (String) session.getAttribute("accessToken");
//         String sid = (String) session.getAttribute("sid");

//         if (token == null || sid == null) {
//             throw new RuntimeException("Token ou SID manquant dans la session.");
//         }

//         HttpHeaders headers = new HttpHeaders();
//         headers.set("Authorization", "Bearer " + token);
//         headers.add("Cookie", "sid=" + sid); 

//         HttpEntity<String> entity = new HttpEntity<>(headers);

//         ResponseEntity<ApiResponse<List<Client>>> response = restTemplate.exchange(
//                 url,
//                 org.springframework.http.HttpMethod.GET,
//                 entity,
//                 new ParameterizedTypeReference<ApiResponse<List<Client>>>() {}
//             );

//             ApiResponse<List<Client>> apiResponse = response.getBody();

//             if (apiResponse != null && "success".equalsIgnoreCase(apiResponse.getStatus())) {
//                 return apiResponse.getMessage();
//             } else {
//                 throw new RuntimeException("Failed to retrieve clients: " +
//                     (apiResponse != null ? apiResponse.getStatus() : "Unknown error"));
//             }
//         }
// }
