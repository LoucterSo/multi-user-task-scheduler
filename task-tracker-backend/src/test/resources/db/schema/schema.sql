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

CREATE TABLE IF NOT EXISTS roles (
  role_id BIGSERIAL PRIMARY KEY,
  user_id BIGSERIAL NOT NULL,
  role VARCHAR(255) NOT NULL
);

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


ALTER TABLE roles
    ADD CONSTRAINT fk_roles_to_user FOREIGN KEY (user_id)
        REFERENCES users ON UPDATE RESTRICT ON DELETE CASCADE;
ALTER TABLE tasks
    ADD CONSTRAINT fk_tasks_to_user FOREIGN KEY (user_id)
        REFERENCES users ON UPDATE RESTRICT ON DELETE CASCADE;

CREATE INDEX idx_tasks_user_id ON tasks(user_id);