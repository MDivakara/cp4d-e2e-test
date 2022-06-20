/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.model.task;

import org.apache.wink.json4j.JSONObject;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "TaskGetEntityResponse")
public class TaskGetEntityResponse {
    

    public TaskGetEntityResponse(String status, String message, JSONObject output_info) {
        super();
        this.status = status;
        this.message = message;
        this.output_info = output_info;
    }

    @ApiModelProperty(required = true, value = "status")
    private String status;
    
    @ApiModelProperty(required = true, value = "message")
    private String message;
    
    @ApiModelProperty(required = true, value = "output_info")
    private JSONObject output_info;

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

    public JSONObject getOutput_info() {
        return output_info;
    }

    public void setOutput_info(JSONObject output_info) {
        this.output_info = output_info;
    }

}
