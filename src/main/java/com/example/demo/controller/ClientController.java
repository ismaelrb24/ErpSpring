// package com.example.demo.controller;

// import com.example.demo.service.ClientApiService;
// import com.example.demo.model.Client;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import java.util.List;
// import jakarta.servlet.http.HttpSession;


// @Controller
// public class ClientController {

//     private final ClientApiService clientApiService;

//     @Autowired
//     public ClientController(ClientApiService clientApiService) {
//         this.clientApiService = clientApiService;
//     }

//     // Méthode pour afficher tous les clients dans la page
//     @GetMapping("/clients")
//     public String getClients(HttpSession session,Model model) {
//         final List<Client> clients = clientApiService.listAllClients(session);
//         model.addAttribute("clients", clients); 
//         return "clients";  
//     }
// }
