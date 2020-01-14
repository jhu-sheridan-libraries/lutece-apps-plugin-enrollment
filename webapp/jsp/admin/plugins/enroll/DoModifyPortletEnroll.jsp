<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="EnrollPortlet" scope="session" class="fr.paris.lutece.plugins.enroll.web.portlet.EnrollPortletJspBean" />

<%
    EnrollPortlet.init( request, EnrollPortlet.RIGHT_MANAGE_ADMIN_SITE );
    response.sendRedirect( EnrollPortlet.doModify( request ) );
%>


