/*
 * Copyright (c) 2002-2019, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */

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
        assertEquals( enrollment, enrollmentsList.get(enrollmentsList.size()-1));

        // Delete test
        EnrollmentHome.remove( enrollment.getId( ) );
        enrollmentStored = EnrollmentHome.findByPrimaryKey( enrollment.getId( ) );
        assertNull( enrollmentStored );

    }
}

