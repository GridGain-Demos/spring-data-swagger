## Setting Up Environment

* Java Developer Kit, version 17
* Apache Maven 3.6 or later
* GridGain version 8.9.10 or later
* Use OpenAPI with Swagger Integration
* Use GridGain Thin Client
* Use Spring Boot 2.7.18
* Use Ignite Spring Data Extension 3
* Your favorite IDE, such as IntelliJ IDEA, or Eclipse, or a simple text editor.

## Clone the Project

Open a terminal window and clone the project to your dev environment:

```shell script
git clone https://github.com/GridGain-Demos/spring-data-swagger.git
```

## Configure Ignite Spring Boot and Data Extensions

1. Enable Ignite Spring Boot, Spring Boot Thin Client AutoConfigure and Spring Data extensions by adding the following artifacts to the `pom.xml` file:

    ```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.18</version>
        <relativePath/> 
    </parent>
   
    <dependency>
       <groupId>org.apache.ignite</groupId>
       <artifactId>ignite-spring-boot-thin-client-autoconfigure-ext</artifactId>
       <version>1.0.0</version>
    </dependency>

    <dependency>
       <groupId>org.apache.ignite</groupId>
       <artifactId>ignite-spring-data-ext</artifactId>
       <version>3.0.0</version>
    </dependency>

    ```

2. Add the following property to the pom.xml to select a version of H2 supported by Ignite:
    ```xml
    <properties>
        <h2.version>1.4.197</h2.version>
    </properties>
    ```

## Spring Boot settings

1. Update the `Application` class by tagging it with `@EnableIgniteRepositories` annotation.

2. Start the application and confirm Spring Boot started an Ignite server node instance.

3. Add a `ServerNodeStartup` class that will be a separate application/process for an Ignite server node.

    ```java
    public class ServerNodeStartup {
        public static void main(String[] args) {
            Ignition.start();
        }
    }
    ```

4. Start server node using the `ServerNodeStartupClass` application.

## Load World Database

1. Open the `world.sql` script and add the `VALUE_TYPE` property to the `CREATE TABLE Country` statement:

    ```sql
    VALUE_TYPE=com.gridgain.training.spring.model.Country
    ``` 

2. Add the following `VALUE_TYPE` property to the `CREATE TABLE City` statement

    ```sql
    VALUE_TYPE=com.gridgain.training.spring.model.City
    ``` 

3. Add the following `KEY_TYPE` property to the `CREATE TABLE City` statement

    ```sql
    KEY_TYPE=com.gridgain.training.spring.model.CityKey
    ``` 

4. Build a shaded package for the app:
    ```shell script
    mvn clean package -DskipTests=true
    ```

5. Start an SQLLine process:

    ```shell script
    java -cp libs/app.jar sqlline.SqlLine
    ```

6. Connect to the cluster:

    ```shell script
    !connect jdbc:ignite:thin://127.0.0.1/ ignite ignite
    ```

7. Load the database:

    ```shell script
    !run config/world.sql
    ```

## Start the Spring Boot application & Run Simple Auto-Generated Queries Via Ignite Repository

1. Start the Spring Boot application that will run the autoconfigured Ignite Thin Cl.
2. Create the `CountryRepository` class:

    ```java
    @RepositoryConfig (cacheName = "Country")
    @Repository
    public interface CountryRepository extends IgniteRepository<Country, String> {
    
    }
    ```

3. Add a method that returns countries with a population bigger than provided one:

    ```java
    public List<Country> findByPopulationGreaterThanOrderByPopulationDesc(int population);
    ```

4. Add a test that validates that the method returns a non-empty result:

    ```java
    @Test
    void countryRepositoryWorks() {
       System.out.println("count=" + countryRepository.findByPopulationGreaterThanOrderByPopulationDesc(100_000_000).size());
    }
    ```
## Run Direct Queries With JOINs Via Ignite Repository

1. Create the `CityRepository` class:

    ```java
    @RepositoryConfig(cacheName = "City")
    @Repository
    public interface CityRepository extends IgniteRepository<City, CityKey> {
    }
    ```

2. Add a query that returns a complete key-value pair:

    ```java
    public Cache.Entry<CityKey, City> findById(int id);
    ```
3. Add a direct SQL query that joins two tables:

    ```java
    @Query("SELECT city.name, MAX(city.population), country.name FROM country " +
            "JOIN city ON city.countrycode = country.code " +
            "GROUP BY city.name, country.name, city.population " +
            "ORDER BY city.population DESC LIMIT ?")
    public List<List<?>> findTopXMostPopulatedCities(int limit);
    ```

4. Create a test to validate the methods respond properly:

    ```java
    @Test
    void cityRepositoryWorks() {
        System.out.println("city = " + cityRepository.findById(34));
        
        System.out.println("top 5 = " + cityRepository.findTopXMostPopulatedCities(5));
    }
    ```
## Create Spring REST Controller

1. Create a REST Controller for the application with RequestMapping:

    ```java
    @RestController
   @RequestMapping("/api")
    public class WorldDatabaseController {
        @Autowired CityRepository cityRepository;
        
    }
    ```

2. Add a method that returns top X most populated cities:

    ```java
    @GetMapping("/mostPopulated")
    public List<List<?>> getMostPopulatedCities(@RequestParam(value = "limit", required = false) Integer limit) {
        return cityRepository.findTopXMostPopulatedCities(limit);
    }
    ```

3. Test the method in your browser or POSTMAN:

    ```shell script
    http://localhost:8080/api/mostPopulated?limit=5
    ```

## BONUS - OpenAPI with Swagger Integration: Configure Swagger API Extensions to generate Swagger API DOCS

1. Enable Springdoc OpenAPI UI that has Swagger Integration by adding the following artifacts to the `pom.xml` file

    ```xml
         <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-tx</artifactId>
            <version>5.3.27</version>
        </dependency>

        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-ui</artifactId>
            <version>1.8.0</version>
        </dependency>
    ```
2. Update the `IgniteConfig` as shown for Springdoc OpenAPI UI with Swagger Integration:
   ```java
      @Configuration
      public class IgniteConfig extends WebMvcConfigurationSupport {
      @Override
      public void addResourceHandlers(ResourceHandlerRegistry registry)
      {
      registry.addResourceHandler("/swagger-ui/**")
      .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/5.11.8/");
      }
   
          @Bean
          public OpenAPI springShopOpenAPI() {
              return new OpenAPI()
                      .info(new Info().title("SpringData Swagger")
                              .description("SpringData + REST + SwaggerUI")
                              .version("v0.0.1")
                              .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                      .externalDocs(new ExternalDocumentation()
                              .description("SpringData Swagger Documentation")
                              .url("https://github.com/rdGridGain/spring-data-swagger"));
          }
   
   
      }

   ```
3. Test Swagger API docs in your browser or POSTMAN :

   ```shell script
   http://localhost:18080/v3/api-docs
   ```

4. Test Swagger UI docs in your browser:

   ```shell script
   http://localhost:18080/swagger-ui/index.html with 
   http://localhost:18080/v3/api-docs in Explore
   ```
   
