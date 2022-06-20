/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.rest.providers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.gov.ms.common.security.SessionManager;

public class InsightsDefaultExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGGER = LoggerFactory.getLogger(InsightsDefaultExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        String trace_id = SessionManager.getCurrentSession().getTraceId();
        //String trace = RandomUtils.randomBase36(RequestConsts.TRACE_LENGTH);
        String message = exception.getMessage();
        LOGGGER.error(message, exception);
//        ErrorContainer container = new ErrorContainer();
//        container.setTrace(trace_id);
//        container.setCode(ErrorInfo.CODE_INTERNAL_ERROR);
//        container.setMessage(Messages.getUnexpectedErrorMessage());
        //return Response.serverError().entity(container).type(MediaType.APPLICATION_JSON).build();
        return Response.serverError().entity(null).type(MediaType.APPLICATION_JSON).build();
    }

}
