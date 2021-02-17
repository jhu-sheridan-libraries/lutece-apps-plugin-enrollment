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
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.web.xpages.XPageApplicationEntry;
import fr.paris.lutece.test.LuteceTestCase;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.request.RequestContextListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import java.util.List;

import static fr.paris.lutece.plugins.enroll.web.EnrollmentsJspBean.*;
import static fr.paris.lutece.plugins.enroll.web.EnrollmentsJspBean.PARAMETER_ID_PROJECT;
import static fr.paris.lutece.plugins.enroll.web.ProjectJspBean.*;
import static fr.paris.lutece.plugins.enroll.web.ProjectJspBean.PARAMETER_STATUS_PROJECT;

import static org.mockito.ArgumentMatchers.*;

public class EnrollmentXPageTest extends LuteceTestCase {

    RequestContextListener listener = new RequestContextListener();
    ServletContext context = new MockServletContext();

    public void testBusiness() {

        String name = "Enrollment XPage Project";
        String size = "30";
        int newProjectId;

        MockHttpServletRequest request = new MockHttpServletRequest();

        //test create project
        request.addParameter(PARAMETER_NAME_PROJECT, name);
        request.addParameter(PARAMETER_SIZE_PROJECT, size);
        request.addParameter(PARAMETER_CURRENTSIZE_PROJECT, "");
        request.addParameter(PARAMETER_STATUS_PROJECT, "");
        request.addParameter(PARAMETER_ID_PROJECT, "");

        List<Project> projectList = ProjectHome.getProjectsList();
        //project id is the database row number
        newProjectId = projectList.size() + 1; //one more than the last existing project id

        listener.requestInitialized(new ServletRequestEvent(context, request));

        ProjectJspBean projectBeanInstance = SpringContextService.getBean("enroll.ProjectJspBean");
        //mock this method called in the return - we don't need it, and it gives NPE if invoked here
        ProjectJspBean instance1 = Mockito.spy(projectBeanInstance);
        Mockito.doReturn("Return value not needed - ignored").when(instance1).redirectView(any(), anyString());

        instance1.doCreateProject(request);

        //at this point this project should be in the database
        projectList = ProjectHome.getProjectsList();

        // this should be the project we just added
        Project latestProject = projectList.get(newProjectId - 1);

        assertEquals(name, latestProject.getName());
        assertEquals(size, Integer.toString(latestProject.getSize()));
        assertEquals(1, latestProject.getActive());
        assertEquals(0, latestProject.getCurrentSize());
        assertEquals(newProjectId, latestProject.getId());

        XPageApplicationEntry xPageApplicationEntry = XPageAppService.getApplicationEntry("enrollment");
        assertNotNull( xPageApplicationEntry);

        EnrollmentXPage instance2 = (EnrollmentXPage) XPageAppService.getApplicationInstance(xPageApplicationEntry);

        //test addition of an enrollment
        Enrollment newEnrollment = new Enrollment();
        newEnrollment.setProgram(name);
        newEnrollment.setEmail("user@place.com");
        newEnrollment.setName("Albert Batross");
        newEnrollment.setPhone("867-5309");

        request = new MockHttpServletRequest();
        request.addParameter(PARAMETER_PROGRAM_ENROLLMENT, String.valueOf(newEnrollment.getProgram()));
        request.addParameter(PARAMETER_NAME_ENROLLMENT, newEnrollment.getName());
        request.addParameter(PARAMETER_EMAIL_ENROLLMENT, String.valueOf(newEnrollment.getEmail()));
        request.addParameter(PARAMETER_PHONE_ENROLLMENT, newEnrollment.getPhone());
        request.addParameter(PARAMETER_ID_PROJECT, String.valueOf(latestProject.getId()));

        instance2.doCreateEnrollment(request);

        List<Enrollment> enrollmentList = EnrollmentHome.getEnrollmentsList();
        Enrollment addedEnrollment = EnrollmentHome.getEnrollmentsList().get(enrollmentList.size() - 1);
        assertEquals(newEnrollment.getEmail(), addedEnrollment.getEmail());
        assertEquals(newEnrollment.getName(), addedEnrollment.getName());
        assertEquals(newEnrollment.getPhone(), addedEnrollment.getPhone());
        assertEquals(newEnrollment.getProgram(), addedEnrollment.getProgram());
        assertEquals(enrollmentList.size(), addedEnrollment.getId());

        Project project = ProjectHome.findByPrimaryKey(latestProject.getId());
        assertEquals(1, project.getCurrentSize());

        listener.requestDestroyed( new ServletRequestEvent( context, request ) );
    }
}
