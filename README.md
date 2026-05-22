# FluxEngine

Java Spring Boot platform for ESP32 heat-flux/temperature monitoring.

## Endpoints

- `POST /api/measurements` — accepts ESP32 JSON.
- `GET /api/measurements/latest?limit=100` — latest measurements.
- `GET /api/measurements/search` — filters by date, deviceId, lambda and heat flux.
- `/dashboard` — realtime chart, filtered chart and table.

## ESP32 JSON example

```json
{
  "t1": 24.94,
  "t2": 5.20,
  "deltaT": 19.74,
  "tecMv": 0.520,
  "qTec": 2.402,
  "fluxMv": 1.100,
  "qFlux": 14.520,
  "thicknessM": 0.03,
  "deviceId": "ESP32-01"
}
```

If `lambdaTec` or `lambdaFlux` are absent, the server calculates:

`lambda = q * thicknessM / deltaT`.

## Local run

```bash
mvn spring-boot:run
```

Uses local H2 database by default.

## Render settings

Build command:

```bash
mvn clean package -DskipTests
```

Start command:

```bash
java -jar target/fluxengine-0.0.1-SNAPSHOT.jar
```

Environment variables for Render PostgreSQL:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://HOST:PORT/DATABASE
SPRING_DATASOURCE_USERNAME=USER
SPRING_DATASOURCE_PASSWORD=PASSWORD
SPRING_DATASOURCE_DRIVER=org.postgresql.Driver
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

Use Render External Database URL for local access, Internal Database URL for the web service on Render.
