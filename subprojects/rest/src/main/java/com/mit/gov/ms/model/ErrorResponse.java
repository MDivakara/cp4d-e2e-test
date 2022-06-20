/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ErrorResponse")
public class ErrorResponse {

    public ErrorResponse(String trace, String message) {
        super();
        this.trace = trace;
        this.message = message;
    }

    @ApiModelProperty(required = true, value = "trace")
    private String trace;
    
    @ApiModelProperty(required = true, value = "message")
    private String message;

    public String getTrace() {
        return trace;
    }

    public void setTrace(String trace) {
        this.trace = trace;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
