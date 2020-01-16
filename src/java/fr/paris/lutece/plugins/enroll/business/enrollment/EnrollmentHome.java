package fr.paris.lutece.plugins.enroll.business.enrollment;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;

/**
 * This class provides instances management methods (create, find, ...) for Enrollment objects
 */
public final class EnrollmentHome {
  // Static variable pointed at the DAO instance
  private static Plugin _plugin = PluginService.getPlugin("enroll");
  private static IEnrollmentDAO _dao = SpringContextService.getBean("enroll.enrollmentDAO");

  /**
   * Private constructor - this class need not be instantiated
   */
  private EnrollmentHome() {
  }

  /**
   * Create an instance of the project class
   * @param enrollment The instance of the Enrollment which contains the information to store
   * @return The  instance of project which has been created with its primary key.
   */
  public static Enrollment create(Enrollment enrollment) {
    _dao.insert(enrollment, _plugin);
    return enrollment;
  }

  /**
   * Update of the project which is specified in parameter
   * @param enrollment The instance of the Enrollment which contains the data to store
   * @return The instance of the  project which has been updated
   */
  public static Enrollment update(Enrollment enrollment) {
    _dao.store(enrollment, _plugin);
    return enrollment;
  }

  /**
   * Remove the enrollment whose identifier is specified in parameter
   * @param nKey The enrollment Id
   */
  public static void remove(int nKey) {
    _dao.delete(nKey, _plugin);
  }

  /**
   * Returns an instance of an enrollment whose identifier is specified in parameter
   * @param nKey The enrollment primary key
   * @return an instance of Enrollment
   */
  public static Enrollment findByPrimaryKey(int nKey) {
    return _dao.load(nKey, _plugin);
  }

  /**
   * Load the data of all the enrollment objects and returns them as a list
   * @return the list which contains the data of all the enrollmentt objects
   */
  public static List<Enrollment> getEnrollmentsList() {
    return _dao.selectEnrollmentsList(_plugin);
  }

  /**
   * Load the id of all the enrollment objects and returns them as a list
   * @return the list which contains the id of all the enrollment objects
   */
  public static List<Integer> getIdEnrollmentsList() {
    return _dao.selectIdEnrollmentsList(_plugin);
  }

  /**
   * Load the data of all the enrollment objects and returns them as a referenceList
   * @return the referenceList which contains the data of all the enrollment objects
   */
  public static ReferenceList getEnrollmentsReferenceList() {
    return _dao.selectEnrollmentsReferenceList(_plugin);
  }
}
