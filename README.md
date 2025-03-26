# Multiuser Task Scheduler 🚀

*A web application for multi-user task management*

## 📌 Table of Contents
- [Key Features](#-key-features)
- [Tech Stack](#-tech-stack)
- [Database Schema](#-database-schema)
- [Quick Start](#-quick-start)
- [Testing](#-testing)
- [Deployment](#-deployment)
- [Contacts](#-contacts)

## 🌟 Key Features
### User Management:
- ✅ Registration and authentication (JWT)
- 🔐 Role-based access control
  
### Task Management
- ➕ Create/edit tasks
- ✔️ Mark tasks as completed
- 🗑️ Delete tasks

### Smart Notifications
- ✉️ Welcome email
- 🔔 Daily midnight email reminders:
  - Summary of completed tasks
  - Upcoming tasks

## 🛠 Tech Stack
| Category       | Technologies                          |
|----------------|-----------------------------------|
| **Backend**     | Java 17, Spring Boot 3, Web, Data JPA, Security, Cloud, Kafka, Scheduler, Mail|
| **Database**| PostgreSQL, Liquibase             |
| **Infrastructure** | Docker, Docker Compose       |
| **Build Tool**     | Maven|
| **Testing** | JUnit 5, Mockito, Testcontainers |

## 📊 Database Schema

```mermaid
erDiagram
    users ||--o{ roles : "has"
    users ||--o{ tasks : "creates"
    
    users {
        bigint user_id PK
        varchar(255) first_name
        varchar(255) last_name
        varchar(255) password
        varchar(255) email
        boolean enabled
        timestamp created_time
        timestamp updated_time
    }
    
    roles {
        bigint role_id PK
        bigint user_id FK
        varchar(255) role
    }
    
    tasks {
        bigint task_id PK
        varchar(255) title
        varchar(255) description
        bigint user_id FK
        boolean done
        timestamp created_time
        timestamp updated_time
        timestamp completion_time
    }
```

## ⚡ Quick Start
1. Clone repository:
```bash
git clone https://github.com/LoucterSo/multi-user-task-scheduler
cd multi-user-task-scheduler
docker-compose -f docker-compose-dev.yaml up --build
```

## 🧪 Testing
```bash
# Unit-tests
./mvnw test
```

## 🐳 Deployment
### 1. Local execution (without Docker):
- application-local.yaml
### 2. Development mode:
```bash
docker-compose -f docker-compose-dev.yaml up
```
### 3. Production mode:
*Do not forget to add the .env file with the necessary properties to the root of the project*
```bash
docker-compose -f docker-compose-prod.yaml up
```

## 📧 Contacts
- Author: Vladislav Gorelkin
- 📧 Email: vlad_gorelkin@inbox.ru | loucterso@gmail.com
- 💻 GitHub: [LoucterSo](https://github.com/LoucterSo)

