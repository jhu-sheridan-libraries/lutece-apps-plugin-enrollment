package fr.paris.lutece.plugins.enroll.web;

import fr.paris.lutece.plugins.enroll.business.enrollment.Enrollment;
import fr.paris.lutece.plugins.enroll.business.enrollment.EnrollmentHome;
import fr.paris.lutece.plugins.enroll.business.project.Project;
import fr.paris.lutece.plugins.enroll.business.project.ProjectHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * This class provides the user interface to manage Enrollment features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageEnrollments.jsp", controllerPath = "jsp/admin/plugins/enroll/", right = "ENROLL_MANAGEMENT" )
public class EnrollmentsJspBean extends ManageEnrollJspBean
{
    // Template
    private static final String TEMPLATE_MANAGE_ENROLLMENTS="/admin/plugins/enroll/manage_enrollments.html";
    private static final String TEMPLATE_MODIFY_ENROLLMENT="/admin/plugins/enroll/modify_enrollment.html";
    private static final String TEMPLATE_ADD_ENROLLMENT_TO_PROJECT="/admin/plugins/enroll/add_enrollment_to_project.html";

    // Parameters
    protected static final String PARAMETER_ID_ENROLLMENT = "id";
    protected static final String PARAMETER_NAME_ENROLLMENT = "name";
    protected static final String PARAMETER_PROGRAM_ENROLLMENT = "program";
    protected static final String PARAMETER_PHONE_ENROLLMENT = "phone";
    protected static final String PARAMETER_EMAIL_ENROLLMENT = "email";
    protected static final String PARAMETER_ID_PROJECT = "projectId";


    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_ENROLLMENTS = "enroll.manage_enrollments.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_ENROLLMENT = "enroll.modify_enrollment.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_ENROLLMENT = "enroll.create_enrollment.pageTitle";

    // Markers
    private static final String MARK_ENROLLMENT_LIST = "enrollment_list";
    private static final String MARK_ENROLLMENT = "enrollment";
    private static final String MARK_PROJECT = "project";

    private static final String JSP_MANAGE_ENROLLMENTS = "jsp/admin/plugins/enroll/ManageEnroll.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_ENROLLMENT = "enroll.message.confirmRemoveEnrollment";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "enroll.model.entity.enrollment.attribute.";

    // Views
    private static final String VIEW_MANAGE_ENROLLMENTS = "manageEnrollments";
    private static final String VIEW_MODIFY_ENROLLMENT = "modifyEnrollment";
    private static final String VIEW_ADD_ENROLLMENT_TO_PROJECT = "addEnrollmentToProject";

    // Actions
    private static final String ACTION_MODIFY_ENROLLMENT = "modifyEnrollment";
    private static final String ACTION_REMOVE_ENROLLMENT = "removeEnrollment";
    private static final String ACTION_CONFIRM_REMOVE_ENROLLMENT = "confirmRemoveEnrollment";
    private static final String ACTION_ADD_ENROLLMENT_TO_PROJECT = "addEnrollmentToProject";

    // Infos
    private static final String INFO_ENROLLMENT_CREATED = "enroll.info.enrollment.created";
    private static final String INFO_ENROLLMENT_UPDATED = "enroll.info.enrollment.updated";
    private static final String INFO_ENROLLMENT_REMOVED = "enroll.info.enrollment.removed";
    private static final String INFO_ENROLLMENT_FAILED =  "enroll.info.enrollment.failed";
    private static final String INFO_PROJECT_INACTIVE = "enroll.info.project.inactive";
    private static final String INFO_PROJECT_FULL = "enroll.info.project.full";
    private static final String INFO_PROJECT_UPDATED = "enroll.info.project.updated";
    private static final String INFO_INCREASE_SIZE = "enroll.info.project.increase";

