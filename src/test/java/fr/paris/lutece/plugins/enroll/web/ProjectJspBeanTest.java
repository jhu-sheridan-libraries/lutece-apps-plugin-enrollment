package fr.paris.lutece.plugins.enroll.web;

import fr.paris.lutece.plugins.enroll.business.project.Project;
import fr.paris.lutece.plugins.enroll.business.project.ProjectHome;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.test.LuteceTestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.request.RequestContextListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import java.util.List;


public class ProjectJspBeanTest extends LuteceTestCase {

    RequestContextListener listener = new RequestContextListener();
    ServletContext context = new MockServletContext();
    int newProjectId;

    public void testDoCreateProject() {

        String name = "New Project To Create";
        String size = "10";

        MockHttpServletRequest request = new MockHttpServletRequest( );

        request.addParameter( "name", name );
        request.addParameter( "size", size );
        request.addParameter( "currentsize", "" );
        request.addParameter( "active", "" );
        request.addParameter( "id", "" );

        List<Project> projectList = ProjectHome.getProjectsList();
        //project id is the database row number
        newProjectId = projectList.size() + 1; //one more than the last existing project id

        listener.requestInitialized( new ServletRequestEvent( context, request ) );

        ProjectJspBean instance = SpringContextService.getBean( "enroll.ProjectJspBean" );

        //return statement will give us an NPE - we don't need the return value,
        //so we catch the exception and ignore it
        try {
                instance.doCreateProject(request);
        } catch (NullPointerException e) {
            //expected
        }

        //at this point this project should be in the database - let's look for it
        //currentsize should initialize to 0; active to 1; id is assigned upon creation,
        //should be equal to the size of the list
        projectList = ProjectHome.getProjectsList();

        // this should be the project we just added
        Project latestProject = projectList.get(newProjectId-1);

        assertEquals( latestProject.getName(), name );
        assertEquals( Integer.toString(latestProject.getSize()), size );
        assertEquals( 1, latestProject.getActive() );
        assertEquals( 0, latestProject.getCurrentSize() );
        assertEquals( newProjectId,  latestProject.getId() );

        listener.requestDestroyed( new ServletRequestEvent( context, request ) );
    }

    public void testDoChangeProjectStatus() {

    }

    public void testDoRemoveProject() {

        }
    public void testDoModifyProject() {

    }

}
