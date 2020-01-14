package fr.paris.lutece.plugins.enroll.web;

import fr.paris.lutece.plugins.enroll.business.enrollment.Enrollment;
import fr.paris.lutece.plugins.enroll.business.enrollment.EnrollmentHome;
import fr.paris.lutece.plugins.enroll.business.Project;
import fr.paris.lutece.plugins.enroll.business.ProjectHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;

import java.util.Map;
import java.util.List;
import java.util.Collection;
import fr.paris.lutece.util.ReferenceList;
import java.util.HashMap;
import java.util.Locale;
import fr.paris.lutece.util.html.HtmlTemplate;
import javax.servlet.http.HttpServletRequest;
import fr.paris.lutece.portal.service.template.AppTemplateService;

@Controller( xpageName = "enrollment" , pageTitleI18nKey = "enroll.xpage.enrollment.pageTitle" , pagePathI18nKey = "enroll.xpage.enrollment.pagePathLabel" )
public class EnrollmentXPage extends MVCApplication {

  private static final String TEMPLATE_MANAGE_ENROLLMENTS="/skin/plugins/enroll/manage_enrollments.html";
  private static final String TEMPLATE_CREATE_ENROLLMENT="/skin/plugins/enroll/create_enrollment.html";
  private static final String TEMPLATE_MODIFY_ENROLLMENT="/skin/plugins/enroll/modify_enrollment.html";
  private static final String TEMPLATE_ENROLLMENT_CREATED="/skin/plugins/enroll/enrollment_created.html";

  // JSP
  private static final String JSP_PAGE_PORTAL = "jsp/site/Portal.jsp";

  // Parameters
  private static final String PARAMETER_ID_ENROLLMENT="id";
  private static final String PARAM_ACTION = "action";
  private static final String PARAM_PAGE = "page";
  private static final String PARAMETER_PAGE_ID="page_id";

  private static final String MARK_ENROLLMENT_LIST = "enrollment_list";
  private static final String MARK_ENROLLMENT = "enrollment";
  private static final String MARK_LIST_PROJECTS = "refListProjects";

  private static final String MESSAGE_CONFIRM_REMOVE_ENROLLMENT = "enroll.message.confirmRemoveEnrollment";

  private static final String VIEW_MANAGE_ENROLLMENTS = "manageEnrollments";
  private static final String VIEW_CREATE_ENROLLMENT = "createEnrollment";
  private static final String VIEW_MODIFY_ENROLLMENT = "modifyEnrollment";

  private static final String ACTION_CREATE_ENROLLMENT = "createEnrollment";
  private static final String ACTION_MODIFY_ENROLLMENT = "modifyEnrollment";
  private static final String ACTION_REMOVE_ENROLLMENT = "removeEnrollment";
  private static final String ACTION_CONFIRM_REMOVE_ENROLLMENT = "confirmRemoveEnrollment";

  private static final String INFO_ENROLLMENT_CREATED = "enroll.info.enrollment.createdModify";
  private static final String INFO_ENROLLMENT_UPDATED = "enroll.info.enrollment.updated";
  private static final String INFO_ENROLLMENT_REMOVED = "enroll.info.enrollment.removed";

  private Enrollment _enrollment;

  @View( VIEW_CREATE_ENROLLMENT )
  public XPage getCreateEnrollment( HttpServletRequest request )
  {
      if (_enrollment == null) {
        _enrollment = new Enrollment();
      }

      Map<String, Object> model = getModel(  );
      model.put( MARK_ENROLLMENT, _enrollment );
      Collection<Project> listProjects = ProjectHome.getProjectsList( );
      ReferenceList refListProjects = new ReferenceList( );
      for ( Project project : listProjects )
      {
          if (project.getActive() == 1) {
            refListProjects.addItem( project.getId( ), project.getName( ) );
          }
      }
      model.put( MARK_LIST_PROJECTS, refListProjects );

      return getXPage( TEMPLATE_CREATE_ENROLLMENT, request.getLocale(  ), model );
  }

  @Action( ACTION_CREATE_ENROLLMENT )
  public XPage doCreateEnrollment( HttpServletRequest request )
  {
      _enrollment = new Enrollment(  );

      populate( _enrollment, request );

      // Check constraints
      if ( !validateBean( _enrollment, getLocale( request ) ) )
      {
          return redirectView( request, VIEW_CREATE_ENROLLMENT );
      }

      EnrollmentHome.create( _enrollment );

      List<Project> listProjects = ProjectHome.getProjectsList();
      for (Project project : listProjects) {
        if (project.getName().equals(_enrollment.getProgram())) {
          project.setCurrentSize(project.getCurrentSize()+1);
          if (project.getSize() > 0) {
            if (project.getCurrentSize() == project.getSize() && project.getActive() == 1) {
              project.setActive(0);
            }
          }
          ProjectHome.update(project);
        }
      }

      addInfo( INFO_ENROLLMENT_CREATED, getLocale( request ) );

      Map<String, Object> model = getModel(  );
      return getXPage( TEMPLATE_ENROLLMENT_CREATED, request.getLocale(  ), model );
      //return redirect( request, "Portal.jsp" );

      //return redirectView( request, VIEW_MODIFY_ENROLLMENT );
  }

  /**
   * Get the HTML content of the enrollment form
   *
   * @param request
   *            The request
   * @param locale
   *            The locale
   * @return The HTML content
   */
  public static String getEnrollmentHtml( HttpServletRequest request, Locale locale )
  {
      Collection<Project> listProjects = ProjectHome.getProjectsList( );
      ReferenceList refListProjects = new ReferenceList( );
      for ( Project project : listProjects )
      {
          if (project.getActive() == 1) {
            refListProjects.addItem( project.getId( ), project.getName( ) );
          }
      }
      Map<String, Object> model = new HashMap<String, Object>( );
      model.put( MARK_LIST_PROJECTS, refListProjects );

      HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_ENROLLMENT, locale, model );
      return template.getHtml( );
  }
}
