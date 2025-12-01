# BFHL Assignment - Webhook Application

**Name:** Sayyad Mehraj  
**Registration Number:** 22BCE9331  
**Email:** sayyadmehraj01@gmail.com

## Project Overview

This Spring Boot application completes the Bajaj Finserv Health Java qualifier assignment by:
1. Generating a webhook on startup
2. Solving the SQL problem (Question 1 - odd registration number)
3. Submitting the solution to the webhook URL with JWT authentication

## SQL Solution Explanation

**Problem:** Find the highest salaried employee per department, excluding payments made on the 1st day of the month.

**Approach:**
- Filter out all payments where `DAY(PAYMENT_TIME) = 1`
- Calculate total salary per employee (sum of remaining payments)
- Find the employee with maximum total salary in each department
- Calculate age using `TIMESTAMPDIFF`
- Join with department table to get department names

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/bfhl/assignment/
│   │       └── WebhookApplication.java
│   └── resources/
│       └── application.properties
└── pom.xml
```

## Prerequisites

- Java 17 or higher
- Maven 3.6+

## How to Build

```bash
# Clone the repository
git clone https://github.com/your-username/bfhl-assignment.git
cd bfhl-assignment

# Build the project
mvn clean package

# The JAR file will be created at:
# target/bfhl-assignment.jar
```

## How to Run

```bash
java -jar target/bfhl-assignment.jar
```

The application will:
1. Start up
2. Automatically send POST request to generate webhook
3. Receive the webhook URL and access token
4. Submit the SQL solution
5. Display the response

## Expected Output

```
Sending POST request to generate webhook...
Webhook URL: https://...
Access Token received
Submitting SQL solution...
Response: ...
✓ Solution submitted successfully!
```

## Technologies Used

- Spring Boot 3.2.0
- Java 17
- RestTemplate for HTTP requests
- Maven for build management

## SQL Query

The final SQL query solves for the highest paid employee per department (excluding 1st-of-month payments):

```sql
SELECT 
    d.DEPARTMENT_NAME, 
    emp_salary.total_salary AS SALARY, 
    CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS EMPLOYEE_NAME, 
    TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE 
FROM (
    SELECT 
        p.EMP_ID, 
        e.DEPARTMENT, 
        SUM(p.AMOUNT) AS total_salary 
    FROM PAYMENTS p 
    JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID 
    WHERE DAY(p.PAYMENT_TIME) != 1 
    GROUP BY p.EMP_ID, e.DEPARTMENT 
) emp_salary 
JOIN EMPLOYEE e ON emp_salary.EMP_ID = e.EMP_ID 
JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID 
WHERE emp_salary.total_salary = (
    SELECT MAX(inner_salary.total_salary) 
    FROM (
        SELECT 
            p2.EMP_ID, 
            e2.DEPARTMENT, 
            SUM(p2.AMOUNT) AS total_salary 
        FROM PAYMENTS p2 
        JOIN EMPLOYEE e2 ON p2.EMP_ID = e2.EMP_ID 
        WHERE DAY(p2.PAYMENT_TIME) != 1 
        GROUP BY p2.EMP_ID, e2.DEPARTMENT 
    ) inner_salary 
    WHERE inner_salary.DEPARTMENT = emp_salary.DEPARTMENT 
) 
ORDER BY d.DEPARTMENT_NAME
```

## Submission Checklist

- ✓ Spring Boot application using RestTemplate
- ✓ Runs on startup (CommandLineRunner)
- ✓ No controller/endpoint needed
- ✓ JWT token in Authorization header
- ✓ GitHub repository with code
- ✓ JAR file included
- ✓ README documentation

## License

This project is for assignment purposes only.