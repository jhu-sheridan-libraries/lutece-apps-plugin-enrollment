package fr.paris.lutece.plugins.enroll.business.enrollment;

import javax.validation.constraints.*;
import org.hibernate.validator.constraints.*;
import java.io.Serializable;

public class Enrollment implements Serializable {
  private int _nId;
  private String _strEnrollment;
  private String _strContactName;
  private String _strContactEmail;
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
}
