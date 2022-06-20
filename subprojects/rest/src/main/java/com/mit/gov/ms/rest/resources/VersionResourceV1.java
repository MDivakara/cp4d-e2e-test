/*
 * IBM Confidential
 * OCO Source Materials
 * 5724-Q36
 * Copyright IBM Corp. 2019
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.mit.gov.ms.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mit.gov.ms.model.SwaggerConstants;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Path("")
@Api(value = SwaggerConstants.INSIGHTS_API_VERSION, hidden = true)
@Produces(MediaType.APPLICATION_JSON)
public class VersionResourceV1 {

    public static final String ID_V1 = "v1";

    @GET
    @ApiOperation(value = "Get API version information",
            notes = "Returns information on Insights API version provided by this service.")
    public String getVersionInfo() {
//        ApiVersion version = new ApiVersion();
//        version.setId(ID_V1);
//        version.setStatus(ApiVersion.STATUS_CURRENT);
        return "1.1.0";
    }

}
