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

./mvnw compile

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

### OpenAPI в проекте

#### Описание
Файл **src/main/resources/openapi.yaml** описывает все эндпоинты, параметры и модели данных на языке OpenAPI 3.0.3. Например, эндпоинт получения статей выглядит следующим образом:

```yaml
paths:
  /api/articles:
    get:
      tags:
        - articles
      operationId: getArticles
      summary: Get paginated list of articles
      parameters:
        - name: category
          in: query
          required: false
          schema:
            type: string
        - name: sourceId
          in: query
          required: false
          schema:
            type: integer
            format: int64
        - name: q
          in: query
          required: false
          description: Search by title
          schema:
            type: string
        - name: from
          in: query
          required: false
          schema:
            type: string
            format: date-time
        - name: to
          in: query
          required: false
          schema:
            type: string
            format: date-time
        - name: page
          in: query
          required: false
          schema:
            type: integer
            default: 0
        - name: size
          in: query
          required: false
          schema:
            type: integer
            default: 20
        - name: sort
          in: query
          required: false
          schema:
            type: string
      responses:
        '200':
          description: Paginated list of articles
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ArticlePage'
```

#### Генерация

В pom.xml подключён плагин openapi-generator-maven-plugin. При запуске ./mvnw compile он читает openapi.yaml и автоматически генерирует Java-интерфейсы и DTO-классы в папку target/generated-sources/openapi/. Пример сгенерированного интерфейса ArticlesApi:

```java
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-03-18T10:47:43.346789700+03:00[Europe/Moscow]", comments = "Generator version: 7.20.0")
@Validated
@Controller
@Tag(name = "articles", description = "the articles API")
public interface ArticlesApi {

    String PATH_GET_ARTICLES = "/api/articles";
    /**
     * GET /api/articles : Get paginated list of articles
     *
     * @param category  (optional)
     * @param sourceId  (optional)
     * @param q Search by title (optional)
     * @param from  (optional)
     * @param to  (optional)
     * @param page  (optional, default to 0)
     * @param size  (optional, default to 20)
     * @param sort  (optional)
     * @return Paginated list of articles (status code 200)
     */
    @Operation(
        operationId = "getArticles",
        summary = "Get paginated list of articles",
        tags = { "articles" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Paginated list of articles", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = ArticlePage.class))
            })
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = ArticlesApi.PATH_GET_ARTICLES,
        produces = { "application/json" }
    )
    ResponseEntity<ArticlePage> getArticles(
        @Parameter(name = "category", description = "", in = ParameterIn.QUERY) @Valid @RequestParam(value = "category", required = false) @Nullable String category,
        @Parameter(name = "sourceId", description = "", in = ParameterIn.QUERY) @Valid @RequestParam(value = "sourceId", required = false) @Nullable Long sourceId,
        @Parameter(name = "q", description = "Search by title", in = ParameterIn.QUERY) @Valid @RequestParam(value = "q", required = false) @Nullable String q,
        @Parameter(name = "from", description = "", in = ParameterIn.QUERY) @Valid @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Nullable OffsetDateTime from,
        @Parameter(name = "to", description = "", in = ParameterIn.QUERY) @Valid @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @Nullable OffsetDateTime to,
        @Parameter(name = "page", description = "", in = ParameterIn.QUERY) @Valid @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
        @Parameter(name = "size", description = "", in = ParameterIn.QUERY) @Valid @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
        @Parameter(name = "sort", description = "", in = ParameterIn.QUERY) @Valid @RequestParam(value = "sort", required = false) @Nullable String sort
    );

}
```

#### Реализация

Контроллеры просто имплементируют сгенерированный интерфейс. Если в openapi.yaml добавить новый эндпоинт, компилятор сразу укажет, что контроллер не реализует новый метод. Пример реализации: 

```java
@RestController
public class ArticlesController implements ArticlesApi {
   private final ArticleService articleService;

   public ResponseEntity<ArticlePage> getArticles(String category, Long sourceId, String q, OffsetDateTime from, OffsetDateTime to, Integer page, Integer size, String sort) {
      int pageNum = page != null ? page : 0;
      int pageSize = size != null ? size : 20;
      PageRequest pageable = PageRequest.of(pageNum, pageSize);
      ArticlePage result = this.articleService.getArticles(category, sourceId, q, from, to, pageable);
      return ResponseEntity.ok(result);
   }

   @Generated
   public ArticlesController(final ArticleService articleService) {
      this.articleService = articleService;
   }
}
```