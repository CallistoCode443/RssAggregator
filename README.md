# RSS News Aggregator

Проект выполнен в рамках дисплины **Инструментальные средства разработки ПО**.

## Стек технологий

- **Java 21** - последняя LTS-версия
- **Spring Boot 4.0.3**
- **Maven** - управление зависимостями и сборка проекта
- **Flyway** - миграции базы данных
- **PostgreSQL 17** - основная база данных
- **OpenAPI 3.0.3** - спецификация API
- **Docker Compose**
- **ROME** - библиотека для парсинга RSS-лент
---
## Как запустить

Требования:
 - Docker Desktop
 - Git
 - JDK 21

### Windows
```
git clone https://github.com/CallistoCode443/RssAggregator.git

cd RssAggregator

docker-compose up -d

./mvnw spring-boot:run
```

Приложение запустится на http://localhost:8080


---
## Swagger UI
После запуска документация API доступна по адресу:
```
http://localhost:8080/swagger-ui/index.html
```