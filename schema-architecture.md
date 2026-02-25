This Spring Boot application uses both MVC and REST controllers. Thymeleaf templates are used for the Admin and Doctor dashboards, while REST APIs serve all other modules. The application interacts with two databases—MySQL (for patient, doctor, appointment, and admin data) and MongoDB (for prescriptions). All controllers route requests through a common service layer, which in turn delegates to the appropriate repositories. MySQL uses JPA entities while MongoDB uses document models.

1. A user accesses dashboards or modules such as appointments or patient records.
2. The request is handled by either a Thymeleaf MVC controller or a REST controller.
3. The controller forwards the request to the service layer.
4. The service layer applies business logic and decides which database to use.
5. For relational data, MySQL repositories interact with JPA entities and the MySQL database.
6. For flexible data such as prescriptions, MongoDB repositories interact with document models.
7. The result is returned through the service layer to the controller and back to the user as HTML or JSON.
