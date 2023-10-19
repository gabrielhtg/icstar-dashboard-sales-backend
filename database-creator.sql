SHOW DATABASES;

CREATE DATABASE icstar_database;

USE icstar_database;

CREATE TABLE user (
    email VARCHAR(100) NOT NULL PRIMARY KEY ,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE business_unit (
    id varchar(100) not null primary key ,
    total_revenue bigint,
    total_gross_profit bigint
);

ALTER TABLE user ADD COLUMN first_name VARCHAR(100);
ALTER TABLE user ADD COLUMN last_name VARCHAR(100);
ALTER TABLE user ADD COLUMN first_name VARCHAR(100);
