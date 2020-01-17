package fr.paris.lutece.plugins.enroll.business;

import fr.paris.lutece.plugins.enroll.business.project.Project;
import fr.paris.lutece.plugins.enroll.business.project.ProjectHome;
import fr.paris.lutece.test.LuteceTestCase;

import java.util.List;

public class ProjectBusinessTest extends LuteceTestCase {

    private static final String NAME1 = "Project One";
    private static final int ACTIVE1 = 0;
    private static final int ID1 = 1;
    private static final int SIZE1 = 10;
    private static final int CURRENTSIZE1 = 9;

    private static final String NAME2 = "Project Two";
    private static final int ACTIVE2 = 1;
    private static final int ID2 = 2;
    private static final int SIZE2 = 20;
    private static final int CURRENTSIZE2 = 19;


    public void testBusiness() {
        Project project = new Project();
        project.setActive( ACTIVE1 );
        project.setCurrentSize( CURRENTSIZE1 );
        project.setId( ID1 );
        project.setName( NAME1 );
        project.setSize( SIZE1 );

        //create test
        ProjectHome.create( project );
        Project projectStored = ProjectHome.findByPrimaryKey( project.getId() );
        assertEquals( project.getName(), projectStored.getName() );
        assertEquals( project.getActive(), projectStored.getActive() );
        assertEquals( project.getCurrentSize(), projectStored.getCurrentSize() );
        assertEquals( project.getSize(), projectStored.getSize() );

        //update test
        project.setActive( ACTIVE2 );
        project.setCurrentSize( CURRENTSIZE2 );
        project.setName( NAME2 );
        project.setSize( SIZE2 );

        ProjectHome.update( project );
        projectStored = ProjectHome.findByPrimaryKey( project.getId() );
        assertEquals( project.getName(), projectStored.getName() );
        assertEquals( project.getActive(), projectStored.getActive() );
        assertEquals( project.getCurrentSize(), projectStored.getCurrentSize() );
        assertEquals( project.getSize(), projectStored.getSize() );

        //list test
        List<Project> projectsList = ProjectHome.getProjectsList();
        assertEquals( 1, projectsList.size() );
        assertEquals( project.getId(), projectsList.get(0).getId());

        // Delete test
        ProjectHome.remove( project.getId( ) );
        projectStored = ProjectHome.findByPrimaryKey( project.getId( ) );
        assertNull( projectStored );

    }
}
