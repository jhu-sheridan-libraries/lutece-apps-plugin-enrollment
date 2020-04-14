--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'ENROLL_MANAGEMENT';
INSERT INTO core_admin_right VALUES ('ENROLL_MANAGEMENT','enroll.adminFeature.ManageEnroll.name',0,'jsp/admin/plugins/enroll/ManageProjects.jsp','enroll.adminFeature.ManageEnroll.description',0,'enroll','APPLICATIONS',null,null,4,0);

--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'ENROLL_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('ENROLL_MANAGEMENT',1);

--
-- Data for table core_portlet_type
--

DELETE FROM core_portlet_type WHERE id_portlet_type = 'ENROLL_PORTLET';
INSERT INTO core_portlet_type VALUES ('ENROLL_PORTLET','enroll.portlet.enrollEnrollPortlet.name','plugins/enroll/CreatePortletEnroll.jsp','plugins/enroll/ModifyPortletEnroll.jsp','fr.paris.lutece.plugins.enroll.business.portlet.EnrollPortletHome','enroll','plugins/enroll/DoCreatePortletEnroll.jsp','/admin/portlet/script_create_portlet.html','/admin/plugins/enroll/portlet/create_portletenroll.html','','plugins/enroll/DoModifyPortletEnroll.jsp','/admin/portlet/script_modify_portlet.html','/admin/plugins/enroll/portlet/modify_portletenroll.html','');
