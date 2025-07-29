# ENCAPSULEARN
A full-stack online quiz system using Angular, Spring Boot, and MySQL.

---

## About The Project

## online-quiz-web-app

A full-stack web application for managing and participating in online quizzes. Built with Spring Boot (backend), Angular (frontend), and MySQL (database).
Features include user and admin roles, JWT authentication, secure quiz creation, participation, and result tracking.


### Features

* **User Authentication:** Secure user registration and login using JWT (JSON Web Tokens).
* **Role-Based Access:**
    * **Admin:** Can create, update, delete, and view quizzes and categories. Manages user roles.
    * **User:** Can view and attempt quizzes. Tracks their own quiz history and scores.
* **Quiz Management:** Admins can create quizzes with multiple-choice questions, set timers, and assign them to categories.
* **Quiz Participation:** Users can take quizzes within the allotted time and receive immediate feedback on their performance.
* **Result Tracking:** Scores and quiz attempts are saved and can be reviewed by the user.

---

## Built With

This project is built with the following technologies:

* **Backend:**
    * [Spring Boot](https://spring.io/projects/spring-boot)
    * [Spring Security](https://spring.io/projects/spring-security)
    * [JPA / Hibernate](https://hibernate.org/)
    * [Maven](https://maven.apache.org/)
* **Frontend:**
    * [Angular](https://angular.io/)
* **Database:**
    * [MySQL](https://www.mysql.com/)

---

## Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

Make sure you have the following software installed on your machine:
* **Java JDK:** Version 21
* **Maven:** Version 3.2
* **Node.js:** Version 20.19.4 (with npm)
* **Angular CLI:** Version 18.2.20
    ```sh
    npm install -g @angular/cli
    ```
* **MySQL Server:** Version 8.0 or higher

### Installation & Setup

1.  **Clone the repository:**
    ```sh
    git clone [https://github.com/mostofa-rezvi/quiz.git](https://github.com/mostofa-rezvi/quiz.git)
    cd quiz
    ```

2.  **Configure Backend (Spring Boot):**
    * Navigate to the `backend` directory.
    * Open `src/main/resources/application.properties`.
    * Update the `spring.datasource.url`, `spring.datasource.username`, and `spring.datasource.password` properties to match your local MySQL setup.
    * Create the database schema in MySQL:
        ```sql
        CREATE DATABASE quiz_db;
        ```
    * Build the project using Maven:
        ```sh
        mvn clean install
        ```
    * Run the Spring Boot application:
        ```sh
        mvn spring-boot:run
        ```
    The backend server will start on `http://localhost:8080`.

3.  **Configure Frontend (Angular):**
    * Navigate to the `frontend` directory in a new terminal.
    * Install the necessary npm packages:
        ```sh
        npm install
        ```
    * Run the Angular development server:
        ```sh
        ng serve
        ```
    The frontend will be available at `http://localhost:4200`.

---

## Usage

* Open your browser and navigate to `http://localhost:4200`.
* **Admin Login:**
    * Username: `admin`
    * Password: `admin123`
* **User Login:** Register a new user account or use a pre-existing one.

---
