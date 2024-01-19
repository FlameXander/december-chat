--
-- PostgreSQL database dump
--

-- Dumped from database version 16.0
-- Dumped by pg_dump version 16.0

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: public; Type: SCHEMA; Schema: -; Owner: user_hw18
--

-- *not* creating schema, since initdb creates it


ALTER SCHEMA public OWNER TO user_hw18;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: roles; Type: TABLE; Schema: public; Owner: user_hw18
--

CREATE TABLE public.roles (
    role_id integer NOT NULL,
    role_name character varying(255) NOT NULL,
    role_description character varying(255) NOT NULL
);


ALTER TABLE public.roles OWNER TO user_hw18;

--
-- Name: roles_role_id_seq; Type: SEQUENCE; Schema: public; Owner: user_hw18
--

CREATE SEQUENCE public.roles_role_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.roles_role_id_seq OWNER TO user_hw18;

--
-- Name: roles_role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: user_hw18
--

ALTER SEQUENCE public.roles_role_id_seq OWNED BY public.roles.role_id;


--
-- Name: user_roles; Type: TABLE; Schema: public; Owner: user_hw18
--

CREATE TABLE public.user_roles (
    user_role_id integer NOT NULL,
    user_id integer,
    role_id integer
);


ALTER TABLE public.user_roles OWNER TO user_hw18;

--
-- Name: user_roles_user_role_id_seq; Type: SEQUENCE; Schema: public; Owner: user_hw18
--

CREATE SEQUENCE public.user_roles_user_role_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.user_roles_user_role_id_seq OWNER TO user_hw18;

--
-- Name: user_roles_user_role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: user_hw18
--

ALTER SEQUENCE public.user_roles_user_role_id_seq OWNED BY public.user_roles.user_role_id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: user_hw18
--

CREATE TABLE public.users (
    user_id integer NOT NULL,
    login character varying(255) NOT NULL,
    user_password character varying(255) NOT NULL,
    user_name character varying(255) NOT NULL,
    user_enabled boolean NOT NULL
);


ALTER TABLE public.users OWNER TO user_hw18;

--
-- Name: users_user_id_seq; Type: SEQUENCE; Schema: public; Owner: user_hw18
--

CREATE SEQUENCE public.users_user_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_user_id_seq OWNER TO user_hw18;

--
-- Name: users_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: user_hw18
--

ALTER SEQUENCE public.users_user_id_seq OWNED BY public.users.user_id;


--
-- Name: roles role_id; Type: DEFAULT; Schema: public; Owner: user_hw18
--

ALTER TABLE ONLY public.roles ALTER COLUMN role_id SET DEFAULT nextval('public.roles_role_id_seq'::regclass);


--
-- Name: user_roles user_role_id; Type: DEFAULT; Schema: public; Owner: user_hw18
--

ALTER TABLE ONLY public.user_roles ALTER COLUMN user_role_id SET DEFAULT nextval('public.user_roles_user_role_id_seq'::regclass);


--
-- Name: users user_id; Type: DEFAULT; Schema: public; Owner: user_hw18
--

ALTER TABLE ONLY public.users ALTER COLUMN user_id SET DEFAULT nextval('public.users_user_id_seq'::regclass);


--
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: user_hw18
--

COPY public.roles (role_id, role_name, role_description) FROM stdin;
1	administrator	Администратор
3	user	Пользователь
2	moderator	Модератор
\.


--
-- Data for Name: user_roles; Type: TABLE DATA; Schema: public; Owner: user_hw18
--

COPY public.user_roles (user_role_id, user_id, role_id) FROM stdin;
1	1	1
2	2	2
3	3	3
4	7	3
5	8	3
6	9	3
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: user_hw18
--

COPY public.users (user_id, login, user_password, user_name, user_enabled) FROM stdin;
1	admin	adminpass	Админ	t
2	login1	pass1	user1	t
3	login2	pass2	user2	t
8	login4	pass4	user4	t
7	login3	pass3	user3	f
9	login5	pass5	user5	f
\.


--
-- Name: roles_role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: user_hw18
--

SELECT pg_catalog.setval('public.roles_role_id_seq', 3, true);


--
-- Name: user_roles_user_role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: user_hw18
--

SELECT pg_catalog.setval('public.user_roles_user_role_id_seq', 6, true);


--
-- Name: users_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: user_hw18
--

SELECT pg_catalog.setval('public.users_user_id_seq', 9, true);


--
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: public; Owner: user_hw18
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (role_id);


--
-- Name: roles roles_role_name_key; Type: CONSTRAINT; Schema: public; Owner: user_hw18
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_role_name_key UNIQUE (role_name);


--
-- Name: user_roles user_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: user_hw18
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT user_roles_pkey PRIMARY KEY (user_role_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: user_hw18
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- Name: user_roles user_roles_role_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: user_hw18
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT user_roles_role_id_fkey FOREIGN KEY (role_id) REFERENCES public.roles(role_id);


--
-- Name: user_roles user_roles_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: user_hw18
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT user_roles_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id);


--
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: public; Owner: user_hw18
--

ALTER DEFAULT PRIVILEGES FOR ROLE user_hw18 IN SCHEMA public GRANT ALL ON TABLES TO user_hw18;


--
-- PostgreSQL database dump complete
--

