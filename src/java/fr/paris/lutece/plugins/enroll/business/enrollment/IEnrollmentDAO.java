package fr.paris.lutece.plugins.enroll.business.enrollment;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import java.util.List;

public interface IEnrollmentDAO {
  /**
   * Insert a new record in the table.
   * @param enrollment instance of the Enrollment object to insert
   * @param plugin the Plugin
   */
  void insert(Enrollment enrollment, Plugin plugin);

  /**
   * Update the record in the table
   * @param enrollment the reference of the Enrollment
   * @param plugin the Plugin
   */
  void store(Enrollment enrollment, Plugin plugin);

  /**
   * Delete a record from the table
   * @param nKey The identifier of the Enrollment to delete
   * @param plugin the Plugin
   */
  void delete(int nKey, Plugin plugin);


  ///////////////////////////////////////////////////////////////////////////
  // Finders

  /**
   * Load the data from the table
   * @param nKey The identifier of the enrollment
   * @param plugin the Plugin
   * @return The instance of the enrollment
   */
  Enrollment load(int nKey, Plugin plugin);

  /**
   * Load the data of all the enrollment objects and returns them as a list
   * @param plugin the Plugin
   * @return The list which contains the data of all the enrollment objects
   */
  List<Enrollment> selectEnrollmentsList(Plugin plugin);

  /**
   * Load the id of all the enrollment objects and returns them as a list
   * @param plugin the Plugin
   * @return The list which contains the id of all the enrollment objects
   */
  List<Integer> selectIdEnrollmentsList(Plugin plugin);

  /**
   * Load the data of all the enrollment objects and returns them as a referenceList
   * @param plugin the Plugin
   * @return The referenceList which contains the data of all the enrollment objects
   */
  ReferenceList selectEnrollmentsReferenceList(Plugin plugin);

  /**
   * Load the data for all the enrollment objects with a given program and
   * return them as a list
   * @param program
   * @param plugin
   * @return The list of enrollments with the provided program, sorted by enrollment name
   */
  List<Enrollment> selectEnrollmentsForProgram(String program, Plugin plugin);
}
