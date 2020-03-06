package fr.paris.lutece.plugins.enroll.web;

import fr.paris.lutece.plugins.enroll.business.enrollment.Enrollment;
import fr.paris.lutece.plugins.enroll.business.enrollment.EnrollmentHome;
import fr.paris.lutece.plugins.enroll.business.project.Project;
import fr.paris.lutece.plugins.enroll.business.project.ProjectHome;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.test.LuteceTestCase;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.request.RequestContextListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import java.util.List;

import static fr.paris.lutece.plugins.enroll.web.EnrollmentsJspBean.PARAMETER_ID_PROJECT;
import static fr.paris.lutece.plugins.enroll.web.EnrollmentsJspBean.*;
import static org.mockito.ArgumentMatchers.*;


public class EnrollmentsJspBeanTest extends LuteceTestCase {

    RequestContextListener listener = new RequestContextListener();
    ServletContext context = new MockServletContext();
    MockHttpServletRequest request;

    EnrollmentsJspBean instance = SpringContextService.getBean( "enroll.EnrollmentsJspBean" );
    EnrollmentsJspBean underTest = Mockito.spy( instance );

    public void testAddProjectToEnrollmentEnrollment() {
        int initialProjectId = reset().getId();

        Mockito.doReturn("Return value not needed - ignored").when(underTest).redirect( any(), anyString(), anyString(), anyInt() );
        Mockito.doReturn("Return value not needed - ignored").when(underTest).getPage( anyString(), anyString(), any() );

        //test addition of an enrollment
        Enrollment newEnrollment = new Enrollment();
        newEnrollment.setProgram( "Create Enrollment" );
        newEnrollment.setEmail("user@place.com");
        newEnrollment.setName("Albert Batross");
        newEnrollment.setPhone("867-5309");
        newEnrollment.setId(1); //adding a new enrollment to an empty database gets id 1

        request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_PROGRAM_ENROLLMENT, String.valueOf(newEnrollment.getProgram()));
        request.addParameter(PARAMETER_NAME_ENROLLMENT, newEnrollment.getName());
        request.addParameter(PARAMETER_EMAIL_ENROLLMENT, String.valueOf(newEnrollment.getEmail()));
        request.addParameter(PARAMETER_PHONE_ENROLLMENT, newEnrollment.getPhone());
        request.addParameter(PARAMETER_ID_PROJECT, String.valueOf(initialProjectId));
        listener.requestInitialized( new ServletRequestEvent( context, request ) );

        underTest.doAddEnrollmentToProject(request);

        List<Enrollment> enrollmentList = EnrollmentHome.getEnrollmentsList();
        Enrollment addedEnrollment = EnrollmentHome.getEnrollmentsList().get( enrollmentList.size() -1 );
        assertEquals( newEnrollment, addedEnrollment);

        Project project = ProjectHome.findByPrimaryKey(initialProjectId );
        assertEquals( 1, project.getCurrentSize() );

