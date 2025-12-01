# Bajaj Finserv Health – JAVA Qualifier  
A Spring Boot application that automatically interacts with Bajaj APIs to:

1. Generate a webhook on startup  
2. Solve the assigned SQL question  
3. Submit the final SQL query using JWT authentication  

This project contains working implementations for **Question 1 and Question 2**.  
The app detects the regNo or can be forced to submit a specific question.

---

## 🚀 Features

### ✔ Automatic execution on startup (no controllers needed)  
### ✔ Sends POST request to generate webhook  
### ✔ Receives:
- `webhook` URL  
- `accessToken` (JWT token)

### ✔ Based on regNo OR forced selection:
- Submits SQL for **Question 1** (odd regNo)  
- OR Submits SQL for **Question 2** (even regNo)  

### ✔ Final SQL is submitted to:  
