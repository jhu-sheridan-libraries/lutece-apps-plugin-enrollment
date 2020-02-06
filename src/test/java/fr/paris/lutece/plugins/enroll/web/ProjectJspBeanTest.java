package fr.paris.lutece.plugins.enroll.web;

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

import static fr.paris.lutece.plugins.enroll.web.ProjectJspBean.PARAMETER_NAME_PROJECT;
import static fr.paris.lutece.plugins.enroll.web.ProjectJspBean.PARAMETER_SIZE_PROJECT;
import static fr.paris.lutece.plugins.enroll.web.ProjectJspBean.PARAMETER_CURRENTSIZE_PROJECT;
import static fr.paris.lutece.plugins.enroll.web.ProjectJspBean.PARAMETER_STATUS_PROJECT;
import static fr.paris.lutece.plugins.enroll.web.ProjectJspBean.PARAMETER_ID_PROJECT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class ProjectJspBeanTest extends LuteceTestCase {

    RequestContextListener listener = new RequestContextListener();
    ServletContext context = new MockServletContext();

    public void testBusiness() {

        String name = "Test Project";
        String size = "20";
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

        ProjectJspBean instance = SpringContextService.getBean( "enroll.ProjectJspBean" );
        //mock this method called in the return - we don't need it, and it gives NPE if invoked here
        ProjectJspBean instance1 = Mockito.spy( instance );
        Mockito.doReturn("Return value not needed - ignored").when(instance1).redirectView( any(), anyString() );

        instance1.doCreateProject(request);

        //at this point this project should be in the database - let's look for it
        //currentsize should initialize to 0; active to 1; id is assigned upon creation,
        //should be equal to the size of the list
        projectList = ProjectHome.getProjectsList();

        // this should be the project we just added
        Project latestProject = projectList.get(newProjectId-1);

        assertEquals( name, latestProject.getName() );
        assertEquals( size, Integer.toString(latestProject.getSize()) );
        assertEquals( 1, latestProject.getActive() );
        assertEquals( 0, latestProject.getCurrentSize() );
        assertEquals( newProjectId,  latestProject.getId() );

        // test that changing status works
        request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_ID_PROJECT, String.valueOf(newProjectId));

        instance1.doChangeProjectStatus(request);
        latestProject = ProjectHome.findByPrimaryKey(newProjectId);
        assertEquals(0, latestProject.getActive());

        instance1.doChangeProjectStatus(request);
        latestProject = ProjectHome.findByPrimaryKey(newProjectId);
        assertEquals(1, latestProject.getActive());

        //test modification of enrollment
        Project modifiedProject = new Project();
        modifiedProject.setName("New Project");
        modifiedProject.setSize(20);
        modifiedProject.setId(latestProject.getId());

        request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_ID_PROJECT, String.valueOf(modifiedProject.getId()));
        request.addParameter(PARAMETER_NAME_PROJECT, modifiedProject.getName());
        request.addParameter(PARAMETER_SIZE_PROJECT, String.valueOf(modifiedProject.getSize()));

        instance1.doModifyProject(request);
        latestProject = ProjectHome.findByPrimaryKey(newProjectId);
        assertEquals(modifiedProject.getName(), latestProject.getName());
        assertEquals(modifiedProject.getSize(), latestProject.getSize());

        //test removal
        request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_ID_PROJECT, String.valueOf(modifiedProject.getId()));
        instance1.doRemoveProject(request);
        assertNull(ProjectHome.findByPrimaryKey(modifiedProject.getId()));

        listener.requestDestroyed( new ServletRequestEvent( context, request ) );
    }
}
