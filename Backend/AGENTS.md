# AGENTS.md

## Project Shape
- Single-module Maven Spring Boot backend; root package is `com.digitaltravel.erp` and the app entrypoint is `DigitalTravelErpApplication`.
- Java target is 21 (`pom.xml`), and the Maven wrapper downloads Maven 3.9.14; prefer `./mvnw.cmd` on Windows from the repo root.
- Main layers live under `src/main/java/com/digitaltravel/erp`: `controller`, `service`, `repository`, `entity`, `dto`, `config`, `exception`.

## Commands
- Run locally: `./mvnw.cmd spring-boot:run`.
- Full verification: `./mvnw.cmd test`; this includes `@SpringBootTest` and starts the Spring context.
- Focused unit test class: `./mvnw.cmd -Dtest=NhanVienServiceTest test`.
- Focused test method: `./mvnw.cmd -Dtest=NhanVienServiceTest#ganVaiTro_tuChoiVaiTroKhongHopLeChoNhanVien test`.
- Build/package without tests: `./mvnw.cmd clean package -DskipTests`, then run `java -jar target/digital-travel-erp-*.jar`.

## Runtime And Database
- `src/main/resources/application.yaml` imports root `.env` with `spring.config.import: optional:file:.env[.properties]`; `.env` is gitignored and should not be committed.
- Required DB env usually comes from `.env`: `DB_HOST`, `DB_PORT`, `DB_SERVICE` or `DB_URL`, plus `DB_USERNAME`, `DB_PASSWORD`; JWT has defaults but README documents `JWT_SECRET`/`JWT_EXPIRATION`.
- Hibernate uses `ddl-auto: validate` and `spring.sql.init.mode: never`; run Oracle DDL/seed scripts manually, starting with `src/main/resources/db/KhoiTaoBang.sql`.
- Full `mvn test` requires a reachable Oracle schema because `DigitalTravelErpApplicationTests` loads the real Spring context; pure Mockito tests can run without DB using `-Dtest=<unit-test>`.

## API And Security Conventions
- Trust controller annotations and `SecurityConfig` over older prose in `HUONGDAN.md`; some older examples use stale prefixes like `/api/admin`, `/api/sales`, `/api/hdv`, or `/api/khachhang`.
- Current secured prefixes are hyphenated: `/api/quan-tri/**`, `/api/san-pham/**`, `/api/kinh-doanh/**`, `/api/dieu-hanh/**`, `/api/huong-dan-vien/**`, `/api/ke-toan/**`, `/api/khach-hang/**`, `/api/thanh-toan/**`.
- Public paths are `/api/auth/**`, `/api/public/**`, Swagger `/swagger-ui/**`, and OpenAPI `/v3/api-docs/**`.
- Roles are defined in `VaiTroConst`: `ADMIN`, `SANPHAM`, `KINHDOANH`, `DIEUHANH`, `KETOAN`, `HDV`, `KHACHHANG`.

## Persistence Quirks
- Entities map to Oracle identifiers explicitly with `@Table`/`@Column`; `PhysicalNamingStrategyStandardImpl` means Java field/column casing is not auto-converted.
- Lombok `@FieldDefaults(level = PRIVATE)` is common; many entity fields use PascalCase-style names matching Oracle columns, so preserve existing naming when editing mappings.

## Background Jobs
- Scheduling is enabled globally with `@EnableScheduling`.
- `DatTourScheduler` expires held bookings every 60 seconds.
- `TourThucTeScheduler` runs hourly (`0 0 * * * *`) for tour status/dynamic pricing work.

## Workflow Notes
- `CONTRIBUTING.md` asks for feature branches from `dev`, PRs into `dev`, conventional commits like `feat(scope): subject`, and no force-push to shared `main`/`dev`.
- Do not commit secrets, `.env`, local Spring profiles, logs, or generated `target/` output.
