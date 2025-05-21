package com.example.webhookapp;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class WebhookSqlApp implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(WebhookSqlApp.class, args);
    }

    @Override
    public void run(String... args) {
        RestTemplate restTemplate = new RestTemplate();

        String requestUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "Nishant Waghela");
        requestBody.put("regNo", "1292240251");
        requestBody.put("email", "nishant.waghela@mitwpu.edu.in");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(requestUrl, entity, Map.class);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            String webhookUrl = (String) response.getBody().get("webhook");
            String token = (String) response.getBody().get("accessToken");

            // Final SQL Query for Question 1 (odd regNo)
            String finalQuery = "SELECT d.name AS DepartmentName, COUNT(e.id) AS EmployeeCount " +
                    "FROM Departments d JOIN Employees e ON d.id = e.department_id " +
                    "GROUP BY d.name HAVING COUNT(e.id) >= 2 ORDER BY d.name;";

            HttpHeaders postHeaders = new HttpHeaders();
            postHeaders.setContentType(MediaType.APPLICATION_JSON);
            postHeaders.setBearerAuth(token);

            Map<String, String> postBody = new HashMap<>();
            postBody.put("finalQuery", finalQuery);
            HttpEntity<Map<String, String>> postEntity = new HttpEntity<>(postBody, postHeaders);

            restTemplate.postForEntity(webhookUrl, postEntity, String.class);
        }
    }
}
