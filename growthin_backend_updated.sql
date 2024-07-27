-- Database generated with pgModeler (PostgreSQL Database Modeler).
-- pgModeler version: 0.9.4
-- PostgreSQL version: 13.0
-- Project Site: pgmodeler.io
-- Model Author: ---

-- Database creation must be performed outside a multi lined SQL file. 
-- These commands were put in this file only as a convenience.
-- 
-- object: growthin | type: DATABASE --
-- DROP DATABASE IF EXISTS growthin;
CREATE DATABASE growthin;
-- ddl-end --


-- object: public.users_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.users_id_seq CASCADE;
CREATE SEQUENCE public.users_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 0
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --

-- object: public.users | type: TABLE --
-- DROP TABLE IF EXISTS public.users CASCADE;
CREATE TABLE public.users (
	id bigint NOT NULL DEFAULT nextval('public.users_id_seq'::regclass),
	username varchar(25) NOT NULL,
	email varchar(255) NOT NULL,
	first_name varchar(255) NOT NULL,
	last_name varchar(255) NOT NULL,
	phone varchar(255),
	country varchar(100),
	area varchar(255),
	is_admin boolean NOT NULL DEFAULT false,
	created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp with time zone,
	CONSTRAINT users_pk PRIMARY KEY (id),
	CONSTRAINT unique_username UNIQUE (username),
	CONSTRAINT unique_email UNIQUE (email)
);
-- ddl-end --
ALTER TABLE public.users OWNER TO postgres;
-- ddl-end --

-- object: public.user_profiles | type: TABLE --
-- DROP TABLE IF EXISTS public.user_profiles CASCADE;
CREATE TABLE public.user_profiles (
	user_id bigint NOT NULL,
	job_field text,
	profile_pic_id bigint,
	description text,
	age smallint,
	education text,
	country varchar(50),
	cv_document_id bigint,
	created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp with time zone,
	CONSTRAINT user_profiles_pk PRIMARY KEY (user_id),
	CONSTRAINT user_profile_id_uniqueness UNIQUE (user_id),
	CONSTRAINT cv_uniqueness UNIQUE (cv_document_id)
);
-- ddl-end --
ALTER TABLE public.user_profiles OWNER TO postgres;
-- ddl-end --

-- object: username_index | type: INDEX --
-- DROP INDEX IF EXISTS public.username_index CASCADE;
CREATE INDEX username_index ON public.users
USING btree
(
	username
);
-- ddl-end --

-- object: email_index | type: INDEX --
-- DROP INDEX IF EXISTS public.email_index CASCADE;
CREATE INDEX email_index ON public.users
USING btree
(
	email
);
-- ddl-end --

-- object: public.job_field_keywords_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.job_field_keywords_id_seq CASCADE;
CREATE SEQUENCE public.job_field_keywords_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 0
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --

