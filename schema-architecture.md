# Architecture Design Document

## Section 1: Architecture summary

This is a full stack Java-based SW development project, designed in a 3 layer architecture: Presentation, Business Logic and Data Persistence.
This allows functional independence of the components, providing the system with modular scalability, maintenance and ease of upgrades.

The design pattern is MVC implemented with Spring MVC, for Viewing and Controlling routes in the Presentation layer. The functionality of the
Model has been divided into Business Logic (application server) and Data Persistence (data server), providing MVC with improved modularity.

The Presentation layer has been distributed in two types of user interfaces:
- Web-based graphical interface to serve medical and administrative staff, as internal application dashboards, developed with Thymeleaf as a 
    HTML, CSS and JavaScript rendering engine.
- Rest API clients for data exchange in JSON format, for scalability and maximum modularity.

The Business Logic Layer is developed with Spring Boot with Web and Security projects, for business rules, operations cordination, validation and application service securization.

The Data Persistence Layer allows storage, disposition and management of application data. Spring Data has been used which contains JDBC, Hibernate and JPA for the management of Databases, being one MySQL relational and the other NoSQL documentary MongoDB.

## Section 2: Numbered flow of data and control

1. Personnel users interact with GUI Dashboards or Non-personnel users utilize external client interface through RESTModules based on JSON API.
2. User REST Requests are managed by Rest controllers and redirected to the apropiate application service functions.
3. Service layer applies logical and computational operations, management of application workflow and requesting data from the data service as needed.
4. Data service starting with the Repository Layer performs data access operations with both MySQL and MongoDB with a simple operation interface.
5. Repositories access to the Database Systems to retrieve and perform data operations, with MySQL and MongoDB as needed.
6. Data retrieved is mapped into Java objects as Model-Entity using ORM functionality used by Hibernate and JPA.
7. Finally, the data is bound in the response layer through Web page following MVC workflow, or DTO data response into JSON for REST external clients.    