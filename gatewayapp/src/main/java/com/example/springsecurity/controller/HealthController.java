//package com.example.springsecurity.controller;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.persistence.PersistenceException;
//import jakarta.persistence.Query;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
//
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.sql.SQLException;
//
//@RestController
//public class HealthController {
//
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @GetMapping("/health/readinesstest")
//    public ResponseEntity<String> readiness() {
//        try {
//
//            ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8098/actuator/health", String.class);
//            if (!response.getStatusCode().is2xxSuccessful()) {
//                return ResponseEntity.status(503).body("Readiness check failed: Actuator health check failed");
//            }
//            Query query = entityManager.createNativeQuery("SELECT id, username, email FROM users LIMIT 1");
//            query.getSingleResult();
//
//            return ResponseEntity.ok("Readiness check passed");
//        } catch (PersistenceException e) {
//            return ResponseEntity.status(503).body("Readiness check failed: Database connection failed");
//        } catch (Exception e) {
//            return ResponseEntity.status(503).body("Readiness check failed");
//        }
//    }
//
//    @GetMapping("/health/liveness")
//    public ResponseEntity<String> liveness() {
//        return ResponseEntity.ok("Liveness check passed");
//    }
//
//}