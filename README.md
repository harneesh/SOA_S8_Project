<<<<<<< HEAD
# FacultySync – AI-Powered Faculty Leave Management & Substitute Recommendation System

**Course:** SOA Programming and Microservices
**Tech:** Java 21, Spring Boot 4.1 (REST), Spring Data JPA, MySQL 8, Maven, HTML/JavaScript

FacultySync lets faculty apply for leave, lets the HOD approve or reject it, finds the classes
affected by an approved leave, and uses an **AI Substitute Service** to recommend the best
substitute for each class. The HOD always makes the final choice.

---

## 1. Architecture

```
   Faculty (browser)                         HOD (browser)
          |                                        |
          v                                        v
 +-------------------+                   +-------------------+
 |  Faculty Portal   |                   |    HOD Portal     |
 |    port 8083      |                   |    port 8084      |
 +-------------------+                   +-------------------+
          |  REST/JSON              REST/JSON  |         |  REST/JSON
          v                                    v         v
 +----------------------------------------------+   +--------------------------+
 |  Faculty Management Service      port 8081   |<--|  AI Substitute Service   |
 |  faculty, leave, timetable, availability,    |   |  port 8082               |
 |  substitute assignment                       |   |  scores & ranks          |
 +----------------------------------------------+   +--------------------------+
          | JPA / JDBC
          v
 +----------------------+
 | MySQL facultysync_db |
 +----------------------+
```

| Service | Port | Type | Responsibility |
|---|---|---|---|
| `faculty-portal` | **8083** | Front end | Faculty: apply for leave, track status (and see who covers their classes), view own timetable |
| `hod-portal` | **8084** | Front end | HOD: approve/reject leave, view AI recommendations, confirm substitutes, **add/edit/delete faculty and timetable periods**, view substitutions |
| `faculty-service` | 8081 | Backend REST API | Faculty, leave, timetable, availability, substitute assignment. Stores its data in **MySQL**. Its home page shows a dashboard of all 4 services |
| `ai-substitute-service` | 8082 | Backend REST API | Substitute recommendation. Owns **no data**: it gets everything from the Faculty Service through REST calls (`RestClient`) |

**SOA / microservice concepts this project shows**
- **Single responsibility:** each service does one job and can be changed or redeployed on its own.
- **Separate front ends per user role:** Faculty and HOD each have their own portal service; both use the same backend APIs.
- **Service-to-service communication:** the AI service calls the Faculty Service's REST API (`FacultyServiceClient.java`).
- **Loose coupling:** the services share only JSON contracts (DTOs), not code or a database.
- **Database per service:** only the Faculty Service talks to MySQL. The AI service never touches the database; it asks the Faculty Service through its API.
- **Configuration:** the URL of the other service lives in `application.properties` (`faculty.service.url`).
- **Fault handling:** if the Faculty Service is down, the AI service returns `503 Service Unavailable` with a clear message.
- **Stateless REST APIs** with proper HTTP methods and status codes (200, 201, 400, 404, 409, 503).

---

## 2. How the AI recommendation works

For each affected class:

1. **Filter:** keep only faculty who are free in that period (no class, no other substitution, not on leave).
   The Faculty Service decides availability (`GET /api/faculty/available`).
2. **Score (out of 100)** each candidate:

| Factor | Points |
|---|---|
| Teaches the same subject (subject expertise) | 50 |
| Same department as the absent faculty | 20 |
| Low workload: `30 × (maxLoad − load) / maxLoad` | 0 – 30 |

3. **Rank:** highest score is the *recommended* substitute; the next 3 are *alternatives*. Each comes with human-readable reasons.

This is a rule-based, weighted multi-criteria scoring model (explainable AI). The weights are in
`RecommendationService.java`, so you can change them or swap in an ML model later without touching the Faculty Service.

---

## 3. Running in IntelliJ IDEA

