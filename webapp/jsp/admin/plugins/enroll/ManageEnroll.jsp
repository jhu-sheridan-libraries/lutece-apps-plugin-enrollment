<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="manageenroll" scope="session" class="fr.paris.lutece.plugins.enroll.web.ManageEnrollJspBean" />

<% manageenroll.init( request, manageenroll.RIGHT_MANAGEENROLL ); %>
<%= manageenroll.getManageEnrollHome ( request ) %>

<%@ include file="../../AdminFooter.jsp" %>
