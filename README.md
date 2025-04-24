# Workify - Professional Job Seeking Platform

<p align="center">
  <img src="https://i.ibb.co/JFcLX7v5/image-removebg-preview.png" alt="Workify Logo" width="200" height="200">
</p>

<p align="center">
  <a href="#features">Features</a> •
  <a href="#tech-stack">Tech Stack</a> •
  <a href="#architecture">Architecture</a> •
  <a href="#getting-started">Getting Started</a> •
  <a href="#api-documentation">API Documentation</a> •
  <a href="#license">License</a>
</p>

## Overview

Workify is a robust job seeking platform built with Spring Boot that connects job seekers with employers. The platform streamlines the job application process, allowing candidates to showcase their skills and experience while enabling employers to find the perfect match for their positions. This is a personal project developed to demonstrate my backend development skills for internship opportunities.

## Features

### For Job Seekers
- Create and manage professional profiles
- Upload resumes and portfolios
- Search and filter job listings
- Apply to positions with one click
- Track application status
- Receive job recommendations based on skills and experience

### For Employers
- Post and manage job listings
- Search candidate database
- Review and manage applications
- Schedule interviews
- Communicate with candidates
- Analytics dashboard for recruitment metrics

## Tech Stack

### Backend
- **Java 17** - Core programming language
- **Spring Boot** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence
- **Hibernate** - ORM for database operations
- **CockroachDB** - Relational database
- **JWT** - Token-based authentication
- **Maven** - Dependency management and build tool

### Backend Concepts Implemented
- **RESTful API Design** - Following REST principles for API endpoints
- **Microservices Architecture** - Modular and scalable service design
- **DTO Pattern** - Data Transfer Objects for API communication
- **Repository Pattern** - Data access abstraction
- **Service Layer** - Business logic encapsulation
- **Global Exception Handling** - Centralized error management
- **Input Validation** - Request data validation
- **Pagination and Sorting** - Efficient data retrieval
- **Caching** - Performance optimization
- **Scheduled Tasks** - Automated background processes
- **Logging** - Comprehensive application monitoring

## Architecture

Workify follows a layered architecture:

1. **Controller Layer** - Handles HTTP requests and responses
2. **Service Layer** - Contains business logic
3. **Repository Layer** - Manages data access
4. **Entity Layer** - Represents database models
5. **DTO Layer** - Manages data transfer objects
6. **Security Layer** - Handles authentication and authorization

## Getting Started

### Prerequisites
- JDK 17 or higher
- Maven 3.6+
- MySQL 8.0+

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/Workify-SpringBoot.git
   cd Workify-SpringBoot
    ```

    2. Configure the database in application.properties :
   
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/workify
   spring.datasource.username=your_username
   spring.datasource.password=your_password
    ```
  
3. Build the project:
   
   ```bash
   mvn clean install
    ```
4. Run the application:
   
   ```bash
   mvn spring-boot:run
    ```
The application will be available at http://localhost:8080 .

## API Documentation
The complete API documentation is available on Postman: Workify API Documentation 
(https://www.postman.com/red-robot-38357/workspace/workify)

This documentation includes all endpoints, request/response examples, and authentication details.

## Database Schema
The application uses a relational database with the following core entities:

- Users (job seekers and employers)
- Jobs
- Applications
- Companies
- Skills
- Resumes
- Notifications
## Project Purpose
This project was developed as a personal portfolio piece to demonstrate my proficiency in Spring Boot and backend development concepts for internship applications. It showcases my ability to design and implement a complete backend system for a real-world application.

## License
This project is licensed under the MIT License.

### MIT License
MIT License

Copyright (c) 2023 [Your Name]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

## Acknowledgments
- Spring Boot community
- Open source libraries used in this project