0. **MySQL setup (one time):** make sure the MySQL80 service is running, then open
   `faculty-service/src/main/resources/application.properties` and set your MySQL login:
   ```
   spring.datasource.username=root
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```
   You don't need to create anything by hand: the database `facultysync_db` and all tables are created
   automatically on the first start, and sample data is inserted.
1. **File → Open** → select the `soa_final_project` folder.
2. IntelliJ detects `pom.xml` and imports both services as Maven modules. If it shows a
   *"Load Maven Project"* popup, click it. (Or right-click `pom.xml` → *Add as Maven Project*.)
3. Make sure **Project SDK = Java 21** (*File → Project Structure → Project*).
4. In the run-configuration dropdown (top right), pick **FacultySync (All Services)** and click ▶.
   This starts all 4 services. You can also run them separately:
   - `1. Faculty Service (8081)` → `FacultyServiceApplication`
   - `2. AI Substitute Service (8082)` → `AiSubstituteServiceApplication`
   - `3. Faculty Portal (8083)` → `FacultyPortalApplication`
   - `4. HOD Portal (8084)` → `HodPortalApplication`
5. Open in the browser:
   - **http://localhost:8083**: Faculty Portal
   - **http://localhost:8084**: HOD Portal
   - http://localhost:8081: service dashboard (shows which services are running)

> If the run configurations don't show up, just open each `*Application.java` file and click the green ▶ next to `main`.
> Start the Faculty Service first.

### From the command line (optional)
```
mvn package -DskipTests
java -jar faculty-service/target/faculty-service-1.0.0.jar
java -jar ai-substitute-service/target/ai-substitute-service-1.0.0.jar
java -jar faculty-portal/target/faculty-portal-1.0.0.jar
java -jar hod-portal/target/hod-portal-1.0.0.jar
```

---

## 4. Demo flow (for the presentation)

1. Open **http://localhost:8081**: the dashboard shows the 4 microservices and that they are running.
2. **Faculty Portal (8083):** choose a faculty in *Logged in as* (e.g. Prof. Karthik Rao), look at *My Timetable*,
   then *Apply Leave* for a day on which they have classes. It shows as **PENDING** under *My Leaves*.
3. **HOD Portal (8084):** the leave appears in *Leave Requests*. Click **Approve**.
4. The AI recommendations appear automatically: each affected class, the best substitute with a score and reasons, and alternatives.
5. Pick a substitute and click **Confirm**. The *Substitutions* tab lists it, and the *Faculty* tab shows the higher workload.
6. Back in the **Faculty Portal**, *My Leaves* now shows **APPROVED** and who is covering each class.
7. **HOD Portal > Faculty / Timetable:** add a new faculty member, give them a period, then edit or delete it.
   Clashes are blocked (a teacher or class cannot have two classes in the same period).

> There is no password login. In the Faculty Portal, the faculty member chooses their name from the *Logged in as* list.

You can also test every API from IntelliJ with **`api-tests.http`** (click ▶ next to each request) or with Postman.

---

## 5. REST API reference

### Faculty Management Service – `http://localhost:8081/api`
| Method | Endpoint | Description |
|---|---|---|
| GET | `/faculty` | List faculty |
| GET | `/faculty/{id}` | One faculty |
| POST | `/faculty` | Add faculty |
| PUT | `/faculty/{id}` | Edit faculty |
| DELETE | `/faculty/{id}` | Delete faculty (only if they have no periods, leaves or substitutions) |
| GET | `/faculty/workload` | Classes per week for each faculty |
| GET | `/faculty/available?date=&period=` | Free faculty for a slot |
| GET | `/timetable?facultyId=` | Timetable (optional filter) |
| POST | `/timetable` | Add a period (checks faculty and class clashes) |
| PUT | `/timetable/{id}` | Edit a period |
| DELETE | `/timetable/{id}` | Delete a period (only if it has no substitutions) |
| GET | `/leaves?status=` | List leaves |
| POST | `/leaves` | Apply for leave |
| PUT | `/leaves/{id}/approve` | Approve leave |
| PUT | `/leaves/{id}/reject` | Reject leave |
| GET | `/leaves/{id}/affected-classes` | Classes missed during the leave |
| GET | `/substitutes` | Confirmed substitutions |
| POST | `/substitutes` | HOD confirms a substitute |

