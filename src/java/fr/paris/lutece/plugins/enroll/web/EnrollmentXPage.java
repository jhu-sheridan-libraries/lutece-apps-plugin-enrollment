package fr.paris.lutece.plugins.enroll.web;

import fr.paris.lutece.plugins.enroll.business.enrollment.Enrollment;
import fr.paris.lutece.plugins.enroll.business.enrollment.EnrollmentHome;
import fr.paris.lutece.plugins.enroll.business.project.Project;
import fr.paris.lutece.plugins.enroll.business.project.ProjectHome;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;

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

  private static final String TEMPLATE_CREATE_ENROLLMENT="/skin/plugins/enroll/create_enrollment.html";
  private static final String TEMPLATE_ENROLLMENT_RESULT="skin/plugins/enroll/enrollment_result.html";

  // Parameters
  private static final String MARK_ENROLLMENT = "enrollment";
  private static final String MARK_LIST_PROJECTS = "refListProjects";

  private static final String VIEW_CREATE_ENROLLMENT = "createEnrollment";
  private static final String ACTION_CREATE_ENROLLMENT = "createEnrollment";

  private Enrollment _enrollment;

  @Action( ACTION_CREATE_ENROLLMENT )
  public XPage doCreateEnrollment( HttpServletRequest request )  {
      _enrollment = new Enrollment(  );
      populate( _enrollment, request );

      // Check constraints
      if ( !validateBean( _enrollment, getLocale( request ) ) )
      {
          return redirectView( request, VIEW_CREATE_ENROLLMENT );
      }

      Map<String, Object> model = getModel();
      List<Project> listProjects = ProjectHome.getProjectsList();

      for (Project project : listProjects) {
          if (project.getName().equals(_enrollment.getProgram())) {
              if ( project.canAdd() ) {
                    EnrollmentHome.create(_enrollment);
                    project.setCurrentSize(project.getCurrentSize() + 1);
                    ProjectHome.update(project);
                    model.put("success", true);
                } else {
                    model.put("inactive", project.getActive()==0);
                    model.put( "full", project.atCapacity());
              }
              break;
          }
      }
      return getXPage( TEMPLATE_ENROLLMENT_RESULT, request.getLocale(), model);
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
          if (project.canAdd() ) {
            refListProjects.addItem( project.getId( ), project.getName( ) );
          }
      }
      Map<String, Object> model = new HashMap<>( );
      model.put( MARK_LIST_PROJECTS, refListProjects );

      HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_ENROLLMENT, locale, model );
      return template.getHtml( );
  }
}
