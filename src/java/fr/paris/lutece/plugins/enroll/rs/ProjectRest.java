 /*
 * Copyright (c) 2002-2016, Mairie de Paris
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
package fr.paris.lutece.plugins.enroll.rs;

import fr.paris.lutece.plugins.enroll.business.Project;
import fr.paris.lutece.plugins.enroll.business.ProjectHome;
import fr.paris.lutece.plugins.rest.service.RestConstants;
import fr.paris.lutece.util.json.ErrorJsonResponse;
import fr.paris.lutece.util.json.JsonResponse;
import fr.paris.lutece.util.json.JsonUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import java.util.List;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * ProjectRest
 */
@Path( RestConstants.BASE_PATH + Constants.API_PATH + Constants.VERSION_PATH + Constants.PROJECT_PATH )
public class ProjectRest
{
    private static final int VERSION_1 = 1;
    private final Logger _logger = Logger.getLogger( RestConstants.REST_LOGGER );

    /**
     * Get Project List
     * @param nVersion the API version
     * @return the Project List
     */
    @GET
    @Path( StringUtils.EMPTY )
    @Produces( MediaType.APPLICATION_JSON )
    public Response getProjectList( @PathParam( Constants.VERSION ) Integer nVersion )
    {
        switch ( nVersion )
        {
            case VERSION_1:
                return getProjectListV1( );
            default:
                break;
        }
        _logger.error( Constants.ERROR_NOT_FOUND_VERSION );
        return Response.status( Response.Status.NOT_FOUND )
                .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), Constants.ERROR_NOT_FOUND_VERSION ) ) )
                .build( );
    }

    /**
     * Get Project List V1
     * @return the Project List for the version 1
     */
    private Response getProjectListV1( )
    {
        List<Project> _listProjects = ProjectHome.getProjectsList( );

        if( _listProjects.isEmpty( ) )
        {
            return Response.status( Response.Status.NO_CONTENT )
                .entity( JsonUtil.buildJsonResponse( new JsonResponse( Constants.EMPTY_OBJECT ) ) )
                .build( );
        }
        return Response.status( Response.Status.OK )
                .entity( JsonUtil.buildJsonResponse( new JsonResponse( _listProjects ) ) )
                .build( );
    }

    /**
     * Create Project
     * @param nVersion the API version
     * @param name the name
     * @param email the email
     * @param phone the phone
     * @return the Project if created
     */
    @POST
    @Path( StringUtils.EMPTY )
    @Produces( MediaType.APPLICATION_JSON )
    public Response createProject(
    @FormParam( Constants.PROJECT_ATTRIBUTE_NAME ) String name,
    @FormParam( Constants.PROJECT_ATTRIBUTE_SIZE ) int size,
    @FormParam( Constants.PROJECT_ATTRIBUTE_CURRENT_SIZE ) int currentsize,
    @FormParam( Constants.PROJECT_ATTRIBUTE_ACTIVE ) int active,
    @PathParam( Constants.VERSION ) Integer nVersion )
    {
        switch ( nVersion )
        {
            case VERSION_1:
                return createProjectV1( name, size, currentsize, active );
            default:
                break;
        }
        _logger.error( Constants.ERROR_NOT_FOUND_VERSION );
        return Response.status( Response.Status.NOT_FOUND )
                .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), Constants.ERROR_NOT_FOUND_VERSION ) ) )
                .build( );
    }

    /**
     * Create Project V1
     * @param name the name
     * @param email the email
     * @param phone the phone
     * @return the Project if created for the version 1
     */
    private Response createProjectV1( String name, int size, int currentsize, int active )
    {
        if ( StringUtils.isEmpty( name ) )
        {
            _logger.error( Constants.ERROR_BAD_REQUEST_EMPTY_PARAMETER );
            return Response.status( Response.Status.BAD_REQUEST )
                    .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.BAD_REQUEST.name( ), Constants.ERROR_BAD_REQUEST_EMPTY_PARAMETER ) ) )
                    .build( );
        }

        Project _project = new Project( );
        _project.setName( name );
        _project.setSize( size );
        _project.setCurrentSize( currentsize );
        _project.setActive( active );
        ProjectHome.create( _project );

        return Response.status( Response.Status.OK )
                .entity( JsonUtil.buildJsonResponse( new JsonResponse( _project ) ) )
                .build( );
    }

    /**
     * Modify Project
     * @param nVersion the API version
     * @param id the id
     * @param name the name
     * @param email the email
     * @param phone the phone
     * @return the Project if modified
     */
    @PUT
    @Path( Constants.ID_PATH )
    @Produces( MediaType.APPLICATION_JSON )
    public Response modifyProject(
    @PathParam( Constants.ID ) Integer id,
    @FormParam( Constants.PROJECT_ATTRIBUTE_NAME ) String name,
    @FormParam( Constants.PROJECT_ATTRIBUTE_SIZE ) int size,
    @FormParam( Constants.PROJECT_ATTRIBUTE_CURRENT_SIZE ) int currentsize,
    @FormParam( Constants.PROJECT_ATTRIBUTE_ACTIVE ) int active,
    @PathParam( Constants.VERSION ) Integer nVersion )
    {
        switch ( nVersion )
        {
            case VERSION_1:
                return modifyProjectV1( id, name, size, currentsize, active );
            default:
                break;
        }
        _logger.error( Constants.ERROR_NOT_FOUND_VERSION );
        return Response.status( Response.Status.NOT_FOUND )
                .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), Constants.ERROR_NOT_FOUND_VERSION ) ) )
                .build( );
    }

    /**
     * Modify Project V1
     * @param id the id
     * @param name the name
     * @param email the email
     * @param phone the phone
     * @return the Project if modified for the version 1
     */
    private Response modifyProjectV1( Integer id, String name, int size, int currentsize, int active )
    {
        if ( StringUtils.isEmpty( name ) )
        {
            _logger.error( Constants.ERROR_BAD_REQUEST_EMPTY_PARAMETER );
            return Response.status( Response.Status.BAD_REQUEST )
                    .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.BAD_REQUEST.name( ), Constants.ERROR_BAD_REQUEST_EMPTY_PARAMETER ) ) )
                    .build( );
        }

        Project _project = ProjectHome.findByPrimaryKey( id );
        if ( _project == null )
        {
            _logger.error( Constants.ERROR_NOT_FOUND_RESOURCE );
            return Response.status( Response.Status.NOT_FOUND )
                    .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), Constants.ERROR_NOT_FOUND_RESOURCE ) ) )
                    .build( );
        }

        _project.setName( name );
        _project.setSize( size );
        _project.setCurrentSize( currentsize );
        _project.setActive( active );
        ProjectHome.update( _project );

        return Response.status( Response.Status.OK )
                .entity( JsonUtil.buildJsonResponse( new JsonResponse( _project ) ) )
                .build( );
    }

    /**
     * Delete Project
     * @param nVersion the API version
     * @param id the id
     * @return the Project List if deleted
     */
    @DELETE
    @Path( Constants.ID_PATH )
    @Produces( MediaType.APPLICATION_JSON )
    public Response deleteProject(
    @PathParam( Constants.VERSION ) Integer nVersion,
    @PathParam( Constants.ID ) Integer id )
    {
        switch ( nVersion )
        {
            case VERSION_1:
                return deleteProjectV1( id );
            default:
                break;
        }
        _logger.error( Constants.ERROR_NOT_FOUND_VERSION );
        return Response.status( Response.Status.NOT_FOUND )
                .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), Constants.ERROR_NOT_FOUND_VERSION ) ) )
                .build( );
    }

    /**
     * Delete Project V1
     * @param id the id
     * @return the Project List if deleted for the version 1
     */
    private Response deleteProjectV1( Integer id )
    {
        Project _project = ProjectHome.findByPrimaryKey( id );
        if ( _project == null )
        {
            _logger.error( Constants.ERROR_NOT_FOUND_RESOURCE );
            return Response.status( Response.Status.NOT_FOUND )
                    .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), Constants.ERROR_NOT_FOUND_RESOURCE ) ) )
                    .build( );
        }

        ProjectHome.remove( id );

        return Response.status( Response.Status.OK )
                .entity( JsonUtil.buildJsonResponse( new JsonResponse( Constants.EMPTY_OBJECT ) ) )
                .build( );
    }

    /**
     * Get Project
     * @param nVersion the API version
     * @param id the id
     * @return the Project
     */
    @GET
    @Path( Constants.ID_PATH )
    @Produces( MediaType.APPLICATION_JSON )
    public Response getProject(
    @PathParam( Constants.VERSION ) Integer nVersion,
    @PathParam( Constants.ID ) Integer id )
    {
        switch ( nVersion )
        {
            case VERSION_1:
                return getProjectV1( id );
            default:
                break;
        }
        _logger.error( Constants.ERROR_NOT_FOUND_VERSION );
        return Response.status( Response.Status.NOT_FOUND )
                .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), Constants.ERROR_NOT_FOUND_VERSION ) ) )
                .build( );
    }

    /**
     * Get Project V1
     * @param id the id
     * @return the Project for the version 1
     */
    private Response getProjectV1( Integer id )
    {
        Project _project = ProjectHome.findByPrimaryKey( id );
        if ( _project == null )
        {
            _logger.error( Constants.ERROR_NOT_FOUND_RESOURCE );
            return Response.status( Response.Status.NOT_FOUND )
                    .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), Constants.ERROR_NOT_FOUND_RESOURCE ) ) )
                    .build( );
        }

        return Response.status( Response.Status.OK )
                .entity( JsonUtil.buildJsonResponse( new JsonResponse( _project ) ) )
                .build( );
    }
}
