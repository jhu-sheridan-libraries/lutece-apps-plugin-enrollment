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

package fr.paris.lutece.plugins.enroll.business.project;

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
        assertEquals(project, projectStored);

        //update test
        project.setActive( ACTIVE2 );
        project.setCurrentSize( CURRENTSIZE2 );
        project.setName( NAME2 );
        project.setSize( SIZE2 );

        ProjectHome.update( project );
        projectStored = ProjectHome.findByPrimaryKey( project.getId() );
        assertEquals(project, projectStored);

        //list test
        List<Project> projectsList = ProjectHome.getProjectsList();
        assertEquals( project, projectsList.get(projectsList.size()-1));

        // Delete test
        ProjectHome.remove( project.getId( ) );
        projectStored = ProjectHome.findByPrimaryKey( project.getId( ) );
        assertNull( projectStored );

    }
}
