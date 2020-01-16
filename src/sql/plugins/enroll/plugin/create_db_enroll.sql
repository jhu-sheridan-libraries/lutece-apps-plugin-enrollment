
--
-- Structure for table enroll_project
--

DROP TABLE IF EXISTS enroll_project;
CREATE TABLE enroll_project (
  id_project int(6) NOT NULL,
  name varchar(250) default '' NOT NULL,
  size int(6) default 0 NOT NULL,
  currentsize int(6) default 0 NOT NULL,
  active int(1) default 1 NOT NULL,
  PRIMARY KEY (id_project))
  ENGINE = InnoDB;

--
-- Structure for table enroll_enrollment
DROP TABLE IF EXISTS enroll_enrollment;
CREATE TABLE enroll_enrollment (
  id_enrollment int(6) NOT NULL,
  program varchar(250) default '' NOT NULL,
  name varchar(250) default '' NOT NULL,
  email varchar(250) default '' NOT NULL,
  phone varchar(250) default '' NOT NULL,
  PRIMARY KEY (id_enrollment))
  ENGINE = InnoDB;
