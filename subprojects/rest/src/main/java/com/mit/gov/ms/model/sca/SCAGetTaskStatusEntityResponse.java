/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.model.sca;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "SCAGetTaskStatusEntityResponse")
public class SCAGetTaskStatusEntityResponse {
    
    public SCAGetTaskStatusEntityResponse(String status, String message) {
        super();
        this.status = status;
        this.message = message;
    }

    @ApiModelProperty(required = true, value = "status")
    private String status;
    
    @ApiModelProperty(required = true, value = "message")
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
