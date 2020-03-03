package fr.paris.lutece.plugins.enroll.web;

import fr.paris.lutece.plugins.enroll.business.project.Project;
import fr.paris.lutece.plugins.enroll.business.project.ProjectHome;
import fr.paris.lutece.plugins.enroll.service.EnrollPlugin;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.dashboard.DashboardComponent;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnrollmentDashboardComponent extends DashboardComponent {

        // MARKS
        private static final String MARK_ICON = "icon";

        // TEMPLATES
        private static final String TEMPLATE_DASHBOARD = "/admin/plugins/enroll/manage_projects.html";


        ProjectJspBean projectJspBean = SpringContextService.getBean("enroll.ProjectJspBean");
        List<Project> listProjects = ProjectHome.getProjectsList(  );


        /**
         * {@inheritDoc}
         */
        @Override
        public String getDashboardData(AdminUser user, HttpServletRequest request )
        {
            Map<String, Object> model = new HashMap<String, Object>( );
            Plugin plugin = PluginService.getPlugin( EnrollPlugin.PLUGIN_NAME );
            model.put( MARK_ICON, plugin.getIconUrl( ) );
            if ( RBACService.isAuthorized( Action.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, ProjectJspBean.RIGHT_MANAGEENROLL, user ) )
            {
                model = projectJspBean.getPaginatedListModel(request, ProjectJspBean.MARK_PROJECT_LIST, listProjects, ProjectJspBean.JSP_MANAGE_PROJECTS );
                HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_DASHBOARD, user.getLocale( ), model );
                return template.getHtml( );
            }
            return StringUtils.EMPTY;

        }
}
