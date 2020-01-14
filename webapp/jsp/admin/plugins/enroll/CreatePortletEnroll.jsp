
<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />

<jsp:useBean id="EnrollPortlet" scope="session" class="fr.paris.lutece.plugins.enroll.web.portlet.EnrollPortletJspBean" />

<% EnrollPortlet.init( request, EnrollPortlet.RIGHT_MANAGE_ADMIN_SITE); %>
<%= EnrollPortlet.getCreate ( request ) %>

<%@ include file="../../AdminFooter.jsp" %>


