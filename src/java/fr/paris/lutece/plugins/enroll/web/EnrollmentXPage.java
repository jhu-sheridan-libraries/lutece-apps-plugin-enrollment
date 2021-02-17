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
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;

import java.util.Map;
import java.util.Collection;
import fr.paris.lutece.util.ReferenceList;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;

@Controller( xpageName = "enrollment" , pageTitleI18nKey = "enroll.xpage.enrollment.pageTitle" , pagePathI18nKey = "enroll.xpage.enrollment.pagePathLabel" )
public class EnrollmentXPage extends MVCApplication {

  private static final String TEMPLATE_CREATE_ENROLLMENT="/skin/plugins/enroll/create_enrollment.html";
  private static final String TEMPLATE_ENROLLMENT_RESULT="/skin/plugins/enroll/enrollment_result.html";
  private static final String TEMPLATE_PROJECT_STATUS="/skin/plugins/enroll/project_status.html";

  // Parameters
  private static final String MARK_LIST_PROJECTS = "refListProjects";
  private static final String VIEW_ENROLLMENT = "enrollment";
  private static final String ACTION_CREATE_ENROLLMENT = "createEnrollment";
  private static final long serialVersionUID = 1L;


  @Action( ACTION_CREATE_ENROLLMENT )
  public XPage doCreateEnrollment( HttpServletRequest request )  {
      Enrollment enrollment = new Enrollment(  );
      populate( enrollment, request );
      Map<String, Object> model = getModel();

      // Check constraints
      if ( !validateBean( enrollment, getLocale( request ) ) )
      {
          model.put( "invalid", true);
          return getXPage( TEMPLATE_ENROLLMENT_RESULT, request.getLocale(), model );
      }

      Project project = ProjectHome.findByName( enrollment.getProgram() );

      if ( project != null ) {
          if ( project.canAdd() ) {
              project.setCurrentSize( project.getCurrentSize() + 1 );
              ProjectHome.update(project);
              EnrollmentHome.create( enrollment );
              model.put("success", true);
          } else {
              model.put("success", false);
              model.put("inactive", project.getActive()==0);
              model.put( "full", project.atCapacity());
          }
      } else {//could not find a project by this name - supplied project is not valid
          model.put("success", false);
          model.put("invalid", true);
      }

      return getXPage( TEMPLATE_ENROLLMENT_RESULT, request.getLocale(), model );
  }

  /**
   * Get the HTML content of the enrollment form
   *
   * @param request
   *            The request
   * @return The HTML content
   */
  @View( value = VIEW_ENROLLMENT , defaultView = true )
  public XPage getEnrollmentHtml(HttpServletRequest request)
  {

      Map<String, Object> model = new HashMap<>( );
      String program = request.getParameter("program");

      if ( program == null || program.isEmpty()) {//no program specified - return select list for project
          Collection<Project> listProjects = ProjectHome.getProjectsList();
          ReferenceList refListProjects = new ReferenceList();
          for (Project project : listProjects) {
              if (project.canAdd()) {
                  refListProjects.addItem(project.getId(), project.getName());
              }
          }
          model.put(MARK_LIST_PROJECTS, refListProjects);
          return getXPage(TEMPLATE_CREATE_ENROLLMENT, request.getLocale(  ), model);
      }else {//program specified; return form if it can add, else return information about project
          Project project = ProjectHome.findByName(program);
          if (project != null && project.canAdd()) {
              model.put("program", program);
              return getXPage(TEMPLATE_CREATE_ENROLLMENT, request.getLocale(  ), model);
          } else {
              if (project == null ) {
                  model.put("invalid", true);
              } else {
                  model.put("inactive", project.getActive() == 0);
                  model.put("full", project.atCapacity());
              }
              return getXPage(TEMPLATE_PROJECT_STATUS, request.getLocale(  ), model);
          }

      }
  }
}
