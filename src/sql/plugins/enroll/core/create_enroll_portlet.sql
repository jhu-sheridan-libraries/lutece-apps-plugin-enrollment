
--
-- Structure for table enroll_portlet
--
DROP TABLE IF EXISTS enroll_portlet;
CREATE TABLE enroll_portlet (
  id_project INT NOT NULL,
  id_portlet int default '0' NOT NULL,
  enroll_feed_id varchar(100) default NULL,
  PRIMARY KEY  (id_portlet)
);
