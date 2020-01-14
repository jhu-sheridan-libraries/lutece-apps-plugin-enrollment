<jsp:useBean id="manageenrollEnrollment" scope="session" class="fr.paris.lutece.plugins.enroll.web.EnrollmentsJspBean" />
<% String strContent = manageenrollEnrollment.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
