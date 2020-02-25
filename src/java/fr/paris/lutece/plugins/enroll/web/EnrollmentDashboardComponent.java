package fr.paris.lutece.plugins.enroll.web;

import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.dashboard.DashboardComponent;

import javax.servlet.http.HttpServletRequest;

public class EnrollmentDashboardComponent extends DashboardComponent {
    @Override
    public String getDashboardData(AdminUser adminUser, HttpServletRequest httpServletRequest) {
        return "";
    }
}
