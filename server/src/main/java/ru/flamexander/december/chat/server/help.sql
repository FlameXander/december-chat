Пользователь БД - user_hw18
Пароль - 1234
CREATE USER user_hw18 WITH PASSWORD '1234';
CREATE DATABASE db_hw18;
ALTER DATABASE db_hw18 OWNER TO user_hw18;
--Далее, остальные изменения делаем из под своей базы данных
GRANT ALL PRIVILEGES ON DATABASE db_hw18 TO user_hw18;
ALTER SCHEMA public OWNER TO user_hw18;
ALTER TABLE public.mytable OWNER TO testuser; --Если таблицы уже созданы
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    login VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    user_role VARCHAR(255) NOT NULL,
    user_enabled BOOLEAN NOT NULL
);

CREATE TABLE users (
	user_id serial4 NOT NULL,
	login varchar(255) NOT NULL,
	user_password varchar(255) NOT NULL,
	user_name varchar(255) NOT NULL,
	user_role varchar(255) NOT NULL,
	user_enabled bool NOT NULL,
	CONSTRAINT users_pkey PRIMARY KEY (user_id)
);

CREATE TABLE roles (
	role_id serial4 NOT NULL,
	role_name varchar(255) NOT NULL,
	role_description varchar(255) NOT NULL,
	CONSTRAINT roles_pkey PRIMARY KEY (role_id),
	CONSTRAINT roles_role_name_key UNIQUE (role_name)
);

CREATE TABLE user_roles (
	user_role_id serial4 NOT NULL,
	user_id int4 NOT NULL,
	role_id int4 NOT NULL,
	CONSTRAINT user_roles_pkey PRIMARY KEY (user_role_id),
	CONSTRAINT user_roles_role_id_fkey FOREIGN KEY (role_id) REFERENCES roles(role_id),
	CONSTRAINT user_roles_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Вставка данных в таблицу roles
INSERT INTO public.roles (role_name, role_description) VALUES
  ('administrator', 'Администратор'),
  ('manager', 'Менеджер'),
  ('user', 'Пользователь');

-- Вставка данных в таблицу users
INSERT INTO public.users (login, user_password, user_name, user_role, user_enabled) VALUES
  ('admin', 'adminpass', 'Админ', 'administrator', true);
  -- Вставка данных в таблицу users
  INSERT INTO public.users (login, user_password, user_name, user_role, user_enabled) VALUES
    ('login1', 'pass1', 'user1', 'moderator', true);
  -- Вставка данных в таблицу users
  INSERT INTO public.users (login, user_password, user_name, user_role, user_enabled) VALUES
    ('login2', 'pass2', 'user2', 'user', true);

-- Вставка данных в таблицу user_roles
INSERT INTO public.user_roles (user_id, role_id)
VALUES (
  (SELECT user_id FROM public.users WHERE login = 'admin'),
  (SELECT role_id FROM public.roles WHERE role_name = 'administrator')
);
-- Вставка данных в таблицу user_roles
INSERT INTO public.user_roles (user_id, role_id)
VALUES (
  (SELECT user_id FROM public.users WHERE login = 'login1'),
  (SELECT role_id FROM public.roles WHERE role_name = 'moderator')
);
-- Вставка данных в таблицу user_roles
INSERT INTO public.user_roles (user_id, role_id)
VALUES (
  (SELECT user_id FROM public.users WHERE login = 'login2'),
  (SELECT role_id FROM public.roles WHERE role_name = 'user')
);