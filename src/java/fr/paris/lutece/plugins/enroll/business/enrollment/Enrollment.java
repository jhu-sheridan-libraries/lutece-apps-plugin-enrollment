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

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import java.util.Objects;

public class Enrollment implements Serializable {
  private int _nId;
  private String _strEnrollment;

  @NotEmpty
  private String _strContactName;

  @Email
  private String _strContactEmail;

  @NotEmpty
  private String _strContactNumber;

  public int getId() {
    return _nId;
  }

  public void setId(int id) {
    _nId = id;
  }

  public String getProgram() {
    return _strEnrollment;
  }

  public void setProgram(String enrollmentName) {
    _strEnrollment = enrollmentName;
  }

  public String getName() {
    return _strContactName;
  }

  public void setName(String name) {
    _strContactName = name;
  }

  public String getEmail() {
    return _strContactEmail;
  }

  public void setEmail(String email) {
    _strContactEmail = email;
  }

  public String getPhone() {
    return _strContactNumber;
  }

  public void setPhone(String number) {
    _strContactNumber = number;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Enrollment that = (Enrollment) o;
    return _nId == that._nId &&
            Objects.equals(_strEnrollment, that._strEnrollment) &&
            Objects.equals(_strContactName, that._strContactName) &&
            Objects.equals(_strContactEmail, that._strContactEmail) &&
            Objects.equals(_strContactNumber, that._strContactNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(_nId, _strEnrollment, _strContactName, _strContactEmail, _strContactNumber);
  }
}
