# VOIS Warehouse
A shop in London has 2 million IoT tracking devices in the Warehouse Inventory for sale, of which half need configuration to meet UK industry standards.
A configured device will have a status ACTIVE and an ideal temperature between (0’C to 10’C).
When a device is not configured, the default status is READY and temperature value is - 1’C.
Every device has a unique secret seven-digit pin code used for unlocking the device.
A given device needs to be sent to a Device Configuration Service (DCS) to set the device status ACTIVE and random temperature value between (0 to 10).
The Device Configuration Service does not need a device pin code for the configuration operation.
The shop can sell a device only if it meets the UK government's industry standard.


# About The Service
The service is just a simple REST service. It uses a relational database PostgreSQL to store the warehouse data. Also it uses in-memory database H2 for testing database.

The service contains a list of REST endpoints defined in `com.vois.warehouse.controller`, the default port for the service is **8080**.


## Requirements

For building and running the application you need:

- [Docker](https://docs.docker.com/get-docker/)
- [JDK 11](https://www.oracle.com/java/technologies/downloads/#java11)
- [Maven 3](https://maven.apache.org)


## Running the application locally

First you will need to run the PostgreSQL container by the following command
```shell
docker-compose -f docker/docker-compose.yml up
```

Second, there are several ways to run a Spring Boot application on your local machine. 

One way is to execute the `main` method in the `com.vois.warehouse.WarehouseApplication` class from your IDE.

Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
mvn clean install
mvn spring-boot:run
```

The web application is accessible via **localhost:8080** by default

