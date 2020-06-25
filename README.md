# Ip-role-mapper

The IP-Role mapper is a REST based web-service which provides methods for mapping roles to IP address ranges and an IP address to a number of associated roles.
It will work both with IPv4 and IPv6 addresses, even mixed, however depending on the contents of the configuration file.

Internal documentation (at the Royal Danish Library) can be found here: https://sbprojects.statsbiblioteket.dk/display/INFRA/Ip-role-mapper

## Requirements
The project requires Java 11 be build and run. Known to build with OpenJDK 11, other JDKs may work

## Building
Use maven to build the project i.e. `mvn clean package`

## Test 
To run a local instance of the service do:

`mvn jetty:run-war`
The test instance will then be available on http://localhost:8080/ip-rolemapper/?_wadl

Configuration used for the test instance is located in `conf/ipRangesAndRoles.xml`

