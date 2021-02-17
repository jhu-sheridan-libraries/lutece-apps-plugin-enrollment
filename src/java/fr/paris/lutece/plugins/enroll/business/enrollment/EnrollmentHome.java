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

package fr.paris.lutece.plugins.enroll.business.enrollment;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;

/**
 * This class provides instances management methods (create, find, ...) for Enrollment objects
 */
public final class EnrollmentHome {
  // Static variable pointed at the DAO instance
  private static Plugin _plugin = PluginService.getPlugin("enroll");
  private static IEnrollmentDAO _dao = SpringContextService.getBean("enroll.enrollmentDAO");

  /**
   * Private constructor - this class need not be instantiated
   */
  private EnrollmentHome() {
  }

  /**
   * Create an instance of the Enrollment class
   * @param enrollment The instance of the Enrollment which contains the information to store
   * @return The  instance of project which has been created with its primary key.
   */
  public static Enrollment create(Enrollment enrollment) {
    _dao.insert(enrollment, _plugin);
    return enrollment;
  }

  /**
   * Update of the Enrollment which is specified in parameter
   * @param enrollment The instance of the Enrollment which contains the data to store
   * @return The instance of the Enrollment which has been updated
   */
  public static Enrollment update(Enrollment enrollment) {
    _dao.store(enrollment, _plugin);
    return enrollment;
  }

  /**
   * Remove the enrollment whose identifier is specified in parameter
   * @param nKey The enrollment Id
   */
  public static void remove(int nKey) {
    _dao.delete(nKey, _plugin);
  }

  /**
   * Returns an instance of an enrollment whose identifier is specified in parameter
   * @param nKey The enrollment primary key
   * @return an instance of Enrollment
   */
  public static Enrollment findByPrimaryKey(int nKey) {
    return _dao.load(nKey, _plugin);
  }

  /**
   * Load the data of all the enrollment objects and returns them as a list
   * @return the list which contains the data of all the enrollmentt objects
   */
  public static List<Enrollment> getEnrollmentsList() {
    return _dao.selectEnrollmentsList(_plugin);
  }

  /**
   * Load the id of all the enrollment objects and returns them as a list
   * @return the list which contains the id of all the enrollment objects
   */
  public static List<Integer> getIdEnrollmentsList() {
    return _dao.selectIdEnrollmentsList(_plugin);
  }

  /**
   * Load the data of all the enrollment objects and returns them as a referenceList
   * @return the referenceList which contains the data of all the enrollment objects
   */
  public static ReferenceList getEnrollmentsReferenceList() {
    return _dao.selectEnrollmentsReferenceList(_plugin);
  }

  /**
   * Load the data of all enrollments for a program and returns them as a list of enrollments sorted by name
   * @return the list of enrollments sorted by name
   */
  public static List<Enrollment> getEnrollmentsForProgram(String program) {
    return _dao.selectEnrollmentsForProgram(program, _plugin);
  }
}
