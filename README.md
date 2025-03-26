# Multiuser Task Scheduler 🚀

*A web application for multi-user task management*

## 📌 Содержание
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
  
### Работа с задачами
- ➕ Create/edit tasks
- ✔️ Mark tasks as completed
- 🗑️ Delete tasks

### Умные оповещения по почте
- ✉️ Welcome email
- 🔔 Daily midnight reminders:
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

<div align="center">
  <img src="docs/er-diagram.png" alt="Entity-Relationship Diagram" width="600"/>
  <p><em>Рисунок 1. ER-диаграмма базы данных (PostgreSQL)</em></p>
</div>

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
```bash
docker-compose -f docker-compose-prod.yaml up
```

## 📧 Contacts
- Author: Vladislav Gorelkin
- 📧 Email: vlad_gorelkin@inbox.ru | loucterso@gmail.com
- 💻 GitHub: [LoucterSo](https://github.com/LoucterSo)

