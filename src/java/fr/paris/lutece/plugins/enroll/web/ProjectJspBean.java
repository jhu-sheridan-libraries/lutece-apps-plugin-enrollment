 /*
  * @author Evan Hsia, Sofya Freyman
  */
package fr.paris.lutece.plugins.enroll.web;

import fr.paris.lutece.plugins.enroll.business.Project;
import fr.paris.lutece.plugins.enroll.business.ProjectHome;
import fr.paris.lutece.plugins.enroll.business.enrollment.Enrollment;
import fr.paris.lutece.plugins.enroll.business.enrollment.EnrollmentHome;
import org.apache.commons.lang.StringUtils;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;

import java.util.List;
import java.util.Collection;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import java.awt.datatransfer.*;
import java.awt.Toolkit;

/**
 * This class provides the user interface to manage Project features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageProjects.jsp", controllerPath = "jsp/admin/plugins/enroll/", right = "ENROLL_MANAGEMENT" )
public class ProjectJspBean extends ManageEnrollJspBean
{
    // Templates
    private static final String TEMPLATE_MANAGE_PROJECTS = "/admin/plugins/enroll/manage_projects.html";
    private static final String TEMPLATE_CREATE_PROJECT = "/admin/plugins/enroll/create_project.html";
    private static final String TEMPLATE_MODIFY_PROJECT = "/admin/plugins/enroll/modify_project.html";
    private static final String TEMPLATE_EMAIL_ALL = "/admin/plugins/enroll/email_all.html";

    // Parameters
    private static final String PARAMETER_ID_PROJECT = "id";
    private static final String PARAMETER_EMAILS = "emails";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_PROJECTS = "enroll.manage_projects.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_PROJECT = "enroll.modify_project.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_PROJECT = "enroll.create_project.pageTitle";

    // Markers
    private static final String MARK_PROJECT_LIST = "project_list";
    private static final String MARK_PROJECT = "project";

    private static final String JSP_MANAGE_PROJECTS = "jsp/admin/plugins/enroll/ManageProjects.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_PROJECT = "enroll.message.confirmRemoveProject";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "enroll.model.entity.project.attribute.";

    // Views
    private static final String VIEW_MANAGE_PROJECTS = "manageProjects";
    private static final String VIEW_CREATE_PROJECT = "createProject";
    private static final String VIEW_MODIFY_PROJECT = "modifyProject";

    // Actions
    private static final String ACTION_CREATE_PROJECT = "createProject";
    private static final String ACTION_MODIFY_PROJECT = "modifyProject";
    private static final String ACTION_REMOVE_PROJECT = "removeProject";
    private static final String ACTION_CONFIRM_REMOVE_PROJECT = "confirmRemoveProject";
    private static final String ACTION_CHANGE_STATUS = "doChangeProjectStatus";
    private static final String ACTION_COPY_EMAILS = "copyEmails";

    // Infos
    private static final String INFO_PROJECT_CREATED = "enroll.info.project.created";
    private static final String INFO_PROJECT_UPDATED = "enroll.info.project.updated";
    private static final String INFO_PROJECT_REMOVED = "enroll.info.project.removed";
    private static final String INFO_INCREASE_SIZE = "enroll.info.project.increase";
    private static final String INFO_SIZE_IS_SMALL = "enroll.info.project.sizesmall";
    private static final String INFO_PROJECT_SAME_NAME = "enroll.info.project.samename";
    private static final String INFO_EMAILS_COPIED = "enroll.info.project.emailscopied";

    // Session variable to store working values
    private Project _project;

    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_PROJECTS, defaultView = true )
    public String getManageProjects( HttpServletRequest request )
    {
        //_project = null;
        List<Project> listProjects = ProjectHome.getProjectsList(  );
        Map<String, Object> model = getPaginatedListModel( request, MARK_PROJECT_LIST, listProjects, JSP_MANAGE_PROJECTS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_PROJECTS, TEMPLATE_MANAGE_PROJECTS, model );
    }

    /**
     * Returns the form to create a project
     *
     * @param request The Http request
     * @return the html code of the project form
     */
    @View( VIEW_CREATE_PROJECT )
    public String getCreateProject( HttpServletRequest request )
    {
        _project = new Project();

        Map<String, Object> model = getModel(  );
        model.put( MARK_PROJECT, _project );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_PROJECT, TEMPLATE_CREATE_PROJECT, model );
    }

    /**
     * Process the data capture form of a new project
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_PROJECT )
    public String doCreateProject( HttpServletRequest request )
    {

        _project = new Project();

        populate( _project, request );

        // Check constraints
        if ( !validateBean( _project, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_PROJECT );
        }
        Collection<Project> listProjects = ProjectHome.getProjectsList( );
        for ( Project project : listProjects )
        {
          if (project.getName().equals(_project.getName())) {
            addInfo( INFO_PROJECT_SAME_NAME, getLocale(  ) );
            return redirectView( request, VIEW_MANAGE_PROJECTS );
          }
        }

        ProjectHome.create( _project );
        addInfo( INFO_PROJECT_CREATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_PROJECTS );
    }

    @Action( ACTION_CHANGE_STATUS )
    public String doChangeProjectStatus( HttpServletRequest request )
    {
      String strIdProject = request.getParameter( PARAMETER_ID_PROJECT );
      int nIdProject = Integer.parseInt( strIdProject );
      Project project = ProjectHome.findByPrimaryKey( nIdProject );
      if (project.getActive() == 0 && project.getSize() != 0 && project.getSize() == project.getCurrentSize()) {
        addInfo( INFO_INCREASE_SIZE, getLocale(  ) );
        return redirectView( request, VIEW_MANAGE_PROJECTS );
      }
      project.flipActive();
      ProjectHome.update( project );

      return redirectView( request, VIEW_MANAGE_PROJECTS );
    }

    /**
     * Manages the removal form of a project whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_PROJECT )
    public String getConfirmRemoveProject( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_PROJECT ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_PROJECT ) );
        url.addParameter( PARAMETER_ID_PROJECT, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_PROJECT, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );


        return redirect( request, strMessageUrl );
    }

    @Action( ACTION_COPY_EMAILS )
    public String copyEmails( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_PROJECT ) );

        String _projectName = ProjectHome.findByPrimaryKey(nId).getName();
        String result = "";

        List<Enrollment> listEnrollments = EnrollmentHome.getEnrollmentsList(  );
        for (Enrollment enrollment : listEnrollments) {
          if (enrollment.getProgram().equals(_projectName)) {
            result = result + enrollment.getEmail() + ", ";
          }
        }
        if (result.length() > 0) {
          result = result.substring(0, result.length()-2);
        }

        Map<String, Object> model = getModel(  );
        model.put( PARAMETER_EMAILS , result );

        return getPage( " ", TEMPLATE_EMAIL_ALL, model );
    }

    /**
     * Handles the removal form of a project
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage projects
     */
    @Action( ACTION_REMOVE_PROJECT )
    public String doRemoveProject( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_PROJECT ) );
        String _projectName = ProjectHome.findByPrimaryKey(nId).getName();
        List<Enrollment> listEnrollments = EnrollmentHome.getEnrollmentsList(  );
        for (int i = 0; i < listEnrollments.size(); i++) {
          if (listEnrollments.get(i).getProgram().equals(_projectName)) {
            EnrollmentHome.remove(listEnrollments.get(i).getId());
          }
        }
        ProjectHome.remove( nId );
        addInfo( INFO_PROJECT_REMOVED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_PROJECTS );
    }

    /**
     * Returns the form to update info about a project
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_PROJECT )
    public String getModifyProject( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_PROJECT ) );

        _project = ProjectHome.findByPrimaryKey( nId );

        Map<String, Object> model = getModel(  );
        model.put( MARK_PROJECT, _project );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_PROJECT, TEMPLATE_MODIFY_PROJECT, model );
    }

    /**
     * Process the change form of a project
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_PROJECT )
    public String doModifyProject( HttpServletRequest request )
    {
        int tempSize = _project.getSize();
        int tempActive = _project.getActive();
        String temp = _project.getName();
        populate( _project, request );

        // Check constraints
        if ( !validateBean( _project, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_PROJECT, PARAMETER_ID_PROJECT, _project.getId( ) );
        }
        if (_project.getSize() == 0) {
          if (!(temp.equals(_project.getName()))) {
            Collection<Project> listProjects = ProjectHome.getProjectsList( );
            for ( Project project : listProjects )
            {
              if (project.getName().equals(_project.getName())) {
                addInfo( INFO_PROJECT_SAME_NAME, getLocale(  ) );
                return redirectView( request, VIEW_MANAGE_PROJECTS );
              }
            }
          }
          ProjectHome.update( _project );
          addInfo( INFO_PROJECT_UPDATED, getLocale(  ) );
          return redirectView( request, VIEW_MANAGE_PROJECTS );
        }
        if (_project.getSize() < _project.getCurrentSize()) {
          addInfo( INFO_SIZE_IS_SMALL, getLocale(  ) );
          return redirect( request, VIEW_MODIFY_PROJECT, PARAMETER_ID_PROJECT, _project.getId( ) );
        }
        if (_project.getSize() == _project.getCurrentSize()) {
          _project.flipActive();
          if (!(temp.equals(_project.getName()))) {
            Collection<Project> listProjects = ProjectHome.getProjectsList( );
            for ( Project project : listProjects )
            {
              if (project.getName().equals(_project.getName())) {
                addInfo( INFO_PROJECT_SAME_NAME, getLocale(  ) );
                return redirectView( request, VIEW_MANAGE_PROJECTS );
              }
            }
          }
          ProjectHome.update( _project );
          addInfo( INFO_PROJECT_UPDATED, getLocale(  ) );
          return redirectView( request, VIEW_MANAGE_PROJECTS );
        }
        if (_project.getSize() > tempSize && _project.getSize() > _project.getCurrentSize() && tempActive == 0) {
          _project.flipActive();
          if (!(temp.equals(_project.getName()))) {
            Collection<Project> listProjects = ProjectHome.getProjectsList( );
            for ( Project project : listProjects )
            {
              if (project.getName().equals(_project.getName())) {
                addInfo( INFO_PROJECT_SAME_NAME, getLocale(  ) );
                return redirectView( request, VIEW_MANAGE_PROJECTS );
              }
            }
          }
          ProjectHome.update( _project );
          addInfo( INFO_PROJECT_UPDATED, getLocale(  ) );
          return redirectView( request, VIEW_MANAGE_PROJECTS );
        }
        if (!(temp.equals(_project.getName()))) {
          Collection<Project> listProjects = ProjectHome.getProjectsList( );
          for ( Project project : listProjects )
          {
            if (project.getName().equals(_project.getName())) {
              addInfo( INFO_PROJECT_SAME_NAME, getLocale(  ) );
              return redirectView( request, VIEW_MANAGE_PROJECTS );
            }
          }
        }

        ProjectHome.update( _project );
        addInfo( INFO_PROJECT_UPDATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_PROJECTS );
    }
}
