/*
 * Copyright (c) 2002-2021, City of Paris
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

import static fr.paris.lutece.plugins.enroll.web.ProjectJspBean.PARAMETER_NAME_PROJECT;
import static fr.paris.lutece.plugins.enroll.web.ProjectJspBean.PARAMETER_SIZE_PROJECT;
import static fr.paris.lutece.plugins.enroll.web.ProjectJspBean.PARAMETER_CURRENTSIZE_PROJECT;
import static fr.paris.lutece.plugins.enroll.web.ProjectJspBean.PARAMETER_STATUS_PROJECT;
import static fr.paris.lutece.plugins.enroll.web.ProjectJspBean.PARAMETER_ID_PROJECT;
import static org.mockito.ArgumentMatchers.*;

/**
 * Test class to exercise the ProjectJspBean
 *
 * Note: we mock the bean to mask an NPE which results from the redirectView() and redirect() methods - we don't care about the return value
 * in methods using this, just the before and after states of the database when exercising the action methods
 */
public class ProjectJspBeanTest extends LuteceTestCase {

    RequestContextListener listener = new RequestContextListener();
    ServletContext context = new MockServletContext();
    MockHttpServletRequest request;

    ProjectJspBean instance = SpringContextService.getBean( "enroll.ProjectJspBean" );
    ProjectJspBean underTest = Mockito.spy( instance );

    /**
     * Test that if we create a project through the bean, that it shows up correctly in the database
     */
    
    public void testCreateProject() {
        String name = "Created Project";
        String size = "20";

        MockHttpServletRequest request = new MockHttpServletRequest( );

        //test create project
        request.addParameter(PARAMETER_NAME_PROJECT, name);
        request.addParameter(PARAMETER_SIZE_PROJECT, size);
        request.addParameter(PARAMETER_CURRENTSIZE_PROJECT, "");
        request.addParameter(PARAMETER_STATUS_PROJECT, "");
        request.addParameter(PARAMETER_ID_PROJECT, "");

        List<Project> projectList = ProjectHome.getProjectsList();
        //project id is the database row number
        int newProjectId = projectList.size() + 1; //one more than the last existing project id

        listener.requestInitialized(new ServletRequestEvent(context, request));
        Mockito.doReturn("Return value not needed - ignored").when(underTest).redirectView(any(), anyString());

        underTest.doCreateProject(request);

        //at this point this project should be in the database - let's look for it
        //currentsize should initialize to 0; active to 1; id is assigned upon creation,
        //should be equal to the size of the list
        projectList = ProjectHome.getProjectsList();

        // this should be the project we just added
        Project createdProject = projectList.get(newProjectId - 1);

        assertEquals(name, createdProject.getName());
        assertEquals(size, Integer.toString(createdProject.getSize()));
        assertEquals(1, createdProject.getActive());
        assertEquals(0, createdProject.getCurrentSize());
        assertEquals(newProjectId, createdProject.getId());


        listener.requestDestroyed( new ServletRequestEvent( context, request ) );
    }

    /**
     * test that flipping a project's status through the bean works as advertised
     */
    
    public void testChangeProjectStatus() {
        int initialProjectId = ProjectHome.update(reset()).getId();

        request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_ID_PROJECT, String.valueOf(initialProjectId));

        listener.requestInitialized(new ServletRequestEvent(context, request));
        Mockito.doReturn("Return value not needed - ignored").when(underTest).redirectView(any(), anyString());

        underTest.doChangeProjectStatus(request);
        Project projectToChangeStatus = ProjectHome.findByPrimaryKey(initialProjectId);
        assertEquals(0, projectToChangeStatus.getActive());

        underTest.doChangeProjectStatus(request);
        projectToChangeStatus = ProjectHome.findByPrimaryKey(initialProjectId);
        assertEquals(1, projectToChangeStatus.getActive());

