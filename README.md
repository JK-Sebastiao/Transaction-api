# Transaction-API

Transaction-API project is a CRUD Spring Boot based REST API. the project is technical task for recruitment process for Java Backend Engineer role at Vyne Technologies Ltd.

## Used tools:
* Java 11
* Spring Boot 2.6.1
* Lombok
* JUnit 5
* Maven
* InMemory Database H2

## Important Dependencies

This project depends on
* Spring Web (spring-boot-starter-web)
* Spring Data JPA (spring-boot-starter-data-jpa)
* Spring Test (spring-boot-starter-test)
* String Actuator (spring-boot-starter-actuator)
* Spring Security (spring-boot-starter-security)
* Spring Doc (springdoc-openapi-ui)
* InMemory Database H2 

> For more please take a look on pom.xml file!

## Building Project

To build this project, you need to run the shell commands below:

```shell script
git clone https://github.com/JK-Sebastiao/Transaction-api.git
cd Transaction-api
mvn clean install
```

## Running Project

After Building you can run the project using maven with the shell command below:
```shell script
mvn exec:java -Dexec.mainClass=com.payvyne.transaction.api.TransactionApiApplication
```

The API will be accessible on **http://localhost:8081/api/v1**
In order to check the API health you can access **http://localhost:8081/api/v1/actuator/health** on your browser or run the shell command below:
```shell script
curl -X GET   http://localhost:8081/api/v1/actuator/health
```

>**Note:** The root path of the application is **/api/v1**

### Login

There is already registered a user with admin role. In order to login into api please send a POST request to `http://localhost:8081/api/v1/auth/login` with request body below:
```
{
    "username": "alan.turing@payvyne.com",
    "password": "admin-password"
}
```

In case of successful login you will get a response with access token in response header in **attribute authorization**  and with response body as:
```
{
    "id": 1,
    "username": "alan.turing@payvyne.com",
    "fullName": "Alan Turing",
    "enabled": true
}
```
> Note id attribute can be different



## API Documentation
In order to access the API Documentation, first you need to run the application and then access on your browser bellow endpoints:
* `http://localhost:8081/api/v1/swagger-ui` for Web page view
* `http://localhost:8081/api/v1/rest-api-docs` for JSON view
