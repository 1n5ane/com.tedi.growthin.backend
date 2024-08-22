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
CREATE TABLE public.users
(
    id                bigint                   NOT NULL DEFAULT nextval('public.users_id_seq'::regclass),
    username          varchar(25)              NOT NULL,
    email             varchar(255)             NOT NULL,
    is_email_public   boolean                  not null DEFAULT false,
    first_name        varchar(255)             NOT NULL,
    last_name         varchar(255)             NOT NULL,
    phone             varchar(255),
    is_phone_public   boolean                  not null DEFAULT false,
    country           varchar(100),
    is_country_public boolean                  not null DEFAULT false,
    area              varchar(255),
    is_area_public    boolean                  not null DEFAULT false,
    is_admin          boolean                  NOT NULL DEFAULT false,
    locked            boolean                  NOT NULL DEFAULT false,
    created_at        timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        timestamp with time zone,
    CONSTRAINT users_pk PRIMARY KEY (id),
    CONSTRAINT unique_username UNIQUE (username),
    CONSTRAINT unique_email UNIQUE (email)
);
-- ddl-end --
ALTER TABLE public.users
    OWNER TO postgres;
-- ddl-end --

-- object: public.user_profiles | type: TABLE --
-- DROP TABLE IF EXISTS public.user_profiles CASCADE;
CREATE TABLE public.user_profiles
(
    user_id               bigint                   NOT NULL,
    job_field             text,
    is_job_field_public   boolean                  not null default false,
    profile_pic_id        bigint,
    description           text,
    is_description_public boolean                  not null default false,
    age                   smallint,
    is_age_public         boolean                  not null default false,
    education             text,
    is_education_public   boolean                  not null default false,
    country               varchar(50),
    is_country_public     boolean                  not null default false,
    cv_document_id        bigint,
    is_cv_document_public boolean                  not null default false,
    created_at            timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            timestamp with time zone,
    CONSTRAINT user_profiles_pk PRIMARY KEY (user_id),
    CONSTRAINT user_profile_id_uniqueness UNIQUE (user_id),
    CONSTRAINT cv_uniqueness UNIQUE (cv_document_id)
);
-- ddl-end --
ALTER TABLE public.user_profiles
    OWNER TO postgres;
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
CREATE TABLE public.job_field_keywords
(
    id   bigint       NOT NULL DEFAULT nextval('public.job_field_keywords_id_seq'::regclass),
    name varchar(255) NOT NULL,
    CONSTRAINT job_field_keywords_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.job_field_keywords
    OWNER TO postgres;
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
CREATE TABLE public.comments
(
    id          bigint                   NOT NULL DEFAULT nextval('public.comments_id_seq'::regclass),
    id_articles bigint                   NOT NULL,
    id_users    bigint                   NOT NULL,
    body        text                     NOT NULL,
    created_at  timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  timestamp with time zone,
    is_deleted  boolean                  NOT NULL DEFAULT false,
    CONSTRAINT comments_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.comments
    OWNER TO postgres;
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
CREATE TABLE public.media
(
    id             bigint                   NOT NULL DEFAULT nextval('public.media_id_seq'::regclass),
    id_users       bigint                   NOT NULL,
    id_media_types smallint                 NOT NULL,
    data           bytea                    NOT NULL,
    created_at     timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted     boolean                           DEFAULT false,
    CONSTRAINT media_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.media
    OWNER TO postgres;
-- ddl-end --

-- object: public.media_types | type: TABLE --
-- DROP TABLE IF EXISTS public.media_types CASCADE;
CREATE TABLE public.media_types
(
    id   smallint     NOT NULL,
    name varchar(255) NOT NULL,
    CONSTRAINT media_types_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.media_types
    OWNER TO postgres;
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
CREATE TABLE public.user_comments_reactions
(
    id           bigint                   NOT NULL DEFAULT nextval('public.user_comments_reactions_id_seq'::regclass),
    id_users     bigint                   NOT NULL,
    id_comments  bigint                   NOT NULL,
    id_reactions integer                  NOT NULL,
    created_at   timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   timestamp with time zone,
    CONSTRAINT user_comments_reactions_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.user_comments_reactions
    OWNER TO postgres;
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
CREATE TABLE public.reactions
(
    id         integer                  NOT NULL DEFAULT nextval('public.reactions_id_seq'::regclass),
    alias      varchar(30)              NOT NULL DEFAULT '',
    image      bytea                    NOT NULL,
    created_at timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT reactions_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.reactions
    OWNER TO postgres;
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
CREATE TABLE public.user_articles_reactions
(
    id           bigint                   NOT NULL DEFAULT nextval('public.user_articles_reactions_id_seq'::regclass),
    id_articles  bigint                   NOT NULL,
    id_users     bigint                   NOT NULL,
    id_reactions integer                  NOT NULL,
    created_at   timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   timestamp with time zone,
    CONSTRAINT user_articles_reactions_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.user_articles_reactions
    OWNER TO postgres;
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
CREATE TABLE public.user_connections
(
    id                bigint                   NOT NULL DEFAULT nextval('public.user_connection_id_seq'::regclass),
    user_id           bigint                   NOT NULL,
    connected_user_id bigint                   NOT NULL,
    created_at        timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT user_connections_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.user_connections
    OWNER TO postgres;
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
CREATE TABLE public.user_job_field_keywords
(
    id_users              bigint NOT NULL,
    id_job_field_keywords bigint NOT NULL

);
-- ddl-end --
ALTER TABLE public.user_job_field_keywords
    OWNER TO postgres;
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
CREATE TABLE public.articles_media
(
    id          bigint                   NOT NULL DEFAULT nextval('public.articles_media_seq'::regclass),
    id_articles bigint                   NOT NULL,
    id_media    bigint,
    "order"     integer                  NOT NULL DEFAULT 0,
    created_at  timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT articles_media_pk PRIMARY KEY (id)
);
-- ddl-end --
COMMENT ON COLUMN public.articles_media."order" IS E'order of user media (to display in the order of user upload)';
-- ddl-end --
ALTER TABLE public.articles_media
    OWNER TO postgres;
-- ddl-end --

-- object: public.user_action_types | type: TABLE --
-- DROP TABLE IF EXISTS public.user_action_types CASCADE;
CREATE TABLE public.user_action_types
(
    id   smallint    NOT NULL,
    type varchar(25) NOT NULL,
    CONSTRAINT user_action_types_pk PRIMARY KEY (id)
);
-- ddl-end --
COMMENT ON COLUMN public.user_action_types.type IS E'type of user action (ex. POST_ARTICLE, REACTION, CONNECTION..\n)';
-- ddl-end --
ALTER TABLE public.user_action_types
    OWNER TO postgres;
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
CREATE TABLE public.user_history
(
    id                          bigint                   NOT NULL DEFAULT nextval('public.user_history_id_seq'::regclass),
    id_users                    bigint                   NOT NULL,
    id_user_action_types        smallint                 NOT NULL,
    is_article                  boolean                  NOT NULL DEFAULT false,
    id_articles                 bigint,
    is_comment                  boolean                  NOT NULL DEFAULT false,
    id_comments                 bigint,
    is_connection_request       boolean                  NOT NULL DEFAULT false,
    id_user_connection_requests bigint,
    is_job_ad                   boolean                  NOT NULL DEFAULT false,
    id_job_ads                  bigint,
    is_comment_reaction         boolean                  NOT NULL DEFAULT false,
    id_user_comments_reactions  bigint,
    is_article_reaction         boolean                  NOT NULL DEFAULT false,
    id_user_articles_reactions  bigint,
    created_at                  timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  timestamp with time zone,
    CONSTRAINT user_history_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.user_history
    OWNER TO postgres;
-- ddl-end --

-- object: user_action_types_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_history DROP CONSTRAINT IF EXISTS user_action_types_fk CASCADE;
ALTER TABLE public.user_history
    ADD CONSTRAINT user_action_types_fk FOREIGN KEY (id_user_action_types)
        REFERENCES public.user_action_types (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: users_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_articles_reactions DROP CONSTRAINT IF EXISTS users_fk CASCADE;
ALTER TABLE public.user_articles_reactions
    ADD CONSTRAINT users_fk FOREIGN KEY (id_users)
        REFERENCES public.users (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: users_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_history DROP CONSTRAINT IF EXISTS users_fk CASCADE;
ALTER TABLE public.user_history
    ADD CONSTRAINT users_fk FOREIGN KEY (id_users)
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
CREATE TABLE public.job_ads
(
    id            bigint                   NOT NULL DEFAULT nextval('public.job_ads_id_seq'::regclass),
    id_users      bigint                   NOT NULL,
    title         varchar(100)             NOT NULL,
    description   text                     NOT NULL,
    created_at    timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    timestamp with time zone,
    is_active     boolean                  NOT NULL DEFAULT true,
    public_status public."PublicStatus"    NOT NULL DEFAULT 'PUBLIC',
    is_deleted    boolean                           DEFAULT false,
    CONSTRAINT job_ads_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.job_ads
    OWNER TO postgres;
-- ddl-end --

-- object: public.job_ads_keywords | type: TABLE --
-- DROP TABLE IF EXISTS public.job_ads_keywords CASCADE;
CREATE TABLE public.job_ads_keywords
(
    id_job_field_keywords bigint NOT NULL,
    id_job_ads            bigint NOT NULL

);
-- ddl-end --
ALTER TABLE public.job_ads_keywords
    OWNER TO postgres;
-- ddl-end --

-- object: job_field_keywords_fk | type: CONSTRAINT --
-- ALTER TABLE public.job_ads_keywords DROP CONSTRAINT IF EXISTS job_field_keywords_fk CASCADE;
ALTER TABLE public.job_ads_keywords
    ADD CONSTRAINT job_field_keywords_fk FOREIGN KEY (id_job_field_keywords)
        REFERENCES public.job_field_keywords (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: users_fk | type: CONSTRAINT --
-- ALTER TABLE public.job_ads DROP CONSTRAINT IF EXISTS users_fk CASCADE;
ALTER TABLE public.job_ads
    ADD CONSTRAINT users_fk FOREIGN KEY (id_users)
        REFERENCES public.users (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: job_ads_fk | type: CONSTRAINT --
-- ALTER TABLE public.job_ads_keywords DROP CONSTRAINT IF EXISTS job_ads_fk CASCADE;
ALTER TABLE public.job_ads_keywords
    ADD CONSTRAINT job_ads_fk FOREIGN KEY (id_job_ads)
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
CREATE TABLE public.job_ads_applicants
(
    id_job_ads  bigint                   NOT NULL,
    id_users    bigint                   NOT NULL,
    cv_media_id integer,
    created_at  timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP

);
-- ddl-end --
ALTER TABLE public.job_ads_applicants
    OWNER TO postgres;
-- ddl-end --

-- object: job_ads_fk | type: CONSTRAINT --
-- ALTER TABLE public.job_ads_applicants DROP CONSTRAINT IF EXISTS job_ads_fk CASCADE;
ALTER TABLE public.job_ads_applicants
    ADD CONSTRAINT job_ads_fk FOREIGN KEY (id_job_ads)
        REFERENCES public.job_ads (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: users_fk | type: CONSTRAINT --
-- ALTER TABLE public.job_ads_applicants DROP CONSTRAINT IF EXISTS users_fk CASCADE;
ALTER TABLE public.job_ads_applicants
    ADD CONSTRAINT users_fk FOREIGN KEY (id_users)
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
CREATE TABLE public.articles
(
    id            bigint                   NOT NULL DEFAULT nextval('public.articles_id_seq'::regclass),
    id_users      bigint                   NOT NULL,
    title         varchar(512),
    body          text                     NOT NULL,
    public_status public."PublicStatus"    NOT NULL DEFAULT 'PUBLIC',
    created_at    timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    timestamp with time zone,
    is_deleted    boolean                  NOT NULL DEFAULT false,
    CONSTRAINT articles_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.articles
    OWNER TO postgres;
-- ddl-end --

-- object: articles_fk | type: CONSTRAINT --
-- ALTER TABLE public.articles_media DROP CONSTRAINT IF EXISTS articles_fk CASCADE;
ALTER TABLE public.articles_media
    ADD CONSTRAINT articles_fk FOREIGN KEY (id_articles)
        REFERENCES public.articles (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: media_types_fk | type: CONSTRAINT --
-- ALTER TABLE public.media DROP CONSTRAINT IF EXISTS media_types_fk CASCADE;
ALTER TABLE public.media
    ADD CONSTRAINT media_types_fk FOREIGN KEY (id_media_types)
        REFERENCES public.media_types (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: users_fk | type: CONSTRAINT --
-- ALTER TABLE public.media DROP CONSTRAINT IF EXISTS users_fk CASCADE;
ALTER TABLE public.media
    ADD CONSTRAINT users_fk FOREIGN KEY (id_users)
        REFERENCES public.users (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: media_fk | type: CONSTRAINT --
-- ALTER TABLE public.articles_media DROP CONSTRAINT IF EXISTS media_fk CASCADE;
ALTER TABLE public.articles_media
    ADD CONSTRAINT media_fk FOREIGN KEY (id_media)
        REFERENCES public.media (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: articles_fk | type: CONSTRAINT --
-- ALTER TABLE public.comments DROP CONSTRAINT IF EXISTS articles_fk CASCADE;
ALTER TABLE public.comments
    ADD CONSTRAINT articles_fk FOREIGN KEY (id_articles)
        REFERENCES public.articles (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: users_fk | type: CONSTRAINT --
-- ALTER TABLE public.comments DROP CONSTRAINT IF EXISTS users_fk CASCADE;
ALTER TABLE public.comments
    ADD CONSTRAINT users_fk FOREIGN KEY (id_users)
        REFERENCES public.users (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: comments_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_comments_reactions DROP CONSTRAINT IF EXISTS comments_fk CASCADE;
ALTER TABLE public.user_comments_reactions
    ADD CONSTRAINT comments_fk FOREIGN KEY (id_comments)
        REFERENCES public.comments (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: users_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_comments_reactions DROP CONSTRAINT IF EXISTS users_fk CASCADE;
ALTER TABLE public.user_comments_reactions
    ADD CONSTRAINT users_fk FOREIGN KEY (id_users)
        REFERENCES public.users (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: reactions_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_comments_reactions DROP CONSTRAINT IF EXISTS reactions_fk CASCADE;
ALTER TABLE public.user_comments_reactions
    ADD CONSTRAINT reactions_fk FOREIGN KEY (id_reactions)
        REFERENCES public.reactions (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: reactions_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_articles_reactions DROP CONSTRAINT IF EXISTS reactions_fk CASCADE;
ALTER TABLE public.user_articles_reactions
    ADD CONSTRAINT reactions_fk FOREIGN KEY (id_reactions)
        REFERENCES public.reactions (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: articles_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_articles_reactions DROP CONSTRAINT IF EXISTS articles_fk CASCADE;
ALTER TABLE public.user_articles_reactions
    ADD CONSTRAINT articles_fk FOREIGN KEY (id_articles)
        REFERENCES public.articles (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: user_id_reaction_id_articles_id_uniqueness | type: CONSTRAINT --
-- ALTER TABLE public.user_articles_reactions DROP CONSTRAINT IF EXISTS user_id_reaction_id_articles_id_uniqueness CASCADE;
ALTER TABLE public.user_articles_reactions
    ADD CONSTRAINT user_id_reaction_id_articles_id_uniqueness UNIQUE (id_articles, id_users);
-- ddl-end --
COMMENT ON CONSTRAINT user_id_reaction_id_articles_id_uniqueness ON public.user_articles_reactions IS E'One reaction per user per article';
-- ddl-end --


-- object: users_id_reactions_id_comments_id_uq | type: CONSTRAINT --
-- ALTER TABLE public.user_comments_reactions DROP CONSTRAINT IF EXISTS users_id_reactions_id_comments_id_uq CASCADE;
ALTER TABLE public.user_comments_reactions
    ADD CONSTRAINT users_id_reactions_id_comments_id_uq UNIQUE (id_users, id_comments);
-- ddl-end --
COMMENT ON CONSTRAINT users_id_reactions_id_comments_id_uq ON public.user_comments_reactions IS E'one reaction per user per comment';
-- ddl-end --


-- object: users_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_connections DROP CONSTRAINT IF EXISTS users_fk CASCADE;
ALTER TABLE public.user_connections
    ADD CONSTRAINT users_fk FOREIGN KEY (user_id)
        REFERENCES public.users (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: public.user_connection_requests | type: TABLE --
-- DROP TABLE IF EXISTS public.user_connection_requests CASCADE;
CREATE TABLE public.user_connection_requests
(
    id                bigint                               NOT NULL DEFAULT nextval('public.user_connection_request_id_seq'::regclass),
    user_id           bigint                               NOT NULL,
    connected_user_id bigint                               NOT NULL,
    status            public."UserConnectionRequestStatus" NOT NULL DEFAULT 'PENDING',
    created_at        timestamp with time zone             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT user_connection_requests_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.user_connection_requests
    OWNER TO postgres;
-- ddl-end --

-- object: articles_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_history DROP CONSTRAINT IF EXISTS articles_fk CASCADE;
ALTER TABLE public.user_history
    ADD CONSTRAINT articles_fk FOREIGN KEY (id_articles)
        REFERENCES public.articles (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: comments_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_history DROP CONSTRAINT IF EXISTS comments_fk CASCADE;
ALTER TABLE public.user_history
    ADD CONSTRAINT comments_fk FOREIGN KEY (id_comments)
        REFERENCES public.comments (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: job_ads_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_history DROP CONSTRAINT IF EXISTS job_ads_fk CASCADE;
ALTER TABLE public.user_history
    ADD CONSTRAINT job_ads_fk FOREIGN KEY (id_job_ads)
        REFERENCES public.job_ads (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: user_connection_requests_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_history DROP CONSTRAINT IF EXISTS user_connection_requests_fk CASCADE;
ALTER TABLE public.user_history
    ADD CONSTRAINT user_connection_requests_fk FOREIGN KEY (id_user_connection_requests)
        REFERENCES public.user_connection_requests (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: users_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_job_field_keywords DROP CONSTRAINT IF EXISTS users_fk CASCADE;
ALTER TABLE public.user_job_field_keywords
    ADD CONSTRAINT users_fk FOREIGN KEY (id_users)
        REFERENCES public.users (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: job_field_keywords_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_job_field_keywords DROP CONSTRAINT IF EXISTS job_field_keywords_fk CASCADE;
ALTER TABLE public.user_job_field_keywords
    ADD CONSTRAINT job_field_keywords_fk FOREIGN KEY (id_job_field_keywords)
        REFERENCES public.job_field_keywords (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: public.notifications | type: TABLE --
-- DROP TABLE IF EXISTS public.notifications CASCADE;
CREATE TABLE public.notifications
(
    id                          bigint   NOT NULL,
    user_id                     bigint   NOT NULL,
    recipient_id                bigint   NOT NULL,
    notification_type_id        integer  NOT NULL,
    viewed                      boolean  NOT NULL        DEFAULT false,
    is_article                  boolean  NOT NULL        DEFAULT false,
    id_articles                 bigint,
    is_comment                  boolean  NOT NULL        DEFAULT false,
    id_comments                 bigint,
    is_connection_request       boolean  NOT NULL        DEFAULT false,
    id_user_connection_requests bigint,
    is_job_ad                   boolean  NOT NULL        DEFAULT false,
    id_job_ads                  bigint,
    is_article_reaction         boolean                  DEFAULT false,
    id_user_articles_reactions  bigint,
    is_comment_reaction         boolean  NOT NULL        DEFAULT false,
    id_user_comments_reactions  bigint,
    is_chat                     boolean  NOT NULL        DEFAULT false,
    id_chat_rooms               bigint,
    created_at                  timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT notification_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.notifications
    OWNER TO postgres;
-- ddl-end --

-- object: public.notification_types | type: TABLE --
-- DROP TABLE IF EXISTS public.notification_types CASCADE;
CREATE TABLE public.notification_types
(
    id   integer      NOT NULL,
    name varchar(100) NOT NULL,
    CONSTRAINT notification_types_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.notification_types
    OWNER TO postgres;
-- ddl-end --

-- object: notification_types_fk | type: CONSTRAINT --
-- ALTER TABLE public.notifications DROP CONSTRAINT IF EXISTS notification_types_fk CASCADE;
ALTER TABLE public.notifications
    ADD CONSTRAINT notification_types_fk FOREIGN KEY (notification_type_id)
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
ALTER TABLE public.user_history
    ADD CONSTRAINT user_articles_reactions_fk FOREIGN KEY (id_user_articles_reactions)
        REFERENCES public.user_articles_reactions (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: user_comments_reactions_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_history DROP CONSTRAINT IF EXISTS user_comments_reactions_fk CASCADE;
ALTER TABLE public.user_history
    ADD CONSTRAINT user_comments_reactions_fk FOREIGN KEY (id_user_comments_reactions)
        REFERENCES public.user_comments_reactions (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: user_articles_reactions_fk | type: CONSTRAINT --
-- ALTER TABLE public.notifications DROP CONSTRAINT IF EXISTS user_articles_reactions_fk CASCADE;
ALTER TABLE public.notifications
    ADD CONSTRAINT user_articles_reactions_fk FOREIGN KEY (id_user_articles_reactions)
        REFERENCES public.user_articles_reactions (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: user_comments_reactions_fk | type: CONSTRAINT --
-- ALTER TABLE public.notifications DROP CONSTRAINT IF EXISTS user_comments_reactions_fk CASCADE;
ALTER TABLE public.notifications
    ADD CONSTRAINT user_comments_reactions_fk FOREIGN KEY (id_user_comments_reactions)
        REFERENCES public.user_comments_reactions (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: comments_fk | type: CONSTRAINT --
-- ALTER TABLE public.notifications DROP CONSTRAINT IF EXISTS comments_fk CASCADE;
ALTER TABLE public.notifications
    ADD CONSTRAINT comments_fk FOREIGN KEY (id_comments)
        REFERENCES public.comments (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: articles_fk | type: CONSTRAINT --
-- ALTER TABLE public.notifications DROP CONSTRAINT IF EXISTS articles_fk CASCADE;
ALTER TABLE public.notifications
    ADD CONSTRAINT articles_fk FOREIGN KEY (id_articles)
        REFERENCES public.articles (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: job_ads_fk | type: CONSTRAINT --
-- ALTER TABLE public.notifications DROP CONSTRAINT IF EXISTS job_ads_fk CASCADE;
ALTER TABLE public.notifications
    ADD CONSTRAINT job_ads_fk FOREIGN KEY (id_job_ads)
        REFERENCES public.job_ads (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: user_connection_requests_fk | type: CONSTRAINT --
-- ALTER TABLE public.notifications DROP CONSTRAINT IF EXISTS user_connection_requests_fk CASCADE;
ALTER TABLE public.notifications
    ADD CONSTRAINT user_connection_requests_fk FOREIGN KEY (id_user_connection_requests)
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
CREATE TABLE public.chat_rooms
(
    id               bigint                   NOT NULL DEFAULT nextval('public.chat_room_seq'::regclass),
    related_user_id1 bigint                   NOT NULL,
    related_user_id2 bigint                   NOT NULL,
    created_at       timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chat_rooms_pk PRIMARY KEY (id),
    CONSTRAINT user_chatroom_uniqueness UNIQUE (related_user_id1, related_user_id2)
);
-- ddl-end --
COMMENT ON CONSTRAINT user_chatroom_uniqueness ON public.chat_rooms IS E'2 related users can have only 1 chatroom';
-- ddl-end --
ALTER TABLE public.chat_rooms
    OWNER TO postgres;
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
CREATE TABLE public.chat_rooms_data
(
    id            bigint  NOT NULL         DEFAULT nextval('public.chat_messages_seq'::regclass),
    id_chat_rooms bigint  NOT NULL,
    sender_id     bigint  NOT NULL,
    message       text    NOT NULL,
    is_read       boolean NOT NULL         DEFAULT false,
    id_media      bigint,
    created_at    timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at    timestamp with time zone,
    CONSTRAINT chat_rooms_data_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.chat_rooms_data
    OWNER TO postgres;
-- ddl-end --

-- object: chat_rooms_fk | type: CONSTRAINT --
-- ALTER TABLE public.chat_rooms_data DROP CONSTRAINT IF EXISTS chat_rooms_fk CASCADE;
ALTER TABLE public.chat_rooms_data
    ADD CONSTRAINT chat_rooms_fk FOREIGN KEY (id_chat_rooms)
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
ALTER TABLE public.chat_rooms_data
    ADD CONSTRAINT media_fk FOREIGN KEY (id_media)
        REFERENCES public.media (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: chat_rooms_fk | type: CONSTRAINT --
-- ALTER TABLE public.notifications DROP CONSTRAINT IF EXISTS chat_rooms_fk CASCADE;
ALTER TABLE public.notifications
    ADD CONSTRAINT chat_rooms_fk FOREIGN KEY (id_chat_rooms)
        REFERENCES public.chat_rooms (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: job_ad_id_user_id_unique | type: CONSTRAINT --
-- ALTER TABLE public.job_ads_applicants DROP CONSTRAINT IF EXISTS job_ad_id_user_id_unique CASCADE;
ALTER TABLE public.job_ads_applicants
    ADD CONSTRAINT job_ad_id_user_id_unique UNIQUE (id_job_ads, id_users);
-- ddl-end --
COMMENT ON CONSTRAINT job_ad_id_user_id_unique ON public.job_ads_applicants IS E'one application per user per job';
-- ddl-end --


-- object: users_fk | type: CONSTRAINT --
-- ALTER TABLE public.articles DROP CONSTRAINT IF EXISTS users_fk CASCADE;
ALTER TABLE public.articles
    ADD CONSTRAINT users_fk FOREIGN KEY (id_users)
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
CREATE TABLE public.admin_requests
(
    id                 bigint                      NOT NULL DEFAULT nextval('public.admin_requests_seq'::regclass),
    user_id            bigint                      NOT NULL,
    curated_by_user_id bigint,
    status             public."AdminRequestStatus" NOT NULL DEFAULT 'PENDING',
    created_at         timestamp with time zone    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         timestamp with time zone,
    CONSTRAINT admin_requests_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.admin_requests
    OWNER TO postgres;
-- ddl-end --

-- object: users_fk | type: CONSTRAINT --
-- ALTER TABLE public.admin_requests DROP CONSTRAINT IF EXISTS users_fk CASCADE;
ALTER TABLE public.admin_requests
    ADD CONSTRAINT users_fk FOREIGN KEY (user_id)
        REFERENCES public.users (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: admin_requests_uq | type: CONSTRAINT --
-- ALTER TABLE public.admin_requests DROP CONSTRAINT IF EXISTS admin_requests_uq CASCADE;
ALTER TABLE public.admin_requests
    ADD CONSTRAINT admin_requests_uq UNIQUE (user_id);
-- ddl-end --

-- object: users_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_connection_requests DROP CONSTRAINT IF EXISTS users_fk CASCADE;
ALTER TABLE public.user_connection_requests
    ADD CONSTRAINT users_fk FOREIGN KEY (user_id)
        REFERENCES public.users (id) MATCH FULL
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: user_profile_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_profiles DROP CONSTRAINT IF EXISTS user_profile_fk CASCADE;
ALTER TABLE public.user_profiles
    ADD CONSTRAINT user_profile_fk FOREIGN KEY (user_id)
        REFERENCES public.users (id) MATCH SIMPLE
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: cv_document_id_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_profiles DROP CONSTRAINT IF EXISTS cv_document_id_fk CASCADE;
ALTER TABLE public.user_profiles
    ADD CONSTRAINT cv_document_id_fk FOREIGN KEY (cv_document_id)
        REFERENCES public.media (id) MATCH SIMPLE
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: profile_pic_id_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_profiles DROP CONSTRAINT IF EXISTS profile_pic_id_fk CASCADE;
ALTER TABLE public.user_profiles
    ADD CONSTRAINT profile_pic_id_fk FOREIGN KEY (profile_pic_id)
        REFERENCES public.media (id) MATCH SIMPLE
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: user_connected_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_connections DROP CONSTRAINT IF EXISTS user_connected_fk CASCADE;
ALTER TABLE public.user_connections
    ADD CONSTRAINT user_connected_fk FOREIGN KEY (connected_user_id)
        REFERENCES public.users (id) MATCH SIMPLE
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: connected_user_fk | type: CONSTRAINT --
-- ALTER TABLE public.user_connection_requests DROP CONSTRAINT IF EXISTS connected_user_fk CASCADE;
ALTER TABLE public.user_connection_requests
    ADD CONSTRAINT connected_user_fk FOREIGN KEY (connected_user_id)
        REFERENCES public.users (id) MATCH SIMPLE
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: cv_media_fk | type: CONSTRAINT --
-- ALTER TABLE public.job_ads_applicants DROP CONSTRAINT IF EXISTS cv_media_fk CASCADE;
ALTER TABLE public.job_ads_applicants
    ADD CONSTRAINT cv_media_fk FOREIGN KEY (cv_media_id)
        REFERENCES public.media (id) MATCH SIMPLE
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: user_fk | type: CONSTRAINT --
-- ALTER TABLE public.notifications DROP CONSTRAINT IF EXISTS user_fk CASCADE;
ALTER TABLE public.notifications
    ADD CONSTRAINT user_fk FOREIGN KEY (user_id)
        REFERENCES public.users (id) MATCH SIMPLE
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: recipient_fk | type: CONSTRAINT --
-- ALTER TABLE public.notifications DROP CONSTRAINT IF EXISTS recipient_fk CASCADE;
ALTER TABLE public.notifications
    ADD CONSTRAINT recipient_fk FOREIGN KEY (recipient_id)
        REFERENCES public.users (id) MATCH SIMPLE
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: users_fk | type: CONSTRAINT --
-- ALTER TABLE public.chat_rooms DROP CONSTRAINT IF EXISTS users_fk CASCADE;
ALTER TABLE public.chat_rooms
    ADD CONSTRAINT users_fk FOREIGN KEY (related_user_id1)
        REFERENCES public.users (id) MATCH SIMPLE
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: recipient_fk | type: CONSTRAINT --
-- ALTER TABLE public.chat_rooms DROP CONSTRAINT IF EXISTS recipient_fk CASCADE;
ALTER TABLE public.chat_rooms
    ADD CONSTRAINT recipient_fk FOREIGN KEY (related_user_id2)
        REFERENCES public.users (id) MATCH SIMPLE
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: sender_fk | type: CONSTRAINT --
-- ALTER TABLE public.chat_rooms_data DROP CONSTRAINT IF EXISTS sender_fk CASCADE;
ALTER TABLE public.chat_rooms_data
    ADD CONSTRAINT sender_fk FOREIGN KEY (sender_id)
        REFERENCES public.users (id) MATCH SIMPLE
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: curated_by_fk | type: CONSTRAINT --
-- ALTER TABLE public.admin_requests DROP CONSTRAINT IF EXISTS curated_by_fk CASCADE;
ALTER TABLE public.admin_requests
    ADD CONSTRAINT curated_by_fk FOREIGN KEY (curated_by_user_id)
        REFERENCES public.users (id) MATCH SIMPLE
        ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

insert into users
values (DEFAULT, 'admin-hardcoded', 'admin-hardcoded@gmail.com', 'admin', 'admin', '69xxxxxxxx', 'GR', NULL, true);
insert into user_action_types (id, type)
values (0, 'NEW'),
       (1, 'UPDATE');
insert into media_types
values (0, 'image/jpeg'),
       (1, 'image/png'),
       (2, 'application/pdf');
CREATE CAST (varchar AS public."UserConnectionRequestStatus") WITH INOUT AS IMPLICIT;
CREATE CAST (varchar AS public."AdminRequestStatus") WITH INOUT AS IMPLICIT;
CREATE CAST (varchar AS public."PublicStatus") WITH INOUT AS IMPLICIT;
insert into notification_types (id, name)
values (0,'ARTICLE'),
       (1, 'ARTICLE_REACTION'),
       (2,'ARTICLE_COMMENT'),
       (3, 'ARTICLE_COMMENT_REACTION'),
       (4,'CHAT_ROOM'),
       (5, 'CONNECTION_REQUEST');
insert into reactions ("alias", image)
values ('Like', decode(
        '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',
        'base64'));
insert into reactions ("alias", image)
values ('Celebrate', decode(
        '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',
        'base64'));
insert into reactions ("alias", image)
values ('Love', decode(
        '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',
        'base64'));
insert into reactions ("alias", image)
values ('Insightful', decode(
        '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',
        'base64'));
insert into reactions ("alias", image)
values ('Funny', decode(
        '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',
        'base64'));
insert into reactions ("alias", image)
values ('Dislike', decode(
        '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',
        'base64'));