        listener.requestDestroyed( new ServletRequestEvent( context, request ) );
    }

    /**
     * test that we can modify the size and/or name of a project when constraints are met
     */
    
    public void testModifyProject() {
        Project storedProject = ProjectHome.update(reset());

        request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_ID_PROJECT, String.valueOf(storedProject.getId()));
        request.addParameter(PARAMETER_NAME_PROJECT, "Modified Project");
        request.addParameter(PARAMETER_SIZE_PROJECT, "20");

        listener.requestInitialized(new ServletRequestEvent(context, request));
        Mockito.doReturn("Return value not needed - ignored").when(underTest).redirectView(any(), anyString());

        underTest.doModifyProject(request);

        Project processedProject = ProjectHome.findByPrimaryKey(storedProject.getId());
        assertEquals("Modified Project", processedProject.getName());
        assertEquals( 20, processedProject.getSize());

        listener.requestDestroyed( new ServletRequestEvent( context, request ) );
    }

    /**
     * test that we can not modify the size of a project when the proposed size is too small
     */
    
    public void testModifyProjectTooSmall() {
        Project storedProject = ProjectHome.update(reset());

        // modify stored project to have a current size of 2

        Project project = ProjectHome.findByPrimaryKey(storedProject.getId());
        project.setCurrentSize(2);
        ProjectHome.update(project);

        //now try to set the project's size to 1
        request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_ID_PROJECT, String.valueOf(storedProject.getId()));
        request.addParameter(PARAMETER_NAME_PROJECT, "Modified Project");
        request.addParameter(PARAMETER_SIZE_PROJECT, "1");

        listener.requestInitialized(new ServletRequestEvent(context, request));
        Mockito.doReturn("Return value not needed - ignored").when(underTest).redirectView(any(), anyString());
        Mockito.doReturn("Return value not needed - ignored").when(underTest).redirect(any(), anyString(), anyString(), anyInt());
        underTest.doModifyProject(request);

        Project processedProject = ProjectHome.findByPrimaryKey(storedProject.getId());
        assertEquals("Test Project", processedProject.getName());//update did not happen
        assertEquals( 2, processedProject.getSize());

        listener.requestDestroyed( new ServletRequestEvent( context, request ) );
    }

    /**
     * test that we can not modify the size and/or name of a project when the proposed project name
     * is already in use
     */
    public void testModifyProjectSameName() {
        Project storedProject = ProjectHome.update(reset());

        Project existingProject = new Project();
        existingProject.setName("Cool Project");
        existingProject.setSize(20);

        ProjectHome.create(existingProject);

        //now try to set the project's size to 1
        request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_ID_PROJECT, String.valueOf(storedProject.getId()));
        request.addParameter(PARAMETER_NAME_PROJECT, "Cool Project");
        request.addParameter(PARAMETER_SIZE_PROJECT, "20");

        listener.requestInitialized(new ServletRequestEvent(context, request));
        Mockito.doReturn("Return value not needed - ignored").when(underTest).redirectView(any(), anyString());
        Mockito.doReturn("Return value not needed - ignored").when(underTest).redirect(any(), anyString(), anyString(), anyInt());
        underTest.doModifyProject(request);

        Project processedProject = ProjectHome.findByPrimaryKey(storedProject.getId());
        assertEquals("Test Project", processedProject.getName());//update did not happen
        assertEquals( 2, processedProject.getSize());

        listener.requestDestroyed( new ServletRequestEvent( context, request ) );
    }

    /**
     * test that when can modify a project's name, its enrollments are updated
     */
    
    public void testModifyProjectNameModifiesAssociatedEnrollments() {
        Project storedProject = ProjectHome.update(reset());

        Enrollment enrollment = new Enrollment();
        enrollment.setProgram( storedProject.getName() );
        enrollment.setPhone("867-5309");
        enrollment.setName("Amanda B. Reckondwith");
        enrollment.setEmail("purinac@chow@pet.com");

        Enrollment storedEnrollment = EnrollmentHome.create( enrollment );
        assertNotNull( storedEnrollment );

        request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_ID_PROJECT, String.valueOf(storedProject.getId()));
        request.addParameter(PARAMETER_NAME_PROJECT, "Modified Project");
        request.addParameter(PARAMETER_SIZE_PROJECT, "20");

        listener.requestInitialized(new ServletRequestEvent(context, request));
        Mockito.doReturn("Return value not needed - ignored").when(underTest).redirectView(any(), anyString());

        underTest.doModifyProject(request);

        Project processedProject = ProjectHome.findByPrimaryKey(storedProject.getId());
        assertEquals("Modified Project", processedProject.getName());
        assertEquals( 20, processedProject.getSize());

        Enrollment modifiedEnrollment = EnrollmentHome.findByPrimaryKey( storedEnrollment.getId() );
        assertNotNull ( modifiedEnrollment );
        assertEquals(processedProject.getName(), modifiedEnrollment.getProgram());

        listener.requestDestroyed( new ServletRequestEvent( context, request ) );
    }
    /**
     * test that we can remove an existing project
     */
    
    public void testRemoveProject() {
        Project removeProject = new Project();
        removeProject.setName("Remove Project Name");
        removeProject.setSize(30);

        Project processedProject = ProjectHome.create(removeProject);
        assertEquals(removeProject.getName(), processedProject.getName());

        request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_ID_PROJECT, String.valueOf(removeProject.getId()));

        listener.requestInitialized(new ServletRequestEvent(context, request));
        Mockito.doReturn("Return value not needed - ignored").when(underTest).redirectView(any(), anyString());
        underTest.doRemoveProject(request);
        assertNull(ProjectHome.findByPrimaryKey(processedProject.getId()));

        listener.requestDestroyed( new ServletRequestEvent( context, request ) );
    }

    /**
     * test that remove an existing project removes its asociated enrollments
     */
    
    public void testRemoveProjectRemovesEnrollments() {
        Project removeProject = ProjectHome.create(reset());

        String program = removeProject.getName();

        Enrollment enrollment = new Enrollment();
        enrollment.setProgram( program );
        enrollment.setPhone("867-5309");
        enrollment.setName("Amanda B. Reckondwith");
        enrollment.setEmail("purinac@chow@pet.com");

        Enrollment storedEnrollment = EnrollmentHome.create( enrollment );
        assertNotNull( storedEnrollment);

        request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_ID_PROJECT, String.valueOf(removeProject.getId()));

        listener.requestInitialized(new ServletRequestEvent(context, request));
        Mockito.doReturn("Return value not needed - ignored").when(underTest).redirectView(any(), anyString());
        underTest.doRemoveProject(request);
        assertNull(EnrollmentHome.findByPrimaryKey(storedEnrollment.getId()));

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

}