### AI Substitute Service – `http://localhost:8082/api`
| Method | Endpoint | Description |
|---|---|---|
| GET | `/recommendations/leave/{leaveId}` | Ranked substitutes for every affected class |
| GET | `/recommendations/model` | Scoring model and weights |

---

## 6. Project structure

```
soa_final_project/
├── pom.xml                         (parent – builds both services)
├── api-tests.http                  (API tests for IntelliJ)
├── faculty-portal/                 (port 8083, static/index.html = Faculty UI)
├── hod-portal/                     (port 8084, static/index.html = HOD UI)
├── faculty-service/
│   └── src/main/
│       ├── java/com/facultysync/faculty/
│       │   ├── FacultyServiceApplication.java
│       │   ├── controller/   FacultyController, LeaveController, TimetableController
│       │   ├── service/      FacultyDataService   (business logic)
│       │   ├── entity/       Faculty, TimetableSlot, LeaveRequest, SubstituteAssignment (JPA → MySQL tables)
│       │   ├── repository/   Spring Data JPA repositories
│       │   ├── model/        Models               (request/response DTOs)
│       │   └── config/       CorsConfig, ApiExceptionHandler, DataSeeder (sample data)
│       └── resources/
│           ├── application.properties
│           └── static/index.html   (service dashboard)
└── ai-substitute-service/
    └── src/main/
        ├── java/com/facultysync/ai/
        │   ├── AiSubstituteServiceApplication.java
        │   ├── controller/   RecommendationController
        │   ├── service/      RecommendationService (scoring model)
        │   ├── client/       FacultyServiceClient  (REST calls to port 8081)
        │   ├── model/        Dtos
        │   └── config/       ApiExceptionHandler
        └── resources/application.properties
```

## 7. Database (MySQL)

Database: **`facultysync_db`** (created automatically). Hibernate creates the tables from the `@Entity` classes
(`spring.jpa.hibernate.ddl-auto=update`), and `DataSeeder` inserts sample data only when the `faculty` table is empty.
Data is kept when the service restarts.

| Table | Entity | Content |
|---|---|---|
| `faculty` | `Faculty` | id, name, department, email, designation |
| `faculty_subjects` | (inside `Faculty`) | faculty_id, subject: the subjects each faculty can teach |
| `timetable_slot` | `TimetableSlot` | faculty_id, day, period, subject, class_name, room |
| `leave_request` | `LeaveRequest` | faculty_id, from_date, to_date, reason, status (PENDING/APPROVED/REJECTED), applied_on |
| `substitute_assignment` | `SubstituteAssignment` | leave_id, slot_id, date, original_faculty_id, substitute_faculty_id |

Data access uses **Spring Data JPA repositories** (`repository/` package). For example,
`findByFacultyIdAndDayOrderByPeriod(...)` becomes SQL automatically.

Useful queries for the demo (MySQL Workbench):
```sql
USE facultysync_db;
SELECT * FROM faculty;
SELECT * FROM leave_request;
SELECT * FROM substitute_assignment;
```

**Reset the sample data:** `DROP DATABASE facultysync_db;` then restart the Faculty Service.

## 8. Possible future improvements
- API Gateway and service discovery (Spring Cloud Gateway + Eureka)
- Login with roles (Faculty / HOD / Admin) using Spring Security + JWT
- Email notifications to the substitute
- Docker Compose to run all services
=======
# SOA_S8_Project Batch 13
>>>>>>> e49c6bf6bfd3cdf78552e30854acc5ef30469d8d
