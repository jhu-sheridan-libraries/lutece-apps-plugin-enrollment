
--
-- Structure for table enroll_project
--

DROP TABLE IF EXISTS enroll_project;
-- DROP TABLE IF EXISTS enroll_user;
DROP TABLE IF EXISTS enroll_enrollment;
CREATE TABLE enroll_project (
id_project int(6) NOT NULL,
name varchar(250) default '' NOT NULL,
size int(6) default 0 NOT NULL,
currentsize int(6) default 0 NOT NULL,
active int(1) default 1 NOT NULL,
PRIMARY KEY (id_project))
ENGINE = InnoDB;
CREATE TABLE enroll_enrollment (
id_enrollment int(6) NOT NULL,
program varchar(250) default '' NOT NULL,
name varchar(250) default '' NOT NULL,
email varchar(250) default '' NOT NULL,
phone varchar(250) default '' NOT NULL,
PRIMARY KEY (id_enrollment))
ENGINE = InnoDB;
-- CREATE TABLE enroll_user (
-- id_user INT NOT NULL,
-- guid VARCHAR(255) NULL,
-- first_name VARCHAR(255) BINARY NOT NULL,
-- last_name VARCHAR(255) BINARY NOT NULL,
-- email VARCHAR(255) NULL,
-- phone_number VARCHAR(255) NULL,
-- PRIMARY KEY (id_user),
-- UNIQUE KEY unique_index_email (first_name, last_name, email))
-- ENGINE = InnoDB;
-- CREATE TABLE IF NOT EXISTS enroll_enrollment (
-- id_enrollment INT NOT NULL,
-- reference VARCHAR(45) NULL,
-- nb_places INT NOT NULL DEFAULT 0,
-- is_cancelled BOOLEAN NOT NULL DEFAULT FALSE,
-- id_action_cancelled INT,
-- notification INT NOT NULL DEFAULT 0,
-- id_admin_user INT NULL DEFAULT 0,
-- id_user INT NOT NULL,
-- PRIMARY KEY (id_enrollment, id_user),
-- CONSTRAINT fk_enroll_enrollment_enroll_user
-- FOREIGN KEY (id_user)
-- REFERENCES enroll_user (id_user)
-- ON DELETE CASCADE
-- ON UPDATE NO ACTION)
-- ENGINE = InnoDB;