    // Session variable to store working values
    private Enrollment _enrollment = null;

    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_ENROLLMENTS, defaultView = true )
    public String getManageEnrollments( HttpServletRequest request ) {
        int projectId = Integer.parseInt(request.getParameter(PARAMETER_ID_PROJECT));
        String projectName = "";

        Project project = ProjectHome.findByPrimaryKey(projectId);
        if (project != null) {
            projectName = project.getName();
        }

        Map<String, Object> model = getPaginatedListModel(request, MARK_ENROLLMENT_LIST, EnrollmentHome.getEnrollmentsForProgram(projectName), JSP_MANAGE_ENROLLMENTS);

        model.put(MARK_PROJECT, projectId);
        return getPage(PROPERTY_PAGE_TITLE_MANAGE_ENROLLMENTS, TEMPLATE_MANAGE_ENROLLMENTS, model);
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

        Enrollment enrollment = EnrollmentHome.findByPrimaryKey(nId);
        Project project = ProjectHome.findByPrimaryKey(projectId);

        if ( enrollment != null && project != null && project.getName().equals(enrollment.getProgram())) {
            EnrollmentHome.remove(enrollment.getId());
            project.setCurrentSize(project.getCurrentSize()-1);
            ProjectHome.update(project);
        }

        addInfo( INFO_ENROLLMENT_REMOVED, getLocale(  ) );

        Map<String, Object> model = getPaginatedListModel( request, MARK_ENROLLMENT_LIST, EnrollmentHome.getEnrollmentsForProgram(project.getName()), JSP_MANAGE_ENROLLMENTS );
        model.put( MARK_PROJECT, projectId);
        return getPage( PROPERTY_PAGE_TITLE_MANAGE_ENROLLMENTS, TEMPLATE_MANAGE_ENROLLMENTS, model );
    }

    /**
     * Returns the form to update info about an enrollment
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_ENROLLMENT )
    public String getModifyEnrollment( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_ENROLLMENT ) );
        int projectId = Integer.parseInt( request.getParameter( PARAMETER_ID_PROJECT ) );
        _enrollment = EnrollmentHome.findByPrimaryKey( nId );

        Map<String, Object> model = getModel(  );
        model.put( MARK_ENROLLMENT, _enrollment );
        model.put( PARAMETER_ID_PROJECT, projectId);
        return getPage( PROPERTY_PAGE_TITLE_MODIFY_ENROLLMENT, TEMPLATE_MODIFY_ENROLLMENT, model );
    }

    /**
     * Process the change form of an enrollment
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_ENROLLMENT )
    public String doModifyEnrollment( HttpServletRequest request )
    {
        int projectId = Integer.parseInt( request.getParameter( PARAMETER_ID_PROJECT ) );
        _enrollment = new Enrollment();
        populate( _enrollment, request );

        // Check constraints
        if ( !validateBean( _enrollment, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_ENROLLMENT, PARAMETER_ID_ENROLLMENT, _enrollment.getId( ) );
        }

        EnrollmentHome.update( _enrollment );
        addInfo( INFO_ENROLLMENT_UPDATED, getLocale(  ) );
        return redirect( request, VIEW_MANAGE_ENROLLMENTS, PARAMETER_ID_PROJECT, projectId);
    }

    /**
     * Returns the form to add an enrollment to a project
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_ADD_ENROLLMENT_TO_PROJECT )
    public String getAddEnrollmentToProject( HttpServletRequest request )
    {
        int projectId = Integer.parseInt( request.getParameter( PARAMETER_ID_PROJECT ) );
        Project project = ProjectHome.findByPrimaryKey( projectId );
        if ( !project.canAdd() ) {
            if ( !project.hasRoom() ) {
                addWarning( INFO_PROJECT_FULL, getLocale() );
                addWarning( INFO_INCREASE_SIZE, getLocale() );
            }
            if ( project.getActive()!=1 ) {
                addWarning( INFO_PROJECT_INACTIVE );
            }
            return redirect( request, VIEW_MANAGE_ENROLLMENTS, PARAMETER_ID_PROJECT, projectId);
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_PROJECT, project );
        return getPage( PROPERTY_PAGE_TITLE_CREATE_ENROLLMENT, TEMPLATE_ADD_ENROLLMENT_TO_PROJECT, model );
    }

    /**
     * Process the addition form of an enrollment to a project
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_ADD_ENROLLMENT_TO_PROJECT )
    public String doAddEnrollmentToProject( HttpServletRequest request )
    {
        int projectId = Integer.parseInt( request.getParameter( PARAMETER_ID_PROJECT ) );
        Project project = ProjectHome.findByPrimaryKey(projectId);
        _enrollment = new Enrollment();
        populate( _enrollment, request );

        // Check constraints
        if ( !validateBean( _enrollment, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_ENROLLMENT, PARAMETER_ID_ENROLLMENT, _enrollment.getId( ) );
        }

        //add enrollment if the project is active and has room
        if ( project.canAdd()) {
                EnrollmentHome.create(_enrollment);
                project.setCurrentSize(project.getCurrentSize() + 1);
                ProjectHome.update(project);
                addInfo(INFO_ENROLLMENT_CREATED, getLocale());
                addInfo(INFO_PROJECT_UPDATED, getLocale());
            } else {
                addWarning ( INFO_ENROLLMENT_FAILED, getLocale() );
                if ( project.getActive() != 1) {
                    addWarning ( INFO_PROJECT_INACTIVE, getLocale() );
                }
                if ( !project.hasRoom()) {
                    addWarning ( INFO_PROJECT_FULL, getLocale() );
                }
            }

        return redirect( request, VIEW_MANAGE_ENROLLMENTS, PARAMETER_ID_PROJECT, projectId);

    }

    // override here changes access, allows us to mock this method in tests
    @Override
    protected String redirect( HttpServletRequest request, String strView, String strParameter, int nValue ) {
        return super.redirect( request, strView, strParameter, nValue );
    }

    @Override
    protected String getPage(String strPageTitleProperty, String strTemplate, Map<String, Object> model) {
        return super.getPage(strPageTitleProperty, strTemplate, model);
    }

}
