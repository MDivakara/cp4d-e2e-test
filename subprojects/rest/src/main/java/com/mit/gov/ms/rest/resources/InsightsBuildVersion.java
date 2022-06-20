/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.mit.gov.ms.common.Constants;
import com.mit.gov.ms.model.SwaggerConstants;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Path("/build")
@Api(value = SwaggerConstants.INSIGHTS_BUILD_VERSION, hidden = true)
@Produces(MediaType.TEXT_PLAIN)
public class InsightsBuildVersion {

    private static final String version = Constants.class.getPackage().getImplementationVersion();

    @GET
    @ApiOperation(value = "Get build version information",
            notes = "Returns information on Insights build version provided by this service.")
    public Response getBuildVersionInfo() {
        return Response.status(Status.OK).entity(version)
                .header("X-Content-Type-Options", "nosniff")
                .header("X-XSS-Protection", "1; mode=block")
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .header("Pragma", "no-cache")
                .build();
    }

}