        listener.requestDestroyed( new ServletRequestEvent( context, request ) );
    }

    public void testAddProjectToEnrollmentEnrollmentFailsIfInactive() {

        Project project = reset();
        int initialProjectId = project.getId();
        project.setActive(0);

        ProjectHome.update(project);

        Mockito.doReturn("Return value not needed - ignored").when(underTest).redirect( any(), anyString(), anyString(), anyInt() );
        Mockito.doReturn("Return value not needed - ignored").when(underTest).getPage( anyString(), anyString(), any() );

        //test addition of an enrollment
        Enrollment newEnrollment = new Enrollment();
        newEnrollment.setProgram( "Create Enrollment" );
        newEnrollment.setEmail("user@place.com");
        newEnrollment.setName("Albert Batross");
        newEnrollment.setPhone("867-5309");
        newEnrollment.setId(1); //adding a new enrollment to an empty database gets id 1

        request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_PROGRAM_ENROLLMENT, String.valueOf(newEnrollment.getProgram()));
        request.addParameter(PARAMETER_NAME_ENROLLMENT, newEnrollment.getName());
        request.addParameter(PARAMETER_EMAIL_ENROLLMENT, String.valueOf(newEnrollment.getEmail()));
        request.addParameter(PARAMETER_PHONE_ENROLLMENT, newEnrollment.getPhone());
        request.addParameter(PARAMETER_ID_PROJECT, String.valueOf(initialProjectId));
        listener.requestInitialized( new ServletRequestEvent( context, request ) );

        underTest.doAddEnrollmentToProject(request);

        List<Enrollment> enrollmentList = EnrollmentHome.getEnrollmentsList();
        assertEquals(0, enrollmentList.size());

        project = ProjectHome.findByPrimaryKey(initialProjectId );
        assertEquals( 0, project.getCurrentSize() );

        listener.requestDestroyed( new ServletRequestEvent( context, request ) );
    }

    public void testAddProjectToEnrollmentEnrollmentFailsIfFull() {

        Project project = reset();
        int initialProjectId = project.getId();

        project.setCurrentSize(project.getSize());

        assertTrue(project.getSize() > 0);

        ProjectHome.update(project);

        Mockito.doReturn("Return value not needed - ignored").when(underTest).redirect( any(), anyString(), anyString(), anyInt() );
        Mockito.doReturn("Return value not needed - ignored").when(underTest).getPage( anyString(), anyString(), any() );

        //test addition of an enrollment
        Enrollment newEnrollment = new Enrollment();
        newEnrollment.setProgram( "Create Enrollment" );
        newEnrollment.setEmail("user@place.com");
        newEnrollment.setName("Albert Batross");
        newEnrollment.setPhone("867-5309");
        newEnrollment.setId(1); //adding a new enrollment to an empty database gets id 1

        request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_PROGRAM_ENROLLMENT, String.valueOf(newEnrollment.getProgram()));
        request.addParameter(PARAMETER_NAME_ENROLLMENT, newEnrollment.getName());
        request.addParameter(PARAMETER_EMAIL_ENROLLMENT, String.valueOf(newEnrollment.getEmail()));
        request.addParameter(PARAMETER_PHONE_ENROLLMENT, newEnrollment.getPhone());
        request.addParameter(PARAMETER_ID_PROJECT, String.valueOf(initialProjectId));
        listener.requestInitialized( new ServletRequestEvent( context, request ) );

        underTest.doAddEnrollmentToProject(request);

        List<Enrollment> enrollmentList = EnrollmentHome.getEnrollmentsList();
        assertEquals(0, enrollmentList.size());

        project = ProjectHome.findByPrimaryKey(initialProjectId );
        assertEquals( project.getSize(), project.getCurrentSize() );

        listener.requestDestroyed( new ServletRequestEvent( context, request ) );
    }

    public void testModifyEnrollment() {
        Project project = reset();
        Enrollment enrollment = EnrollmentHome.create(makeEnroll( project ));
        Mockito.doReturn("Return value not needed - ignored").when(underTest).redirect( any(), anyString(), anyString(), anyInt() );
        Mockito.doReturn("Return value not needed - ignored").when(underTest).getPage( anyString(), anyString(), any() );

        Enrollment modifiedEnrollment = new Enrollment();
        modifiedEnrollment.setName("Jane Smith");
        modifiedEnrollment.setPhone("098-7654");
        modifiedEnrollment.setEmail("new@email.com");
        modifiedEnrollment.setProgram(enrollment.getProgram());
        modifiedEnrollment.setId(enrollment.getId());

        request = new MockHttpServletRequest();

        request.addParameter(PARAMETER_PROGRAM_ENROLLMENT, String.valueOf(modifiedEnrollment.getProgram()));
        request.addParameter(PARAMETER_NAME_ENROLLMENT, modifiedEnrollment.getName());
        request.addParameter(PARAMETER_EMAIL_ENROLLMENT, String.valueOf(modifiedEnrollment.getEmail()));
        request.addParameter(PARAMETER_PHONE_ENROLLMENT, modifiedEnrollment.getPhone());
        request.addParameter(PARAMETER_ID_ENROLLMENT, String.valueOf(modifiedEnrollment.getId()));
        request.addParameter(PARAMETER_ID_PROJECT, String.valueOf(project.getId()));
        listener.requestInitialized( new ServletRequestEvent( context, request ) );

        underTest.doModifyEnrollment(request);
        Enrollment  processedEnrollment = EnrollmentHome.findByPrimaryKey(modifiedEnrollment.getId());
        assertEquals(modifiedEnrollment, processedEnrollment);

        listener.requestDestroyed( new ServletRequestEvent( context, request ) );
    }

    public void testRemoveEnrollment() {
        Project project = reset();
        Enrollment enrollment = makeEnroll(project);

        assertEquals(1, ProjectHome.findByName(project.getName()).getCurrentSize());

        Mockito.doReturn("Return value not needed - ignored").when(underTest).redirect( any(), anyString(), anyString(), anyInt() );
        Mockito.doReturn("Return value not needed - ignored").when(underTest).getPage( anyString(), anyString(), any() );

        request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_ID_ENROLLMENT, String.valueOf(enrollment.getId()));
        request.addParameter(PARAMETER_ID_PROJECT, String.valueOf(project.getId()));

        listener.requestInitialized( new ServletRequestEvent( context, request ) );
        underTest.doRemoveEnrollment(request);

        assertNull(EnrollmentHome.findByPrimaryKey(enrollment.getId()));;

        assertEquals(0, ProjectHome.findByName(project.getName()).getCurrentSize());

        listener.requestDestroyed( new ServletRequestEvent( context, request ) );

    }

    /**
     * private convenience method to simplify database state. returns a fresh project
     * with reliable field values and a reset database
     * @return the project created
     */
    private Project reset() {
        for (Project project : ProjectHome.getProjectsList()) {
            ProjectHome.remove(project.getId());
        }
        for (Enrollment enrollment : EnrollmentHome.getEnrollmentsList()) {
            EnrollmentHome.remove(enrollment.getId());
        }
        Project project = new Project();
        project.setName("Test Project");
        project.setSize(2);
        return ProjectHome.create(project);
    }

    /**
     * private convenience method which will add an enrollment to the project passed in
     * @return the Enrollment created
     */
    private Enrollment makeEnroll(Project project) {
        Enrollment enrollment = new Enrollment();
        enrollment.setPhone("123-4567");
        enrollment.setEmail("testemail@g.com");
        enrollment.setName("John Smith");
        enrollment.setProgram(project.getName());

        project.setCurrentSize( project.getCurrentSize() +1);
        ProjectHome.update(project);

        return EnrollmentHome.create(enrollment);
    }
}
