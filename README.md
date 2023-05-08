**<h1>RBA interview app</h1>**

Simple application for RBA interview.

### Prerequisites

* Intellij IDEA or Eclipse
* Docker, Docker Compose v3
* Java 17

## Starting the app

First start dependencies with Docker Compose. From the project's root run the following:

    $ docker compose --file docker/docker-compose.yml up -d

This will start:

* PostgreSQL

Then, build the service:

    $ mvn clean compile

Finally, we can start the app running the main
class: `Application` - [src/main/java/hr/rba/interview/RbaInterviewApplication.java](src/main/java/hr/rba/interview/RbaInterviewApplication.java)

After we are done we can clean up the resources with the following:

    $ docker compose --file docker/docker-compose.yml down


**OPEN API SPECIFICATION AVAILABLE LOCALLY AT:**

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)