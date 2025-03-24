--liquibase formatted sql

--changeset loucterso:create-tables.0 rollback:"DROP TABLE users"
CREATE TABLE IF NOT EXISTS users (
  user_id BIGSERIAL PRIMARY KEY,
  first_name VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  enabled BOOLEAN NOT NULL DEFAULT FALSE,
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset loucterso:create-tables.1
CREATE TABLE IF NOT EXISTS roles (
  role_id BIGSERIAL PRIMARY KEY,
  user_id BIGSERIAL NOT NULL,
  role VARCHAR(255) NOT NULL
);

--changeset loucterso:create-tables.2
CREATE TABLE IF NOT EXISTS tasks (
  task_id BIGSERIAL PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  description VARCHAR(255),
  user_id BIGSERIAL NOT NULL,
  done BOOLEAN NOT NULL DEFAULT FALSE,
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  completion_time TIMESTAMP
);