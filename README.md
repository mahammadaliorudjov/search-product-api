# search-product-api
## Start the services(PostgreSQL and Elasticsearch) with Docker Compose
```bash
docker-compose up
```
## Launch Spring Boot application
```bash
./mvnw spring-boot:run
```
## Access the API
You can interact with the search service using a tool like Postman or curl. Here are some example endpoints:

Load Data from PostgreSQL into Elasticsearch
To load data from the PostgreSQL database into Elasticsearch:
```bash
POST http://localhost:8080/api/copyData
```
Search for Products
To search for products by name, description, or SKU code, use this endpoint:
```bash
GET http://localhost:8080/api/products?query=<your-query>
```
You can also filter products by additional fields like active and startDate:
```bash
GET http://localhost:8080/api/products?query=<your-query>&active=true&startDate=2000-01-01
```
