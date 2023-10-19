USE icstar_database;

SHOW TABLES ;

SELECT * FROM users;

DESC users;

select * from sales;

delete from sales;

RENAME TABLE users TO users;

select * from sales;

alter table sales add column id_uploader varchar(200);

alter table sales add constraint fk_user_uploader_id foreign key (id_uploader) references users(email);

desc sales;

alter table sales add column upload_time bigint;

alter table sales change column id_uploader user_uploader varchar(200);

alter table sales add column last_modified_at bigint;

select * from users;

alter table users change column session_token_active_until session_token_active_until varchar(20);

alter table sales change column  upload_time upload_time varchar(20);

select * from sales;

truncate table sales;

create table business_unit;

desc
