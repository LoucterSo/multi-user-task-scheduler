# Multiuser Task Scheduler 🚀

*Веб-приложение для управления задачами с многопользовательским доступом*

## 📌 Содержание
- [Основные функции](#-основные-функции)
- [Технологический стек](#-технологический-стек)
- [Схема базы данных](#-схема-базы-данных)
- [Быстрый старт](#-быстрый-старт)
- [Тестирование](#-тестирование)
- [Развертывание](#-развертывание)
- [Контакты](#-контакты)

## 🌟 Основные функции
### Работа с пользователями:
- ✅ Регистрация и авторизация (JWT)
- 🔐 Ролевая модель доступа
  
### Работа с задачами
- ➕ Создание/редактирование задач
- ✔️ Пометка задачи как сделанной
- Удаление

### Умные оповещения по почте
- ✉️ Приветственное письмо
- 🔔 Каждый день в полночь напоминания о предстоящих задачах и выполненных задачах за день

## 🛠 Технологический стек
| Категория       | Технологии                          |
|----------------|-----------------------------------|
| **Бэкенд**     | Java 17, Spring Boot 3, Web, Data JPA, Security, Cloud, Kafka, Scheduler, Mail|
| **Базы данных**| PostgreSQL, Liquibase             |
| **Инфраструктура** | Docker, Docker Compose       |
| **Сборка**     | Maven|
| **Тестирование** | JUnit 5, Mockito, Testcontainers |

## 📊 Схема базы данных
![ER-диаграмма](docs/er-diagram.png)

## ⚡ Быстрый старт
1. Клонируйте репозиторий:
```bash
git clone https://github.com/LoucterSo/multi-user-task-scheduler
cd multi-user-task-scheduler
docker-compose -f docker-compose-dev.yaml up --build
```

## 🧪 Тестирование 
```bash
# Unit-тесты в сервисе
./mvnw test
```

## 🐳 Развертывание
Каждый сервис имеет:
### 1. application-local.yaml: Для запуска сервиса локально(без Docker)
### 2. application-dev.yaml: Для запуска всего приложения в режиме разработки используется docker-compose-dev
#### В корне проекта для запуска:
```bash
docker-compose -f docker-compose-dev.yaml up
```
### 3. application-prod.yaml: Для запуска всего приложения в режиме продакшена используется docker-compose-prod
#### В корне проекта для запуска:
```bash
docker-compose -f docker-compose-prod.yaml up
```

## 📧 Контакты
- Автор: Владислав Горелкин
- 📧 Email: vlad_gorelkin@inbox.ru или loucterso@gmail.com
- 💻 GitHub: [LoucterSo](https://github.com/LoucterSo)

