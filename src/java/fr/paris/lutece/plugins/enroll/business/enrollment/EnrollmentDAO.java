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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;

public final class EnrollmentDAO implements IEnrollmentDAO {
  private static final String SQL_QUERY_NEW_PK = "SELECT max( id_enrollment ) FROM enroll_enrollment";
  private static final String SQL_QUERY_SELECT = "SELECT * FROM enroll_enrollment WHERE id_enrollment = ?";
  private static final String SQL_QUERY_INSERT = "INSERT INTO enroll_enrollment ( id_enrollment, program, name, email, phone ) VALUES ( ?, ?, ?, ?, ? )";
  private static final String SQL_QUERY_DELETE = "DELETE FROM enroll_enrollment WHERE id_enrollment = ?";
  private static final String SQL_QUERY_UPDATE = "UPDATE enroll_enrollment SET id_enrollment = ?, program = ?, name = ?, email = ?, phone = ? WHERE id_enrollment = ?";
  private static final String SQL_QUERY_SELECTALL = "SELECT * FROM enroll_enrollment";
  private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_enrollment FROM enroll_enrollment";
  private static final String SQL_QUERY_SELECT_BY_PROGRAM = "SELECT * FROM enroll_enrollment WHERE program = ?  ORDER BY name ASC";

    /**
     * {@inheritDoc }
     */
  public int newPrimaryKey( Plugin plugin)
  {
      DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK , plugin  );
      daoUtil.executeQuery( );
      int nKey = 1;

      if( daoUtil.next( ) )
      {
          nKey = daoUtil.getInt( 1 ) + 1;
      }

      daoUtil.free();
      return nKey;
  }

    /**
     * {@inheritDoc }
     */
  @Override
  public synchronized void insert(Enrollment enrollment, Plugin plugin) {
    DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
    enrollment.setId( newPrimaryKey( plugin ) );
    int nIndex = 1;

    daoUtil.setInt( nIndex++ , enrollment.getId( ) );
    daoUtil.setString(nIndex++, enrollment.getProgram());
    daoUtil.setString( nIndex++ , enrollment.getName( ) );
    daoUtil.setString( nIndex++ , enrollment.getEmail( ) );
    daoUtil.setString( nIndex , enrollment.getPhone( ) );

    daoUtil.executeUpdate( );
    daoUtil.free( );
  }

    /**
     * {@inheritDoc }
     */
  @Override
  public void delete(int nKey, Plugin plugin) {
    DAOUtil daoUtil = new DAOUtil(SQL_QUERY_DELETE, plugin);
    daoUtil.setInt( 1 , nKey );
    daoUtil.executeUpdate( );
    daoUtil.free( );
  }

    /**
     * {@inheritDoc }
     */
  @Override
  public void store(Enrollment enrollment, Plugin plugin) {
    DAOUtil daoUtil = new DAOUtil(SQL_QUERY_UPDATE, plugin);
    int nIndex = 1;
    daoUtil.setInt( nIndex++ , enrollment.getId( ) );
    daoUtil.setString( nIndex++ , enrollment.getProgram( ) );
    daoUtil.setString( nIndex++ , enrollment.getName( ) );
    daoUtil.setString( nIndex++ , enrollment.getEmail( ) );
    daoUtil.setString( nIndex++ , enrollment.getPhone( ) );
    daoUtil.setInt( nIndex , enrollment.getId( ) );

    daoUtil.executeUpdate( );
    daoUtil.free( );
  }

    /**
     * {@inheritDoc }
     */
  @Override
  public Enrollment load(int nKey, Plugin plugin) {
      DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
      daoUtil.setInt( 1 , nKey );
      daoUtil.executeQuery( );
      Enrollment enrollment = null;

      if ( daoUtil.next( ) )
      {
          enrollment = new Enrollment();
          int nIndex = 1;

          enrollment.setId( daoUtil.getInt( nIndex++ ) );
          enrollment.setProgram( daoUtil.getString( nIndex++ ) );
          enrollment.setName( daoUtil.getString( nIndex++ ) );
          enrollment.setEmail( daoUtil.getString( nIndex++ ));
          enrollment.setPhone( daoUtil.getString( nIndex) );
      }

      daoUtil.free( );
      return enrollment;
  }

    /**
     * {@inheritDoc }
     */
  @Override
  public List<Enrollment> selectEnrollmentsList( Plugin plugin )
  {
      List<Enrollment> enrollmentList = new ArrayList<>();
      DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
      daoUtil.executeQuery(  );

      while ( daoUtil.next(  ) )
      {
          Enrollment enrollment = new Enrollment(  );
          int nIndex = 1;

          enrollment.setId( daoUtil.getInt( nIndex++ ) );
          enrollment.setProgram(daoUtil.getString(nIndex++));
          enrollment.setName( daoUtil.getString( nIndex++ ) );
          enrollment.setEmail( daoUtil.getString( nIndex++ ) );
          enrollment.setPhone( daoUtil.getString( nIndex ) );

          enrollmentList.add( enrollment );
      }

      daoUtil.free( );
      return enrollmentList;
  }

    /**
     * {@inheritDoc }
     */
  @Override
  public List<Integer> selectIdEnrollmentsList(Plugin plugin) {
          List<Integer> idEnrollmentList = new ArrayList<>();
          DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin );
          daoUtil.executeQuery(  );

          while ( daoUtil.next(  ) )
          {
              idEnrollmentList.add( daoUtil.getInt( 1 ) );
          }
          daoUtil.free( );
          return idEnrollmentList;
  }


    /**
     * {@inheritDoc }
     */
  @Override
  public ReferenceList selectEnrollmentsReferenceList(Plugin plugin) {
      ReferenceList enrollmentList = new ReferenceList();
      DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
      daoUtil.executeQuery(  );

      while ( daoUtil.next(  ) )
      {
          enrollmentList.addItem( daoUtil.getInt( 1 ) , daoUtil.getString( 2 ) );
      }

      daoUtil.free( );
      return enrollmentList;
  }

    /**
     *  {@inheritDoc }
     */
    @Override
    public List<Enrollment> selectEnrollmentsForProgram(String program, Plugin plugin) {
        List<Enrollment> enrollmentList = new ArrayList<>();
          DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_PROGRAM, plugin );
          daoUtil.setString(1, program);
          daoUtil.executeQuery(  );

          while ( daoUtil.next(  ) )
          {
              Enrollment enrollment = new Enrollment(  );
              int nIndex = 1;

              enrollment.setId( daoUtil.getInt( nIndex++ ) );
              enrollment.setProgram(daoUtil.getString(nIndex++));
              enrollment.setName( daoUtil.getString( nIndex++ ) );
              enrollment.setEmail( daoUtil.getString( nIndex++ ) );
              enrollment.setPhone( daoUtil.getString( nIndex ) );

              enrollmentList.add( enrollment );
          }

          daoUtil.free( );
          return enrollmentList;
        }
}
