### Prerequisites

* `Java 21` should be installed --> `export JAVA_HOME=$(/usr/libexec/java_home -v 21)`
* `Docker` should be installed
* `Maven` should be installed
* `pgAdmin`/`DBeaver` can be installed (Optional)

-----

### How to start the application

* First way
    * Run [./scripts/run.sh](scripts%2Frun.sh) script
* Second way
    * Run `mvn clean install` or `mvn clean package`
    * Run `mvn spring-boot:run` or `./mvnw spring-boot:run`

-----

### How to test the application

* Swagger Url: http://localhost:8080/api/v1/swagger-ui.html
* Actuator Url: http://localhost:8080/api/v1/actuator
* Metric Url: http://localhost:8080/api/v1/actuator/metrics
* Run `mvn test` or `mvn clean install` or `mvn clean package` command to run all the tests
* Test User to Get JWT Token:
    * `username: admin_user`
    * `password: test1234`

-----

### `docker-compose` contains the followings

* Zipkin: http://localhost:9411/
* Prometheus: http://localhost:9090/graph
* RedisInsight: http://localhost:8001/
    * `ADD REDIS DATABASE`: https://collabnix.com/running-redisinsight-using-docker-compose/
* Grafana: http://localhost:3000/
    * `Email or username: admin`
    * `Password: admin`
    * Add datasource
        * Select Prometheus
        * Prometheus Url: http://prometheus:9090/
        * Save & test
* PostgreSQL DB connection details
    * `POSTGRES_USER: postgres`
    * `POSTGRES_PASSWORD: postgres`
    * `Port: 5432`

-----