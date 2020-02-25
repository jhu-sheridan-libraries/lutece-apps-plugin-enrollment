package fr.paris.lutece.plugins.enroll.web;

import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.dashboard.admin.AdminDashboardComponent;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

public class EnrollmentAdminDashboardComponent extends AdminDashboardComponent {
    @Override
    public String getDashboardData(AdminUser adminUser, HttpServletRequest httpServletRequest) {
        Plugin plugin = PluginService.getPlugin( "enrollment" );
        Map<String, Object> model = new HashMap<String, Object>(  );
        //model.put( Constants, EnrollPortletHome.findAll( plugin ) );
        HtmlTemplate template = new HtmlTemplate(  );
        template = AppTemplateService.getTemplate( "manage_enrollments.html", getLocale(  ), model );
        return template.getHtml(  );
    }
}
