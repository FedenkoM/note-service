# Getting Started

### Task Requirements
- Java 17
- Maven 3.8+
- Mongodb


### How to run:
- you can run it as simple spring boot app, but you will need mongobd installed in you local machine(you can use docker-compose file for it, just exclude note application service)
- you can run it with docker: 
  1) Go to root app folder(note-service)
  2) open terminal and build app `mvn clean install`
  3) run docker compose `docker compose up -d`
  4) wait until all containers is up and running(startup for mongo can take up to 1 min.)
  5) open swagger `http:localhost:8085/swagger-ui.html` 
