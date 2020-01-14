package fr.paris.lutece.plugins.enroll.web;

import fr.paris.lutece.plugins.enroll.business.enrollment.Enrollment;
import fr.paris.lutece.plugins.enroll.business.enrollment.EnrollmentHome;
import fr.paris.lutece.plugins.enroll.business.Project;
import fr.paris.lutece.plugins.enroll.business.ProjectHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import fr.paris.lutece.util.ReferenceList;
import java.util.Map;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;

/**
 * This class provides the user interface to manage Project features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageEnrollments.jsp", controllerPath = "jsp/admin/plugins/enroll/", right = "ENROLL_MANAGEMENT" )
public class EnrollmentsJspBean extends ManageEnrollJspBean
{
    // Templates
    private static final String TEMPLATE_MANAGE_ENROLLMENTS="/skin/plugins/enroll/manage_enrollments.html";
    private static final String TEMPLATE_CREATE_ENROLLMENT="/skin/plugins/enroll/create_enrollment.html";
    private static final String TEMPLATE_MODIFY_ENROLLMENT="/skin/plugins/enroll/modify_enrollment.html";

    // Parameters
    private static final String PARAMETER_ID_ENROLLMENT = "id";
    private static final String PARAMETER_ID_PROJECT = "projectId";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_ENROLLMENTS = "enroll.manage_enrollments.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_ENROLLMENT = "enroll.modify_enrollment.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_ENROLLMENT = "enroll.create_enrollment.pageTitle";

    // Markers
    private static final String MARK_ENROLLMENT_LIST = "enrollment_list";
    private static final String MARK_ENROLLMENT = "enrollment";
    private static final String MARK_PROJECT = "project";
    private static final String MARK_LIST_PROJECTS = "refListProjects";

    private static final String JSP_MANAGE_ENROLLMENTS = "jsp/admin/plugins/enroll/ManageEnroll.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_ENROLLMENT = "enroll.message.confirmRemoveEnrollment";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "enroll.model.entity.enrollment.attribute.";

    // Views
    private static final String VIEW_MANAGE_ENROLLMENTS = "manageEnrollments";
    private static final String VIEW_CREATE_ENROLLMENT = "createEnrollment";
    private static final String VIEW_MODIFY_ENROLLMENT = "modifyEnrollment";

    // Actions
    private static final String ACTION_CREATE_ENROLLMENT = "createEnrollment";
    private static final String ACTION_MODIFY_ENROLLMENT = "modifyEnrollment";
    private static final String ACTION_REMOVE_ENROLLMENT = "removeEnrollment";
    private static final String ACTION_CONFIRM_REMOVE_ENROLLMENT = "confirmRemoveEnrollment";

    // Infos
    private static final String INFO_ENROLLMENT_CREATED = "enroll.info.enrollment.created";
    private static final String INFO_ENROLLMENT_UPDATED = "enroll.info.enrollment.updated";
    private static final String INFO_ENROLLMENT_REMOVED = "enroll.info.enrollment.removed";

    // Session variable to store working values
    private Enrollment _enrollment = null;

    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_ENROLLMENTS, defaultView = true )
    public String getManageEnrollments( HttpServletRequest request )
    {
        int _projectId = Integer.parseInt(request.getParameter ( PARAMETER_ID_PROJECT ));
        String _projectName = "";
        List<Project> listProjects = ProjectHome.getProjectsList();
        for (Project project : listProjects) {
          if (project.getId() == _projectId) {
            _projectName = project.getName();
          }
        }
        List<Enrollment> listEnrollments = EnrollmentHome.getEnrollmentsList(  );
        List<Enrollment> sortedEnrollments = new ArrayList<Enrollment>();
        for (Enrollment enrollment : listEnrollments) {
          if (enrollment.getProgram().equals(_projectName)) {
            sortedEnrollments.add(enrollment);
          }
        }
        Map<String, Object> model = getPaginatedListModel( request, MARK_ENROLLMENT_LIST, sortedEnrollments, JSP_MANAGE_ENROLLMENTS );

        model.put( MARK_PROJECT, _projectId);
        return getPage( PROPERTY_PAGE_TITLE_MANAGE_ENROLLMENTS, TEMPLATE_MANAGE_ENROLLMENTS, model );
    }

    /**
     * Returns the form to create a enrollment
     *
     * @param request The Http request
     * @return the html code of the enrollment form
     */
    @View( VIEW_CREATE_ENROLLMENT )
    public String getCreateEnrollment( HttpServletRequest request )
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

      return getPage( PROPERTY_PAGE_TITLE_CREATE_ENROLLMENT, TEMPLATE_CREATE_ENROLLMENT, model );
    }

    /**
     * Process the data capture form of a new enrollment
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_ENROLLMENT )
    public String doCreateEnrollment( HttpServletRequest request )
    {

      _enrollment = new Enrollment(  );

      populate( _enrollment, request );

      // Check constraints
      if ( !validateBean( _enrollment, VALIDATION_ATTRIBUTES_PREFIX ) )
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

      addInfo( INFO_ENROLLMENT_CREATED, getLocale(  ) );

      return redirectView( request, VIEW_CREATE_ENROLLMENT );
    }

    /**
     * Manages the removal form of an enrollment whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_ENROLLMENT )
    public String getConfirmRemoveEnrollment( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_ENROLLMENT ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_ENROLLMENT ) );
        url.addParameter( PARAMETER_ID_ENROLLMENT, nId );
        int projectId = Integer.parseInt( request.getParameter( PARAMETER_ID_PROJECT ) );
        url.addParameter( PARAMETER_ID_PROJECT, projectId );


        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_ENROLLMENT, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of an enrollment
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage projects
     */
    @Action( ACTION_REMOVE_ENROLLMENT )
    public String doRemoveEnrollment( HttpServletRequest request )
    {

        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_ENROLLMENT ) );
        int projectId = Integer.parseInt( request.getParameter( PARAMETER_ID_PROJECT ) );
        List<Enrollment> listEnroll = EnrollmentHome.getEnrollmentsList();
        for (Enrollment enroll : listEnroll) {
          if (enroll.getId() == nId) {
            _enrollment = enroll;
          }
        }

        List<Project> listProjects = ProjectHome.getProjectsList();
        for (Project project : listProjects) {
          if ((project.getName()).equals(_enrollment.getProgram())) {
            project.setCurrentSize(project.getCurrentSize()-1);
            ProjectHome.update(project);
          }
        }

        EnrollmentHome.remove( nId );

        addInfo( INFO_ENROLLMENT_REMOVED, getLocale(  ) );

        String _projectName = "";
        listProjects = ProjectHome.getProjectsList();
        for (Project project : listProjects) {
          if (project.getId() == projectId) {
            _projectName = project.getName();
          }
        }
        List<Enrollment> listEnrollments = EnrollmentHome.getEnrollmentsList(  );
        List<Enrollment> sortedEnrollments = new ArrayList<Enrollment>();
        for (Enrollment enrollment : listEnrollments) {
          if (enrollment.getProgram().equals(_projectName)) {
            sortedEnrollments.add(enrollment);
          }
        }
        Map<String, Object> model = getPaginatedListModel( request, MARK_ENROLLMENT_LIST, sortedEnrollments, JSP_MANAGE_ENROLLMENTS );
        model.put( MARK_PROJECT, projectId);
        return getPage( PROPERTY_PAGE_TITLE_MANAGE_ENROLLMENTS, TEMPLATE_MANAGE_ENROLLMENTS, model );
    }
}