-- object: public.job_field_keywords | type: TABLE --
-- DROP TABLE IF EXISTS public.job_field_keywords CASCADE;
CREATE TABLE public.job_field_keywords (
	id bigint NOT NULL DEFAULT nextval('public.job_field_keywords_id_seq'::regclass),
	name varchar(255) NOT NULL,
	CONSTRAINT job_field_keywords_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.job_field_keywords OWNER TO postgres;
-- ddl-end --

-- object: public.articles_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.articles_id_seq CASCADE;
CREATE SEQUENCE public.articles_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 0
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --

-- object: public."PublicStatus" | type: TYPE --
-- DROP TYPE IF EXISTS public."PublicStatus" CASCADE;
CREATE TYPE public."PublicStatus" AS
ENUM ('PUBLIC','CONNECTED_NETWORK','HIDDEN');
-- ddl-end --
ALTER TYPE public."PublicStatus" OWNER TO postgres;
-- ddl-end --

-- object: public.comments_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.comments_id_seq CASCADE;
CREATE SEQUENCE public.comments_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 0
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --

-- object: public.comments | type: TABLE --
-- DROP TABLE IF EXISTS public.comments CASCADE;
CREATE TABLE public.comments (
	id bigint NOT NULL DEFAULT nextval('public.comments_id_seq'::regclass),
	id_articles bigint NOT NULL,
	id_users bigint NOT NULL,
	body text NOT NULL,
	created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp with time zone,
	is_deleted boolean NOT NULL DEFAULT false,
	CONSTRAINT comments_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.comments OWNER TO postgres;
-- ddl-end --

-- object: public.media_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.media_id_seq CASCADE;
CREATE SEQUENCE public.media_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 0
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --

-- object: public.media | type: TABLE --
-- DROP TABLE IF EXISTS public.media CASCADE;
CREATE TABLE public.media (
	id bigint NOT NULL DEFAULT nextval('public.media_id_seq'::regclass),
	id_users bigint NOT NULL,
	id_media_types smallint NOT NULL,
	data bytea NOT NULL,
	created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
	is_deleted boolean DEFAULT false,
	CONSTRAINT media_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.media OWNER TO postgres;
-- ddl-end --

-- object: public.media_types | type: TABLE --
-- DROP TABLE IF EXISTS public.media_types CASCADE;
CREATE TABLE public.media_types (
	id smallint NOT NULL,
	name varchar(255) NOT NULL,
	CONSTRAINT media_types_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.media_types OWNER TO postgres;
-- ddl-end --

-- object: public.user_comments_reactions_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.user_comments_reactions_id_seq CASCADE;
CREATE SEQUENCE public.user_comments_reactions_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 0
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --
ALTER SEQUENCE public.user_comments_reactions_id_seq OWNER TO postgres;
-- ddl-end --

-- object: public.user_comments_reactions | type: TABLE --
-- DROP TABLE IF EXISTS public.user_comments_reactions CASCADE;
CREATE TABLE public.user_comments_reactions (
	id bigint NOT NULL DEFAULT nextval('public.user_comments_reactions_id_seq'::regclass),
	id_users bigint NOT NULL,
	id_comments bigint NOT NULL,
	id_reactions integer NOT NULL,
	created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp with time zone,
	CONSTRAINT user_comments_reactions_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.user_comments_reactions OWNER TO postgres;
-- ddl-end --

-- object: public.reactions_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.reactions_id_seq CASCADE;
CREATE SEQUENCE public.reactions_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 0
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --
ALTER SEQUENCE public.reactions_id_seq OWNER TO postgres;
-- ddl-end --

-- object: public.reactions | type: TABLE --
-- DROP TABLE IF EXISTS public.reactions CASCADE;
CREATE TABLE public.reactions (
	id integer NOT NULL DEFAULT nextval('public.reactions_id_seq'::regclass),
	alias varchar(30) NOT NULL DEFAULT '',
	image bytea NOT NULL,
	created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT reactions_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.reactions OWNER TO postgres;
-- ddl-end --

-- object: public.user_articles_reactions_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.user_articles_reactions_id_seq CASCADE;
CREATE SEQUENCE public.user_articles_reactions_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 0
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --
ALTER SEQUENCE public.user_articles_reactions_id_seq OWNER TO postgres;
-- ddl-end --

-- object: public.user_articles_reactions | type: TABLE --
-- DROP TABLE IF EXISTS public.user_articles_reactions CASCADE;
CREATE TABLE public.user_articles_reactions (
	id bigint NOT NULL DEFAULT nextval('public.user_articles_reactions_id_seq'::regclass),
	id_articles bigint NOT NULL,
	id_users bigint NOT NULL,
	id_reactions integer NOT NULL,
	created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp with time zone,
	CONSTRAINT user_articles_reactions_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.user_articles_reactions OWNER TO postgres;
-- ddl-end --

-- object: public.user_connection_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.user_connection_id_seq CASCADE;
CREATE SEQUENCE public.user_connection_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 0
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --
ALTER SEQUENCE public.user_connection_id_seq OWNER TO postgres;
-- ddl-end --

-- object: public.user_connections | type: TABLE --
-- DROP TABLE IF EXISTS public.user_connections CASCADE;
CREATE TABLE public.user_connections (
	id bigint NOT NULL DEFAULT nextval('public.user_connection_id_seq'::regclass),
	user_id bigint NOT NULL,
	connected_user_id bigint NOT NULL,
	created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT user_connections_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.user_connections OWNER TO postgres;
-- ddl-end --

-- object: public.user_connection_request_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.user_connection_request_id_seq CASCADE;
CREATE SEQUENCE public.user_connection_request_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 0
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --
ALTER SEQUENCE public.user_connection_request_id_seq OWNER TO postgres;
-- ddl-end --

-- object: public."UserConnectionRequestStatus" | type: TYPE --
-- DROP TYPE IF EXISTS public."UserConnectionRequestStatus" CASCADE;
CREATE TYPE public."UserConnectionRequestStatus" AS
ENUM ('PENDING','ACCEPTED','DECLINED');
-- ddl-end --
ALTER TYPE public."UserConnectionRequestStatus" OWNER TO postgres;
-- ddl-end --

-- object: public.user_job_field_keywords | type: TABLE --
-- DROP TABLE IF EXISTS public.user_job_field_keywords CASCADE;
CREATE TABLE public.user_job_field_keywords (
	id_users bigint NOT NULL,
	id_job_field_keywords bigint NOT NULL

);
-- ddl-end --
ALTER TABLE public.user_job_field_keywords OWNER TO postgres;
-- ddl-end --

-- object: public.articles_media_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.articles_media_seq CASCADE;
CREATE SEQUENCE public.articles_media_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 0
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --
ALTER SEQUENCE public.articles_media_seq OWNER TO postgres;
-- ddl-end --

-- object: public.articles_media | type: TABLE --
-- DROP TABLE IF EXISTS public.articles_media CASCADE;
CREATE TABLE public.articles_media (
	id bigint NOT NULL DEFAULT nextval('public.articles_media_seq'::regclass),
	id_articles bigint NOT NULL,
	id_media bigint,
	"order" integer NOT NULL DEFAULT 0,
	created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT articles_media_pk PRIMARY KEY (id)
);
-- ddl-end --
COMMENT ON COLUMN public.articles_media."order" IS E'order of user media (to display in the order of user upload)';
-- ddl-end --
ALTER TABLE public.articles_media OWNER TO postgres;
-- ddl-end --

-- object: public.user_action_types | type: TABLE --
-- DROP TABLE IF EXISTS public.user_action_types CASCADE;
CREATE TABLE public.user_action_types (
	id smallint NOT NULL,
	type varchar(25) NOT NULL,
	CONSTRAINT user_action_types_pk PRIMARY KEY (id)
);
-- ddl-end --
COMMENT ON COLUMN public.user_action_types.type IS E'type of user action (ex. POST_ARTICLE, REACTION, CONNECTION..\n)';
-- ddl-end --
ALTER TABLE public.user_action_types OWNER TO postgres;
-- ddl-end --

-- object: public.user_history_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.user_history_id_seq CASCADE;
CREATE SEQUENCE public.user_history_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 0
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --
ALTER SEQUENCE public.user_history_id_seq OWNER TO postgres;
-- ddl-end --

-- object: public.user_history | type: TABLE --
-- DROP TABLE IF EXISTS public.user_history CASCADE;
CREATE TABLE public.user_history (
	id bigint NOT NULL DEFAULT nextval('public.user_history_id_seq'::regclass),
	id_users bigint NOT NULL,
	id_user_action_types smallint NOT NULL,
	is_article boolean NOT NULL DEFAULT false,
	id_articles bigint,
	is_comment boolean NOT NULL DEFAULT false,
	id_comments bigint,
	is_connection_request boolean NOT NULL DEFAULT false,
	id_user_connection_requests bigint,
	is_job_ad boolean NOT NULL DEFAULT false,
	id_job_ads bigint,
	is_comment_reaction boolean NOT NULL DEFAULT false,
	id_user_comments_reactions bigint,
	is_article_reaction boolean NOT NULL DEFAULT false,
	id_user_articles_reactions bigint,
    created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp with time zone,
	CONSTRAINT user_history_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.user_history OWNER TO postgres;
-- ddl-end --

-- object: user_action_types_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_history DROP CONSTRAINT IF EXISTS user_action_types_fk CASCADE;
ALTER TABLE public.user_history ADD CONSTRAINT user_action_types_fk FOREIGN KEY (id_user_action_types)
REFERENCES public.user_action_types (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: users_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_articles_reactions DROP CONSTRAINT IF EXISTS users_fk CASCADE;
ALTER TABLE public.user_articles_reactions ADD CONSTRAINT users_fk FOREIGN KEY (id_users)
REFERENCES public.users (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: users_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_history DROP CONSTRAINT IF EXISTS users_fk CASCADE;
ALTER TABLE public.user_history ADD CONSTRAINT users_fk FOREIGN KEY (id_users)
REFERENCES public.users (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: public.job_ads_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.job_ads_id_seq CASCADE;
CREATE SEQUENCE public.job_ads_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --
ALTER SEQUENCE public.job_ads_id_seq OWNER TO postgres;
-- ddl-end --

-- object: public.job_ads | type: TABLE --
-- DROP TABLE IF EXISTS public.job_ads CASCADE;
CREATE TABLE public.job_ads (
	id bigint NOT NULL DEFAULT nextval('public.job_ads_id_seq'::regclass),
	id_users bigint NOT NULL,
	title varchar(100) NOT NULL,
	description text NOT NULL,
	created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp with time zone,
	is_active boolean NOT NULL DEFAULT true,
	public_status public."PublicStatus" NOT NULL DEFAULT 'PUBLIC',
	is_deleted boolean DEFAULT false,
	CONSTRAINT job_ads_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.job_ads OWNER TO postgres;
-- ddl-end --

-- object: public.job_ads_keywords | type: TABLE --
-- DROP TABLE IF EXISTS public.job_ads_keywords CASCADE;
CREATE TABLE public.job_ads_keywords (
	id_job_field_keywords bigint NOT NULL,
	id_job_ads bigint NOT NULL

);
-- ddl-end --
ALTER TABLE public.job_ads_keywords OWNER TO postgres;
-- ddl-end --

-- object: job_field_keywords_fk | type: CONSTRAINT --
-- ALTER TABLE public.job_ads_keywords DROP CONSTRAINT IF EXISTS job_field_keywords_fk CASCADE;
ALTER TABLE public.job_ads_keywords ADD CONSTRAINT job_field_keywords_fk FOREIGN KEY (id_job_field_keywords)
REFERENCES public.job_field_keywords (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: users_fk | type: CONSTRAINT --
-- ALTER TABLE public.job_ads DROP CONSTRAINT IF EXISTS users_fk CASCADE;
ALTER TABLE public.job_ads ADD CONSTRAINT users_fk FOREIGN KEY (id_users)
REFERENCES public.users (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: job_ads_fk | type: CONSTRAINT --
-- ALTER TABLE public.job_ads_keywords DROP CONSTRAINT IF EXISTS job_ads_fk CASCADE;
ALTER TABLE public.job_ads_keywords ADD CONSTRAINT job_ads_fk FOREIGN KEY (id_job_ads)
REFERENCES public.job_ads (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: job_ads_id_index | type: INDEX --
-- DROP INDEX IF EXISTS public.job_ads_id_index CASCADE;
CREATE INDEX job_ads_id_index ON public.job_ads_keywords
USING btree
(
	id_job_ads
);
-- ddl-end --

-- object: public.job_ads_applicants | type: TABLE --
-- DROP TABLE IF EXISTS public.job_ads_applicants CASCADE;
CREATE TABLE public.job_ads_applicants (
	id_job_ads bigint NOT NULL,
	id_users bigint NOT NULL,
	cv_media_id integer,
	created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP

);
-- ddl-end --
ALTER TABLE public.job_ads_applicants OWNER TO postgres;
-- ddl-end --

-- object: job_ads_fk | type: CONSTRAINT --
-- ALTER TABLE public.job_ads_applicants DROP CONSTRAINT IF EXISTS job_ads_fk CASCADE;
ALTER TABLE public.job_ads_applicants ADD CONSTRAINT job_ads_fk FOREIGN KEY (id_job_ads)
REFERENCES public.job_ads (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: users_fk | type: CONSTRAINT --
-- ALTER TABLE public.job_ads_applicants DROP CONSTRAINT IF EXISTS users_fk CASCADE;
ALTER TABLE public.job_ads_applicants ADD CONSTRAINT users_fk FOREIGN KEY (id_users)
REFERENCES public.users (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: id_job_ads_index | type: INDEX --
-- DROP INDEX IF EXISTS public.id_job_ads_index CASCADE;
CREATE INDEX id_job_ads_index ON public.job_ads_applicants
USING btree
(
	id_job_ads
);
-- ddl-end --

-- object: public.articles | type: TABLE --
-- DROP TABLE IF EXISTS public.articles CASCADE;
CREATE TABLE public.articles (
	id bigint NOT NULL DEFAULT nextval('public.articles_id_seq'::regclass),
	id_users bigint NOT NULL,
	title varchar(512),
	body text NOT NULL,
	public_status public."PublicStatus" NOT NULL DEFAULT 'PUBLIC',
	created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp with time zone,
	is_deleted boolean NOT NULL DEFAULT false,
	CONSTRAINT articles_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.articles OWNER TO postgres;
-- ddl-end --

-- object: articles_fk | type: CONSTRAINT --
-- ALTER TABLE public.articles_media DROP CONSTRAINT IF EXISTS articles_fk CASCADE;
ALTER TABLE public.articles_media ADD CONSTRAINT articles_fk FOREIGN KEY (id_articles)
REFERENCES public.articles (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: media_types_fk | type: CONSTRAINT --
-- ALTER TABLE public.media DROP CONSTRAINT IF EXISTS media_types_fk CASCADE;
ALTER TABLE public.media ADD CONSTRAINT media_types_fk FOREIGN KEY (id_media_types)
REFERENCES public.media_types (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: users_fk | type: CONSTRAINT --
-- ALTER TABLE public.media DROP CONSTRAINT IF EXISTS users_fk CASCADE;
ALTER TABLE public.media ADD CONSTRAINT users_fk FOREIGN KEY (id_users)
REFERENCES public.users (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: media_fk | type: CONSTRAINT --
-- ALTER TABLE public.articles_media DROP CONSTRAINT IF EXISTS media_fk CASCADE;
ALTER TABLE public.articles_media ADD CONSTRAINT media_fk FOREIGN KEY (id_media)
REFERENCES public.media (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: articles_fk | type: CONSTRAINT --
-- ALTER TABLE public.comments DROP CONSTRAINT IF EXISTS articles_fk CASCADE;
ALTER TABLE public.comments ADD CONSTRAINT articles_fk FOREIGN KEY (id_articles)
REFERENCES public.articles (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: users_fk | type: CONSTRAINT --
-- ALTER TABLE public.comments DROP CONSTRAINT IF EXISTS users_fk CASCADE;
ALTER TABLE public.comments ADD CONSTRAINT users_fk FOREIGN KEY (id_users)
REFERENCES public.users (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: comments_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_comments_reactions DROP CONSTRAINT IF EXISTS comments_fk CASCADE;
ALTER TABLE public.user_comments_reactions ADD CONSTRAINT comments_fk FOREIGN KEY (id_comments)
REFERENCES public.comments (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: users_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_comments_reactions DROP CONSTRAINT IF EXISTS users_fk CASCADE;
ALTER TABLE public.user_comments_reactions ADD CONSTRAINT users_fk FOREIGN KEY (id_users)
REFERENCES public.users (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: reactions_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_comments_reactions DROP CONSTRAINT IF EXISTS reactions_fk CASCADE;
ALTER TABLE public.user_comments_reactions ADD CONSTRAINT reactions_fk FOREIGN KEY (id_reactions)
REFERENCES public.reactions (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: reactions_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_articles_reactions DROP CONSTRAINT IF EXISTS reactions_fk CASCADE;
ALTER TABLE public.user_articles_reactions ADD CONSTRAINT reactions_fk FOREIGN KEY (id_reactions)
REFERENCES public.reactions (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: articles_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_articles_reactions DROP CONSTRAINT IF EXISTS articles_fk CASCADE;
ALTER TABLE public.user_articles_reactions ADD CONSTRAINT articles_fk FOREIGN KEY (id_articles)
REFERENCES public.articles (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: user_id_reaction_id_articles_id_uniqueness | type: CONSTRAINT --
-- ALTER TABLE public.user_articles_reactions DROP CONSTRAINT IF EXISTS user_id_reaction_id_articles_id_uniqueness CASCADE;
ALTER TABLE public.user_articles_reactions ADD CONSTRAINT user_id_reaction_id_articles_id_uniqueness UNIQUE (id_articles,id_users);
-- ddl-end --
COMMENT ON CONSTRAINT user_id_reaction_id_articles_id_uniqueness ON public.user_articles_reactions  IS E'One reaction per user per article';
-- ddl-end --


-- object: users_id_reactions_id_comments_id_uq | type: CONSTRAINT --
-- ALTER TABLE public.user_comments_reactions DROP CONSTRAINT IF EXISTS users_id_reactions_id_comments_id_uq CASCADE;
ALTER TABLE public.user_comments_reactions ADD CONSTRAINT users_id_reactions_id_comments_id_uq UNIQUE (id_users,id_comments);
-- ddl-end --
COMMENT ON CONSTRAINT users_id_reactions_id_comments_id_uq ON public.user_comments_reactions  IS E'one reaction per user per comment';
-- ddl-end --


-- object: users_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_connections DROP CONSTRAINT IF EXISTS users_fk CASCADE;
ALTER TABLE public.user_connections ADD CONSTRAINT users_fk FOREIGN KEY (user_id)
REFERENCES public.users (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: public.user_connection_requests | type: TABLE --
-- DROP TABLE IF EXISTS public.user_connection_requests CASCADE;
CREATE TABLE public.user_connection_requests (
	id bigint NOT NULL DEFAULT nextval('public.user_connection_request_id_seq'::regclass),
	user_id bigint NOT NULL,
	connected_user_id bigint NOT NULL,
	status public."UserConnectionRequestStatus" NOT NULL DEFAULT 'PENDING',
	created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT user_connection_requests_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.user_connection_requests OWNER TO postgres;
-- ddl-end --

-- object: articles_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_history DROP CONSTRAINT IF EXISTS articles_fk CASCADE;
ALTER TABLE public.user_history ADD CONSTRAINT articles_fk FOREIGN KEY (id_articles)
REFERENCES public.articles (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: comments_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_history DROP CONSTRAINT IF EXISTS comments_fk CASCADE;
ALTER TABLE public.user_history ADD CONSTRAINT comments_fk FOREIGN KEY (id_comments)
REFERENCES public.comments (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: job_ads_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_history DROP CONSTRAINT IF EXISTS job_ads_fk CASCADE;
ALTER TABLE public.user_history ADD CONSTRAINT job_ads_fk FOREIGN KEY (id_job_ads)
REFERENCES public.job_ads (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: user_connection_requests_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_history DROP CONSTRAINT IF EXISTS user_connection_requests_fk CASCADE;
ALTER TABLE public.user_history ADD CONSTRAINT user_connection_requests_fk FOREIGN KEY (id_user_connection_requests)
REFERENCES public.user_connection_requests (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: users_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_job_field_keywords DROP CONSTRAINT IF EXISTS users_fk CASCADE;
ALTER TABLE public.user_job_field_keywords ADD CONSTRAINT users_fk FOREIGN KEY (id_users)
REFERENCES public.users (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: job_field_keywords_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_job_field_keywords DROP CONSTRAINT IF EXISTS job_field_keywords_fk CASCADE;
ALTER TABLE public.user_job_field_keywords ADD CONSTRAINT job_field_keywords_fk FOREIGN KEY (id_job_field_keywords)
REFERENCES public.job_field_keywords (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: public.notifications | type: TABLE --
-- DROP TABLE IF EXISTS public.notifications CASCADE;
CREATE TABLE public.notifications (
	id bigint NOT NULL,
	user_id bigint NOT NULL,
	recipient_id bigint NOT NULL,
	notification_type_id integer NOT NULL,
	status smallint NOT NULL DEFAULT 0,
	viewed boolean NOT NULL DEFAULT false,
	is_article boolean NOT NULL DEFAULT false,
	id_articles bigint,
	is_comment boolean NOT NULL DEFAULT false,
	id_comments bigint,
	is_connection_request boolean NOT NULL DEFAULT false,
	id_user_connection_requests bigint,
	is_job_ad boolean NOT NULL DEFAULT false,
	id_job_ads bigint,
	is_article_reaction boolean DEFAULT false,
	id_user_articles_reactions bigint,
	is_comment_reaction boolean NOT NULL DEFAULT false,
	id_user_comments_reactions bigint,
	is_chat boolean NOT NULL DEFAULT false,
	id_chat_rooms bigint,
	created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT notification_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.notifications OWNER TO postgres;
-- ddl-end --

-- object: public.notification_types | type: TABLE --
-- DROP TABLE IF EXISTS public.notification_types CASCADE;
CREATE TABLE public.notification_types (
	id integer NOT NULL,
	name varchar(100) NOT NULL,
	CONSTRAINT notification_types_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.notification_types OWNER TO postgres;
-- ddl-end --

-- object: notification_types_fk | type: CONSTRAINT --
-- ALTER TABLE public.notifications DROP CONSTRAINT IF EXISTS notification_types_fk CASCADE;
ALTER TABLE public.notifications ADD CONSTRAINT notification_types_fk FOREIGN KEY (notification_type_id)
REFERENCES public.notification_types (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: public.notification_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.notification_id_seq CASCADE;
CREATE SEQUENCE public.notification_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 0
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --
ALTER SEQUENCE public.notification_id_seq OWNER TO postgres;
-- ddl-end --

-- object: user_articles_reactions_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_history DROP CONSTRAINT IF EXISTS user_articles_reactions_fk CASCADE;
ALTER TABLE public.user_history ADD CONSTRAINT user_articles_reactions_fk FOREIGN KEY (id_user_articles_reactions)
REFERENCES public.user_articles_reactions (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: user_comments_reactions_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_history DROP CONSTRAINT IF EXISTS user_comments_reactions_fk CASCADE;
ALTER TABLE public.user_history ADD CONSTRAINT user_comments_reactions_fk FOREIGN KEY (id_user_comments_reactions)
REFERENCES public.user_comments_reactions (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: user_articles_reactions_fk | type: CONSTRAINT --
-- ALTER TABLE public.notifications DROP CONSTRAINT IF EXISTS user_articles_reactions_fk CASCADE;
ALTER TABLE public.notifications ADD CONSTRAINT user_articles_reactions_fk FOREIGN KEY (id_user_articles_reactions)
REFERENCES public.user_articles_reactions (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: user_comments_reactions_fk | type: CONSTRAINT --
-- ALTER TABLE public.notifications DROP CONSTRAINT IF EXISTS user_comments_reactions_fk CASCADE;
ALTER TABLE public.notifications ADD CONSTRAINT user_comments_reactions_fk FOREIGN KEY (id_user_comments_reactions)
REFERENCES public.user_comments_reactions (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: comments_fk | type: CONSTRAINT --
-- ALTER TABLE public.notifications DROP CONSTRAINT IF EXISTS comments_fk CASCADE;
ALTER TABLE public.notifications ADD CONSTRAINT comments_fk FOREIGN KEY (id_comments)
REFERENCES public.comments (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: articles_fk | type: CONSTRAINT --
-- ALTER TABLE public.notifications DROP CONSTRAINT IF EXISTS articles_fk CASCADE;
ALTER TABLE public.notifications ADD CONSTRAINT articles_fk FOREIGN KEY (id_articles)
REFERENCES public.articles (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: job_ads_fk | type: CONSTRAINT --
-- ALTER TABLE public.notifications DROP CONSTRAINT IF EXISTS job_ads_fk CASCADE;
ALTER TABLE public.notifications ADD CONSTRAINT job_ads_fk FOREIGN KEY (id_job_ads)
REFERENCES public.job_ads (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: user_connection_requests_fk | type: CONSTRAINT --
-- ALTER TABLE public.notifications DROP CONSTRAINT IF EXISTS user_connection_requests_fk CASCADE;
ALTER TABLE public.notifications ADD CONSTRAINT user_connection_requests_fk FOREIGN KEY (id_user_connection_requests)
REFERENCES public.user_connection_requests (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: public.chat_room_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.chat_room_seq CASCADE;
CREATE SEQUENCE public.chat_room_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 0
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --
ALTER SEQUENCE public.chat_room_seq OWNER TO postgres;
-- ddl-end --

-- object: public.chat_rooms | type: TABLE --
-- DROP TABLE IF EXISTS public.chat_rooms CASCADE;
CREATE TABLE public.chat_rooms (
	id bigint NOT NULL DEFAULT nextval('public.chat_room_seq'::regclass),
	related_user_id1 bigint NOT NULL,
	related_user_id2 bigint NOT NULL,
	created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT chat_rooms_pk PRIMARY KEY (id),
	CONSTRAINT user_chatroom_uniqueness UNIQUE (related_user_id1,related_user_id2)
);
-- ddl-end --
COMMENT ON CONSTRAINT user_chatroom_uniqueness ON public.chat_rooms  IS E'2 related users can have only 1 chatroom';
-- ddl-end --
ALTER TABLE public.chat_rooms OWNER TO postgres;
-- ddl-end --

-- object: public.chat_messages_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.chat_messages_seq CASCADE;
CREATE SEQUENCE public.chat_messages_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 0
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --
ALTER SEQUENCE public.chat_messages_seq OWNER TO postgres;
-- ddl-end --

-- object: public.chat_rooms_data | type: TABLE --
-- DROP TABLE IF EXISTS public.chat_rooms_data CASCADE;
CREATE TABLE public.chat_rooms_data (
	id bigint NOT NULL DEFAULT nextval('public.chat_messages_seq'::regclass),
	id_chat_rooms bigint NOT NULL,
	sender_id bigint NOT NULL,
	message text NOT NULL,
	is_read boolean NOT NULL DEFAULT false,
	id_media bigint,
	created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp with time zone,
	CONSTRAINT chat_rooms_data_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.chat_rooms_data OWNER TO postgres;
-- ddl-end --

-- object: chat_rooms_fk | type: CONSTRAINT --
-- ALTER TABLE public.chat_rooms_data DROP CONSTRAINT IF EXISTS chat_rooms_fk CASCADE;
ALTER TABLE public.chat_rooms_data ADD CONSTRAINT chat_rooms_fk FOREIGN KEY (id_chat_rooms)
REFERENCES public.chat_rooms (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: chat_rooms_index | type: INDEX --
-- DROP INDEX IF EXISTS public.chat_rooms_index CASCADE;
CREATE INDEX chat_rooms_index ON public.chat_rooms_data
USING btree
(
	id_chat_rooms
);
-- ddl-end --

-- object: media_fk | type: CONSTRAINT --
-- ALTER TABLE public.chat_rooms_data DROP CONSTRAINT IF EXISTS media_fk CASCADE;
ALTER TABLE public.chat_rooms_data ADD CONSTRAINT media_fk FOREIGN KEY (id_media)
REFERENCES public.media (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: chat_rooms_fk | type: CONSTRAINT --
-- ALTER TABLE public.notifications DROP CONSTRAINT IF EXISTS chat_rooms_fk CASCADE;
ALTER TABLE public.notifications ADD CONSTRAINT chat_rooms_fk FOREIGN KEY (id_chat_rooms)
REFERENCES public.chat_rooms (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: job_ad_id_user_id_unique | type: CONSTRAINT --
-- ALTER TABLE public.job_ads_applicants DROP CONSTRAINT IF EXISTS job_ad_id_user_id_unique CASCADE;
ALTER TABLE public.job_ads_applicants ADD CONSTRAINT job_ad_id_user_id_unique UNIQUE (id_job_ads,id_users);
-- ddl-end --
COMMENT ON CONSTRAINT job_ad_id_user_id_unique ON public.job_ads_applicants  IS E'one application per user per job';
-- ddl-end --


-- object: users_fk | type: CONSTRAINT --
-- ALTER TABLE public.articles DROP CONSTRAINT IF EXISTS users_fk CASCADE;
ALTER TABLE public.articles ADD CONSTRAINT users_fk FOREIGN KEY (id_users)
REFERENCES public.users (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: public.admin_requests_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.admin_requests_seq CASCADE;
CREATE SEQUENCE public.admin_requests_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 0
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --
ALTER SEQUENCE public.admin_requests_seq OWNER TO postgres;
-- ddl-end --

-- object: public."AdminRequestStatus" | type: TYPE --
-- DROP TYPE IF EXISTS public."AdminRequestStatus" CASCADE;
CREATE TYPE public."AdminRequestStatus" AS
ENUM ('PENDING','ACCEPTED','DECLINED');
-- ddl-end --
ALTER TYPE public."AdminRequestStatus" OWNER TO postgres;
-- ddl-end --

-- object: public.admin_requests | type: TABLE --
-- DROP TABLE IF EXISTS public.admin_requests CASCADE;
CREATE TABLE public.admin_requests (
	id bigint NOT NULL DEFAULT nextval('public.admin_requests_seq'::regclass),
	user_id bigint NOT NULL,
	curated_by_user_id bigint,
	status public."AdminRequestStatus" NOT NULL DEFAULT 'PENDING',
	created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp with time zone,
	CONSTRAINT admin_requests_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.admin_requests OWNER TO postgres;
-- ddl-end --

-- object: users_fk | type: CONSTRAINT --
-- ALTER TABLE public.admin_requests DROP CONSTRAINT IF EXISTS users_fk CASCADE;
ALTER TABLE public.admin_requests ADD CONSTRAINT users_fk FOREIGN KEY (user_id)
REFERENCES public.users (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: admin_requests_uq | type: CONSTRAINT --
-- ALTER TABLE public.admin_requests DROP CONSTRAINT IF EXISTS admin_requests_uq CASCADE;
ALTER TABLE public.admin_requests ADD CONSTRAINT admin_requests_uq UNIQUE (user_id);
-- ddl-end --

-- object: users_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_connection_requests DROP CONSTRAINT IF EXISTS users_fk CASCADE;
ALTER TABLE public.user_connection_requests ADD CONSTRAINT users_fk FOREIGN KEY (user_id)
REFERENCES public.users (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: user_profile_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_profiles DROP CONSTRAINT IF EXISTS user_profile_fk CASCADE;
ALTER TABLE public.user_profiles ADD CONSTRAINT user_profile_fk FOREIGN KEY (user_id)
REFERENCES public.users (id) MATCH SIMPLE
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: cv_document_id_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_profiles DROP CONSTRAINT IF EXISTS cv_document_id_fk CASCADE;
ALTER TABLE public.user_profiles ADD CONSTRAINT cv_document_id_fk FOREIGN KEY (cv_document_id)
REFERENCES public.media (id) MATCH SIMPLE
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: profile_pic_id_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_profiles DROP CONSTRAINT IF EXISTS profile_pic_id_fk CASCADE;
ALTER TABLE public.user_profiles ADD CONSTRAINT profile_pic_id_fk FOREIGN KEY (profile_pic_id)
REFERENCES public.media (id) MATCH SIMPLE
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: user_connected_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_connections DROP CONSTRAINT IF EXISTS user_connected_fk CASCADE;
ALTER TABLE public.user_connections ADD CONSTRAINT user_connected_fk FOREIGN KEY (connected_user_id)
REFERENCES public.users (id) MATCH SIMPLE
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: connected_user_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_connection_requests DROP CONSTRAINT IF EXISTS connected_user_fk CASCADE;
ALTER TABLE public.user_connection_requests ADD CONSTRAINT connected_user_fk FOREIGN KEY (connected_user_id)
REFERENCES public.users (id) MATCH SIMPLE
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: cv_media_fk | type: CONSTRAINT --
-- ALTER TABLE public.job_ads_applicants DROP CONSTRAINT IF EXISTS cv_media_fk CASCADE;
ALTER TABLE public.job_ads_applicants ADD CONSTRAINT cv_media_fk FOREIGN KEY (cv_media_id)
REFERENCES public.media (id) MATCH SIMPLE
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: user_fk | type: CONSTRAINT --
-- ALTER TABLE public.notifications DROP CONSTRAINT IF EXISTS user_fk CASCADE;
ALTER TABLE public.notifications ADD CONSTRAINT user_fk FOREIGN KEY (user_id)
REFERENCES public.users (id) MATCH SIMPLE
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: recipient_fk | type: CONSTRAINT --
-- ALTER TABLE public.notifications DROP CONSTRAINT IF EXISTS recipient_fk CASCADE;
ALTER TABLE public.notifications ADD CONSTRAINT recipient_fk FOREIGN KEY (recipient_id)
REFERENCES public.users (id) MATCH SIMPLE
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: users_fk | type: CONSTRAINT --
-- ALTER TABLE public.chat_rooms DROP CONSTRAINT IF EXISTS users_fk CASCADE;
ALTER TABLE public.chat_rooms ADD CONSTRAINT users_fk FOREIGN KEY (related_user_id1)
REFERENCES public.users (id) MATCH SIMPLE
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: recipient_fk | type: CONSTRAINT --
-- ALTER TABLE public.chat_rooms DROP CONSTRAINT IF EXISTS recipient_fk CASCADE;
ALTER TABLE public.chat_rooms ADD CONSTRAINT recipient_fk FOREIGN KEY (related_user_id2)
REFERENCES public.users (id) MATCH SIMPLE
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: sender_fk | type: CONSTRAINT --
-- ALTER TABLE public.chat_rooms_data DROP CONSTRAINT IF EXISTS sender_fk CASCADE;
ALTER TABLE public.chat_rooms_data ADD CONSTRAINT sender_fk FOREIGN KEY (sender_id)
REFERENCES public.users (id) MATCH SIMPLE
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: curated_by_fk | type: CONSTRAINT --
-- ALTER TABLE public.admin_requests DROP CONSTRAINT IF EXISTS curated_by_fk CASCADE;
ALTER TABLE public.admin_requests ADD CONSTRAINT curated_by_fk FOREIGN KEY (curated_by_user_id)
REFERENCES public.users (id) MATCH SIMPLE
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

insert into users values (DEFAULT,'admin-hardcoded','admin-hardcoded@gmail.com','admin','admin','69xxxxxxxx');
insert into user_action_types (id,type) values (0,'NEW'),(1,'UPDATE');
CREATE CAST (varchar AS public."UserConnectionRequestStatus") WITH INOUT AS IMPLICIT;
CREATE CAST (public."UserConnectionRequestStatus" AS varchar) WITH INOUT AS IMPLICIT;