package fr.paris.lutece.plugins.enroll.business.enrollment;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import java.util.List;

public interface IEnrollmentDAO {
  void insert(Enrollment enrollment, Plugin plugin);

  void delete(int key, Plugin plugin);

  void store(Enrollment enrollment, Plugin plugin);

  Enrollment load(int key, Plugin plugin);

  List<Enrollment> selectEnrollmentsList(Plugin plugin);

  List<Integer> selectIdEnrollmentsList(Plugin plugin);

  ReferenceList selectEnrollmentsReferenceList(Plugin plugin);
}
