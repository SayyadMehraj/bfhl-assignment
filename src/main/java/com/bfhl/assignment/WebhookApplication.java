package com.bfhl.assignment;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class WebhookApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebhookApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    CommandLineRunner run(RestTemplate restTemplate) {
        return args -> {
            try {
                // Step 1: Generate webhook
                String generateWebhookUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
                
                Map<String, String> requestBody = new HashMap<>();
                requestBody.put("name", "Sayyad Mehraj");
                requestBody.put("regNo", "22BCE9331");
                requestBody.put("email", "sayyadmehraj01@gmail.com");

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

                System.out.println("Sending POST request to generate webhook...");
                Map<String, String> response = restTemplate.postForObject(
                    generateWebhookUrl, 
                    requestEntity, 
                    Map.class
                );

                if (response != null) {
                    String webhookUrl = response.get("webhook");
                    String accessToken = response.get("accessToken");
                    
                    System.out.println("Webhook URL: " + webhookUrl);
                    System.out.println("Access Token received");

                    // Step 2: Prepare SQL solution
                    String sqlQuery = "SELECT " +
                        "d.DEPARTMENT_NAME, " +
                        "emp_salary.total_salary AS SALARY, " +
                        "CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS EMPLOYEE_NAME, " +
                        "TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE " +
                        "FROM ( " +
                        "    SELECT " +
                        "        p.EMP_ID, " +
                        "        e.DEPARTMENT, " +
                        "        SUM(p.AMOUNT) AS total_salary " +
                        "    FROM PAYMENTS p " +
                        "    JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
                        "    WHERE DAY(p.PAYMENT_TIME) != 1 " +
                        "    GROUP BY p.EMP_ID, e.DEPARTMENT " +
                        ") emp_salary " +
                        "JOIN EMPLOYEE e ON emp_salary.EMP_ID = e.EMP_ID " +
                        "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                        "WHERE emp_salary.total_salary = ( " +
                        "    SELECT MAX(inner_salary.total_salary) " +
                        "    FROM ( " +
                        "        SELECT " +
                        "            p2.EMP_ID, " +
                        "            e2.DEPARTMENT, " +
                        "            SUM(p2.AMOUNT) AS total_salary " +
                        "        FROM PAYMENTS p2 " +
                        "        JOIN EMPLOYEE e2 ON p2.EMP_ID = e2.EMP_ID " +
                        "        WHERE DAY(p2.PAYMENT_TIME) != 1 " +
                        "        GROUP BY p2.EMP_ID, e2.DEPARTMENT " +
                        "    ) inner_salary " +
                        "    WHERE inner_salary.DEPARTMENT = emp_salary.DEPARTMENT " +
                        ") " +
                        "ORDER BY d.DEPARTMENT_NAME";

                    // Step 3: Submit solution
                    String submitUrl = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";
                    
                    Map<String, String> solutionBody = new HashMap<>();
                    solutionBody.put("finalQuery", sqlQuery);

                    HttpHeaders submitHeaders = new HttpHeaders();
                    submitHeaders.setContentType(MediaType.APPLICATION_JSON);
                    submitHeaders.set("Authorization", accessToken);
                    HttpEntity<Map<String, String>> submitEntity = new HttpEntity<>(solutionBody, submitHeaders);

                    System.out.println("Submitting SQL solution...");
                    String submitResponse = restTemplate.postForObject(
                        submitUrl, 
                        submitEntity, 
                        String.class
                    );

                    System.out.println("Response: " + submitResponse);
                    System.out.println("✓ Solution submitted successfully!");
                } else {
                    System.err.println("Failed to generate webhook - no response received");
                }

            } catch (Exception e) {
                System.err.println("Error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}