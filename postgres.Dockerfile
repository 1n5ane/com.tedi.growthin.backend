FROM postgres:alpine3.19

USER postgres

COPY ./growthin_backend_updated.sql /docker-entrypoint-initdb.d/init.sql

EXPOSE 5432