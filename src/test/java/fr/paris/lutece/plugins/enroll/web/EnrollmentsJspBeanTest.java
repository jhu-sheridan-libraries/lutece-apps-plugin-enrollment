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

import static fr.paris.lutece.plugins.enroll.web.EnrollmentsJspBean.PARAMETER_EMAIL_ENROLLMENT;
import static fr.paris.lutece.plugins.enroll.web.EnrollmentsJspBean.PARAMETER_ID_ENROLLMENT;
import static fr.paris.lutece.plugins.enroll.web.EnrollmentsJspBean.PARAMETER_NAME_ENROLLMENT;
import static fr.paris.lutece.plugins.enroll.web.EnrollmentsJspBean.PARAMETER_PHONE_ENROLLMENT;
import static fr.paris.lutece.plugins.enroll.web.EnrollmentsJspBean.PARAMETER_PROGRAM_ENROLLMENT;

import static fr.paris.lutece.plugins.enroll.web.EnrollmentsJspBean.PARAMETER_ID_PROJECT;
import static fr.paris.lutece.plugins.enroll.web.ProjectJspBean.PARAMETER_NAME_PROJECT;
import static fr.paris.lutece.plugins.enroll.web.ProjectJspBean.PARAMETER_SIZE_PROJECT;
import static fr.paris.lutece.plugins.enroll.web.ProjectJspBean.PARAMETER_CURRENTSIZE_PROJECT;
import static fr.paris.lutece.plugins.enroll.web.ProjectJspBean.PARAMETER_STATUS_PROJECT;

import static org.mockito.ArgumentMatchers.*;


public class EnrollmentsJspBeanTest extends LuteceTestCase {

    RequestContextListener listener = new RequestContextListener();
    ServletContext context = new MockServletContext();

    public void testBusiness() {

        String name = "Enrollment Test Project";
        String size = "30";
        int newProjectId;

        MockHttpServletRequest request = new MockHttpServletRequest( );

        //test create project
        request.addParameter( PARAMETER_NAME_PROJECT, name );
        request.addParameter( PARAMETER_SIZE_PROJECT, size );
        request.addParameter( PARAMETER_CURRENTSIZE_PROJECT, "" );
        request.addParameter( PARAMETER_STATUS_PROJECT, "" );
        request.addParameter(PARAMETER_ID_PROJECT, "" );

        List<Project> projectList = ProjectHome.getProjectsList();
        //project id is the database row number
        newProjectId = projectList.size() + 1; //one more than the last existing project id

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

        assertEquals( name, latestProject.getName() );
        assertEquals( size, Integer.toString(latestProject.getSize()) );
        assertEquals( 1, latestProject.getActive() );
        assertEquals( 0, latestProject.getCurrentSize() );
        assertEquals( newProjectId,  latestProject.getId() );

        EnrollmentsJspBean enrollmentsJspBean = SpringContextService.getBean("enroll.EnrollmentsJspBean");
        EnrollmentsJspBean instance2 = Mockito.spy(enrollmentsJspBean);

        Mockito.doReturn("Return value not needed - ignored").when(instance2).redirect( any(), anyString(), anyString(), anyInt() );
        Mockito.doReturn("Return value not needed - ignored").when(instance2).getPage( anyString(), anyString(), any() );

        //test addition of an enrollment
        Enrollment newEnrollment = new Enrollment();
        newEnrollment.setProgram( name );
        newEnrollment.setEmail("user@place.com");
        newEnrollment.setName("Albert Batross");
        newEnrollment.setPhone("867-5309");

        request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_PROGRAM_ENROLLMENT, String.valueOf(newEnrollment.getProgram()));
        request.addParameter(PARAMETER_NAME_ENROLLMENT, newEnrollment.getName());
        request.addParameter(PARAMETER_EMAIL_ENROLLMENT, String.valueOf(newEnrollment.getEmail()));
        request.addParameter(PARAMETER_PHONE_ENROLLMENT, newEnrollment.getPhone());
        request.addParameter(PARAMETER_ID_PROJECT, String.valueOf(latestProject.getId()));

        instance2.doAddEnrollmentToProject(request);

        List<Enrollment> enrollmentList = EnrollmentHome.getEnrollmentsList();
        Enrollment addedEnrollment = EnrollmentHome.getEnrollmentsList().get( enrollmentList.size() -1 );
        assertEquals( newEnrollment.getEmail(), addedEnrollment.getEmail());
        assertEquals( newEnrollment.getName(), addedEnrollment.getName() );
        assertEquals( newEnrollment. getPhone(), addedEnrollment.getPhone() );
        assertEquals( newEnrollment.getProgram(), addedEnrollment.getProgram() );
        assertEquals( enrollmentList.size(), addedEnrollment.getId());

        Project project = ProjectHome.findByPrimaryKey(latestProject.getId() );
        assertEquals( 1, project.getCurrentSize() );

        //test modification of an enrollment
        Enrollment modifiedEnrollment = new Enrollment();
        modifiedEnrollment.setPhone("867-5332");
        modifiedEnrollment.setName( "Amanda B. Reckondwith");
        modifiedEnrollment.setEmail( "c@tchow.com" );
        modifiedEnrollment.setProgram(addedEnrollment.getProgram());
        modifiedEnrollment.setId(addedEnrollment.getId());

        request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_PROGRAM_ENROLLMENT, String.valueOf(modifiedEnrollment.getProgram()));
        request.addParameter(PARAMETER_NAME_ENROLLMENT, modifiedEnrollment.getName());
        request.addParameter(PARAMETER_EMAIL_ENROLLMENT, String.valueOf(modifiedEnrollment.getEmail()));
        request.addParameter(PARAMETER_PHONE_ENROLLMENT, modifiedEnrollment.getPhone());
        request.addParameter(PARAMETER_ID_ENROLLMENT, String.valueOf(modifiedEnrollment.getId()));
        request.addParameter(PARAMETER_ID_PROJECT, String.valueOf(latestProject.getId()));

        instance2.doModifyEnrollment(request);

        Enrollment  processedEnrollment = EnrollmentHome.findByPrimaryKey(modifiedEnrollment.getId());
        assertEquals(modifiedEnrollment, processedEnrollment);

        project = ProjectHome.findByPrimaryKey(latestProject.getId() );
        assertEquals( 1, project.getCurrentSize() );

        //test removal of an enrollment
        request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_ID_ENROLLMENT, String.valueOf(processedEnrollment.getId()));
        request.addParameter(PARAMETER_ID_PROJECT, String.valueOf(project.getId()));

        instance2.doRemoveEnrollment(request);

        assertNull(EnrollmentHome.findByPrimaryKey(modifiedEnrollment.getId()));

        project = ProjectHome.findByPrimaryKey(latestProject.getId() );
        assertEquals(0, project.getCurrentSize());

        listener.requestDestroyed( new ServletRequestEvent( context, request ) );
    }
}
