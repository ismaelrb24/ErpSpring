package com.example.demo.controller;

import com.example.demo.model.Login;
import com.example.demo.model.AuthResponse;
import com.example.demo.service.AuthService;
import com.example.demo.exception.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/")
    public String showLoginForm(Model model) {
        model.addAttribute("login", new Login());
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @ModelAttribute("login") Login loginRequest,
            RedirectAttributes redirectAttributes,
            HttpSession session
    ) {
        try {
            AuthResponse authResponse = authService.login(loginRequest, session);

            // Stocker le token et le sid dans la session
            session.setAttribute("accessToken", authResponse.getAccessToken());
            session.setAttribute("sid", authResponse.getSid());

            return "redirect:/api/auth/accueil";

        } catch (AuthenticationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/api/auth/";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur d'authentification.");
            return "redirect:/api/auth/";
        }
    }

    @GetMapping("/accueil")
    public String dashboard(HttpSession session, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("accessToken");

        if (token == null) {
            redirectAttributes.addFlashAttribute("error", "Vous devez être connecté pour accéder au tableau de bord.");
            return "redirect:/api/auth/";
        }
        return "acceuil";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            authService.logout(session);
            redirectAttributes.addFlashAttribute("success", "Déconnexion réussie.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la déconnexion: " + e.getMessage());
        }
        return "redirect:/api/auth/";
    }
}
