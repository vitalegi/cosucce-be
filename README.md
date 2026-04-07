# README

## Prerequisites

- JDK 17
- Maven

```
$env:M2_HOME = 'C:\a\software\apache-maven-3.9.4'
$env:JAVA_HOME = 'C:\a\software\jdk-17'
$env:PATH = $env:M2_HOME + '\bin;' + $env:JAVA_HOME + '\bin;' + $env:PATH
```

## Build

```bash
mvn clean package
```

## Run

Setup the environment configuring:

```bash
# mandatory values

## DB

$env:DB_USERNAME=username
$env:DB_PASSWORD=pwd

## OIDC
$env:OIDC_ISSUER=*issuer uri*
$env:OIDC_JWKS_URI=*jwks uri*
$env:OIDC_AUTHORIZATION_URL=*authorization url*
$env:OIDC_CLIENT_ID=*client id*

# optional values
$env:SERVER_PORT=8080
```

Then run the application

### From maven, for local development

```bash
./mvnw compile exec:java
```

### In production

```bash
java -jar ./target/cosucce-be-${project.version}.jar
```
