package com.example.demo.service;

import com.example.demo.model.AuthResponse;
import com.example.demo.model.Login;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.exception.AuthenticationException;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service
public class AuthService {

    private static final String LOGIN_URL = "http://erpnext.localhost:8000/api/method/erpnext.controllers.api.auth.auth_api_controller.login";
    private static final String LOGOUT_URL = "http://erpnext.localhost:8000/api/method/erpnext.controllers.api.auth.auth_api_controller.logout";

    public AuthResponse login(Login loginRequest, HttpSession session) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Login> entity = new HttpEntity<>(loginRequest, headers);
    RestTemplate restTemplate = new RestTemplate();

    try {
        ResponseEntity<String> response = restTemplate.exchange(
            LOGIN_URL, HttpMethod.POST, entity, String.class
        );

        // ✅ Récupération du cookie "sid" dans les headers
        String sid = null;
        List<String> cookies = response.getHeaders().get("Set-Cookie");
        if (cookies != null) {
            for (String cookie : cookies) {
                if (cookie.startsWith("sid=")) {
                    sid = cookie.split(";")[0].split("=")[1];
                    break;
                }
            }
        }

        // ✅ Parser le body JSON pour récupérer l'access_token
        String body = response.getBody();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(body);
        String accessToken = root.path("access_token").asText();

        if (accessToken != null && sid != null) {
            session.setAttribute("accessToken", accessToken);
            session.setAttribute("sid", sid);
            System.out.println("Token : " + accessToken);
            System.out.println("SID : " + sid);
        }

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(accessToken);
        authResponse.setSid(sid);
        return authResponse;

    } catch (HttpClientErrorException.Unauthorized e) {
        throw new AuthenticationException("Utilisateur non valide");
    } catch (Exception e) {
        throw new RuntimeException("Erreur pendant l'authentification : " + e.getMessage());
    }
}


public void logout(HttpSession session) {
    String token = (String) session.getAttribute("accessToken");
    String sid = (String) session.getAttribute("sid");

    if (token == null || sid == null) {
        throw new RuntimeException("Token ou SID manquant dans la session.");
    }

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);
    headers.add("Cookie", "sid=" + sid); 

    HttpEntity<String> entity = new HttpEntity<>(headers);
    RestTemplate restTemplate = new RestTemplate();

    try {
        String logoutUrl = "http://erpnext.localhost:8000/api/method/erpnext.controllers.api.auth.auth_api_controller.logout";
        ResponseEntity<String> response = restTemplate.exchange(logoutUrl, HttpMethod.POST, entity, String.class);

        System.out.println("Déconnexion réussie : " + response.getBody());
        session.invalidate();

    } catch (HttpClientErrorException.Unauthorized e) {
        throw new RuntimeException("Token invalide ou expiré (401 Unauthorized).");
    } catch (Exception e) {
        throw new RuntimeException("Erreur pendant la déconnexion : " + e.getMessage());
    }
}

}
