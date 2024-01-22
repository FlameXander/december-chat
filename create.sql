create table users(
user_id serial PRIMARY KEY,
user_login VARCHAR(255) UNIQUE NOT NULL,
user_password VARCHAR(255) NOT NULL,
user_username VARCHAR(255) UNIQUE NOT NULL,
user_isAdmin BOOLEAN NOT NULL);