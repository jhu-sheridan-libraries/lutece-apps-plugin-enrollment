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
import static fr.paris.lutece.plugins.enroll.web.ProjectJspBean.*;
import static org.mockito.ArgumentMatchers.*;


public class EnrollmentsJspBeanTest extends LuteceTestCase {

    RequestContextListener listener = new RequestContextListener();
    ServletContext context = new MockServletContext();
    MockHttpServletRequest request;

    EnrollmentsJspBean instance = SpringContextService.getBean( "enroll.EnrollmentsJspBean" );
    EnrollmentsJspBean underTest = Mockito.spy( instance );

    //this function creates a project that enrollments can be added to, modified, or deleted
    public void testCreateProject() {
        String name = "Test Project";
        String size = "30";

        request = new MockHttpServletRequest( );

        //create project
        request.addParameter( PARAMETER_NAME_PROJECT, name );
        request.addParameter( PARAMETER_SIZE_PROJECT, size );
        request.addParameter( PARAMETER_CURRENTSIZE_PROJECT, "" );
        request.addParameter( PARAMETER_STATUS_PROJECT, "" );
        request.addParameter(PARAMETER_ID_PROJECT, "" );

        List<Project> projectList = ProjectHome.getProjectsList();
        //project id is the database row number
        int newProjectId = projectList.size() + 1; //one more than the last existing project id

        listener.requestInitialized( new ServletRequestEvent( context, request ) );
        ProjectJspBean projectBeanInstance = SpringContextService.getBean( "enroll.ProjectJspBean" );

        //mock this method called in the return - we don't need it, and it gives NPE if invoked here
        ProjectJspBean instance1 = Mockito.spy( projectBeanInstance );
        Mockito.doReturn("Return value not needed - ignored").when(instance1).redirectView( any(), anyString() );

        instance1.doCreateProject(request);

        //at this point this project should be in the database
        projectList = ProjectHome.getProjectsList();

        // this should be the project we just added
        Project latestProject = projectList.get(newProjectId-1);

        //make sure the last project is the one we are looking at
        assertEquals( name, latestProject.getName() );
        assertEquals( size, Integer.toString(latestProject.getSize()) );
        assertEquals( 1, latestProject.getActive() );
        assertEquals( 0, latestProject.getCurrentSize() );
        assertEquals( newProjectId,  latestProject.getId() );
        listener.requestDestroyed( new ServletRequestEvent( context, request ) );
    }


    public void testCreateEnrollment() {
        int initialProjectId = ProjectHome.update(reset()).getId();

        Mockito.doReturn("Return value not needed - ignored").when(underTest).redirect( any(), anyString(), anyString(), anyInt() );
        Mockito.doReturn("Return value not needed - ignored").when(underTest).getPage( anyString(), anyString(), any() );

        //test addition of an enrollment
        Enrollment newEnrollment = new Enrollment();
        newEnrollment.setProgram( "Create Enrollment" );
        newEnrollment.setEmail("user@place.com");
        newEnrollment.setName("Albert Batross");
        newEnrollment.setPhone("867-5309");

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
        assertEquals( newEnrollment.getEmail(), addedEnrollment.getEmail());
        assertEquals( newEnrollment.getName(), addedEnrollment.getName() );
        assertEquals( newEnrollment. getPhone(), addedEnrollment.getPhone() );
        assertEquals( newEnrollment.getProgram(), addedEnrollment.getProgram() );
        assertEquals( enrollmentList.size(), addedEnrollment.getId());

        Project project = ProjectHome.findByPrimaryKey(initialProjectId );
        assertEquals( 1, project.getCurrentSize() );
        listener.requestDestroyed( new ServletRequestEvent( context, request ) );
    }

    public void testModifyEnrollment() {
        Project project = ProjectHome.update(reset());
        int projectId = project.getId();
        Enrollment enrollment = EnrollmentHome.update(makeEnroll(project));
        Mockito.doReturn("Return value not needed - ignored").when(underTest).redirect( any(), anyString(), anyString(), anyInt() );
        Mockito.doReturn("Return value not needed - ignored").when(underTest).getPage( anyString(), anyString(), any() );

        Enrollment modifiedEnrollment = new Enrollment();
        modifiedEnrollment.setName("Jane Smith");
        modifiedEnrollment.setPhone("098-7654");
        modifiedEnrollment.setEmail("new@email.com");
        modifiedEnrollment.setPhone(enrollment.getProgram());
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

        project = ProjectHome.findByPrimaryKey(projectId);
        assertEquals(1, project.getCurrentSize());

        listener.requestDestroyed( new ServletRequestEvent( context, request ) );
    }

    public void testRemoveEnrollment() {
        Project project = ProjectHome.update(reset());
        int projectId = project.getId();
        Enrollment enrollment = EnrollmentHome.update(makeEnroll(project));
        Mockito.doReturn("Return value not needed - ignored").when(underTest).redirect( any(), anyString(), anyString(), anyInt() );
        Mockito.doReturn("Return value not needed - ignored").when(underTest).getPage( anyString(), anyString(), any() );

        request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_ID_ENROLLMENT, String.valueOf(enrollment.getId()));
        request.addParameter(PARAMETER_ID_PROJECT, String.valueOf(project.getId()));

        listener.requestInitialized( new ServletRequestEvent( context, request ) );
        underTest.doRemoveEnrollment(request);

        assertNull(EnrollmentHome.findByPrimaryKey(enrollment.getId()));
        project = ProjectHome.findByPrimaryKey(projectId);
        assertEquals(0, project.getCurrentSize());

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
        return EnrollmentHome.create(enrollment);
    }
}
