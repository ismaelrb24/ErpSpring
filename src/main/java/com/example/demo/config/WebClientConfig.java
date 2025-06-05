package com.example.demo.config;

   import org.springframework.beans.factory.annotation.Value;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   import org.springframework.web.reactive.function.client.WebClient;

   @Configuration
   public class WebClientConfig {

       @Value("${api.method}")
       private String baseApiUrl;

       @Bean
       public WebClient.Builder webClientBuilder() {
           return WebClient.builder()
                   .baseUrl(baseApiUrl)
                   .defaultHeader("Content-Type", "application/json");
       }
   }