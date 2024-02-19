# QUOTA API

## Prerequisites

- Java 21
- Docker

## Running the Application

1. Clone the repository
2. Run ```docker-compose up``` inside docker folder
3. Create quota_db database
4. Run the application
5. Access the application at http://localhost:8080

## Making requests

Creating an user:

```bash

POST http://localhost:8080/users
Content-Type: application/json

{
  "firstName": "name",
  "lastName": "name"
}

```

Consuming quota:

```bash

POST http://localhost:8080/quota/users/{userId}
Content-Type: application/x-www-form-urlencoded
```

Checking quota quota:

```bash

GET http://localhost:8080/quota/users

```
