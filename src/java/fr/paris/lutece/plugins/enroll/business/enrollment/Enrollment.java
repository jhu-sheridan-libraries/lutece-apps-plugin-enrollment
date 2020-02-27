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
