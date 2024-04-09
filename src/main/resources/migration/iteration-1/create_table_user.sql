CREATE TABLE app_user (
					  user_id NUMBER(6) NOT NULL,
					  user_name VARCHAR2(255) NOT NULL,
					  email VARCHAR2(255) NOT NULL,
					  avatar VARCHAR2(120),
					  phone VARCHAR2(20),
					  CONSTRAINT app_user_pk PRIMARY KEY (user_id)
);
