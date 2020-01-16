package fr.paris.lutece.plugins.enroll.business.enrollment;

import fr.paris.lutece.test.LuteceTestCase;

import java.util.List;

public class EnrollmentBusinessTest extends LuteceTestCase {
    private static final String PROGRAM1 = "Program1";
    private static final String PROGRAM2 = "Program2";
    private static final String NAME1 = "Name1";
    private static final String NAME2 = "Name2";
    private static final String EMAIL1 = "Email1@farm.com";
    private static final String EMAIL2 = "Email2@farm.com";
    private static final String PHONE1 = "410-867-5309";
    private static final String PHONE2 = "410-867-5310";
    private static final int ID1 = 1;

    public void testBusiness( ) {
        Enrollment enrollment = new Enrollment();
        enrollment.setProgram( PROGRAM1 );
        enrollment.setName( NAME1 );
        enrollment.setEmail( EMAIL1 );
        enrollment.setPhone( PHONE1 );
        enrollment.setId( ID1 );

        //create test
        EnrollmentHome.create( enrollment );
        Enrollment enrollmentStored = EnrollmentHome.findByPrimaryKey ( enrollment.getId() );

        assertEquals( enrollment.getProgram(), enrollmentStored.getProgram() );
        assertEquals( enrollment.getName(), enrollmentStored.getName() );
        assertEquals( enrollment.getEmail(), enrollmentStored.getEmail() );
        assertEquals( enrollment.getPhone(), enrollmentStored.getPhone());

        //update test
        enrollment.setProgram( PROGRAM2 );
        enrollment.setName( NAME2 );
        enrollment.setEmail( EMAIL2 );
        enrollment.setPhone( PHONE2 );

        EnrollmentHome.update( enrollment );
        enrollmentStored = EnrollmentHome.findByPrimaryKey ( enrollment.getId() );
        assertEquals( enrollment.getProgram(), enrollmentStored.getProgram() );
        assertEquals( enrollment.getName(), enrollmentStored.getName() );
        assertEquals( enrollment.getEmail(), enrollmentStored.getEmail() );
        assertEquals( enrollment.getPhone(), enrollmentStored.getPhone());

        //list test
        List<Enrollment> enrollmentsList = EnrollmentHome.getEnrollmentsList();
        assertEquals( 1, enrollmentsList.size() );
        assertEquals( enrollment.getId(), enrollmentsList.get(0).getId());

        // Delete test
        EnrollmentHome.remove( enrollment.getId( ) );
        enrollmentStored = EnrollmentHome.findByPrimaryKey( enrollment.getId( ) );
        assertNull( enrollmentStored );

    }
}

