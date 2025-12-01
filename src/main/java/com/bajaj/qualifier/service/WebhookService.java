package com.bajaj.qualifier.service;

import com.bajaj.qualifier.model.GenerateWebhookRequest;
import com.bajaj.qualifier.model.GenerateWebhookResponse;
import com.bajaj.qualifier.model.SubmitSolutionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WebhookService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookService.class);

    private final RestTemplate restTemplate = new RestTemplate();

    // YOUR DETAILS
    private final String name = "Tamatam Jagadeeswara Reddy";
    private final String regNo = "22BCE1881";
    private final String email = "Jagadeeswara.reddy2022@vitstudent.ac.in";

    public void executeFlow() {
        try {
            LOGGER.info("Calling generateWebhook API...");

            GenerateWebhookResponse response = callGenerateWebhook();

            if (response == null) {
                LOGGER.error("generateWebhook returned null.");
                return;
            }

            String webhookUrl = response.getWebhook();
            String accessToken = response.getAccessToken();

            LOGGER.info("Webhook URL: {}", webhookUrl);
            LOGGER.info("Access Token: {}", accessToken);

            // Send SQL based on regNo
            String sqlQuery = solveSqlQuestion();

            LOGGER.info("Submitting SQL Query...");
            submitFinalQuery(accessToken, sqlQuery);

            LOGGER.info("Done.");
        } catch (Exception e) {
            LOGGER.error("Error occurred:", e);
        }
    }

    private GenerateWebhookResponse callGenerateWebhook() {
        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        GenerateWebhookRequest request = new GenerateWebhookRequest(name, regNo, email);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GenerateWebhookRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<GenerateWebhookResponse> response =
                restTemplate.exchange(url, HttpMethod.POST, entity, GenerateWebhookResponse.class);

        return response.getBody();
    }

    private String solveSqlQuestion() {
        int lastDigit = regNo.charAt(regNo.length() - 1) - '0';

        if (lastDigit % 2 == 1) {
            LOGGER.info("RegNo is ODD → Submitting SQL for QUESTION 1");
            return getSQLForQuestion1();
        } else {
            LOGGER.info("RegNo is EVEN → Submitting SQL for QUESTION 2");
            return getSQLForQuestion2();
        }
    }

    // ------------------------ QUESTION 1 SQL ------------------------
    private String getSQLForQuestion1() {
        return "SELECT d.department_name AS DEPARTMENT_NAME, "
                + "t.total_salary AS SALARY, "
                + "CONCAT(e.first_name, ' ', e.last_name) AS EMPLOYEE_NAME, "
                + "TIMESTAMPDIFF(YEAR, e.dob, CURDATE()) AS AGE "
                + "FROM ( "
                + "    SELECT e.emp_id, e.department, SUM(p.amount) AS total_salary, "
                + "           ROW_NUMBER() OVER (PARTITION BY e.department ORDER BY SUM(p.amount) DESC) AS rn "
                + "    FROM employee e "
                + "    JOIN payments p ON e.emp_id = p.emp_id "
                + "    WHERE DAY(p.payment_time) != 1 "
                + "    GROUP BY e.emp_id, e.department "
                + ") t "
                + "JOIN employee e ON e.emp_id = t.emp_id "
                + "JOIN department d ON d.department_id = e.department "
                + "WHERE t.rn = 1;";
    }

    // ------------------------ QUESTION 2 SQL ------------------------
    private String getSQLForQuestion2() {
        return "SELECT d.DEPARTMENT_NAME, "
                + "AVG(FLOOR(DATEDIFF(CURDATE(), e.DOB) / 365)) AS AVERAGE_AGE, "
                + "SUBSTRING(GROUP_CONCAT(CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) "
                + "ORDER BY e.EMP_ID SEPARATOR ', '), 1, 1000) AS EMPLOYEE_LIST "
                + "FROM DEPARTMENT d "
                + "JOIN EMPLOYEE e ON d.DEPARTMENT_ID = e.DEPARTMENT "
                + "JOIN PAYMENTS p ON e.EMP_ID = p.EMP_ID "
                + "WHERE p.AMOUNT > 70000 "
                + "GROUP BY d.DEPARTMENT_ID, d.DEPARTMENT_NAME "
                + "ORDER BY d.DEPARTMENT_ID DESC;";
    }

    private void submitFinalQuery(String token, String finalSql) {
        String url = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);

        SubmitSolutionRequest req = new SubmitSolutionRequest(finalSql);
        HttpEntity<SubmitSolutionRequest> entity = new HttpEntity<>(req, headers);

        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }
}
