package fr.paris.lutece.plugins.enroll.business.enrollment;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;

public final class EnrollmentHome {
  private static Plugin _plugin = PluginService.getPlugin("enroll");
  private static IEnrollmentDAO _dao = SpringContextService.getBean("enroll.enrollmentDAO");

  private EnrollmentHome() {
  }

  public static Enrollment create(Enrollment enrollment) {
    _dao.insert(enrollment, _plugin);
    return enrollment;
  }

  public static Enrollment update(Enrollment enrollment) {
    _dao.store(enrollment, _plugin);
    return enrollment;
  }

  public static void remove(int key) {
    _dao.delete(key, _plugin);
  }

  public static Enrollment findByPrimaryKey(int key) {
    return _dao.load(key, _plugin);
  }

  public static List<Enrollment> getEnrollmentsList() {
    return _dao.selectEnrollmentsList(_plugin);
  }

  public static List<Integer> getIdEnrollmentsList() {
    return _dao.selectIdEnrollmentsList(_plugin);
  }

  public static ReferenceList getEnrollmentsReferenceList() {
    return _dao.selectEnrollmentsReferenceList(_plugin);
  }
}
