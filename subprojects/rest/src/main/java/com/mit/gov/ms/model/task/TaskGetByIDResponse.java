/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.model.task;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "TaskGetByIDResponse")
public class TaskGetByIDResponse {
    
    public TaskGetByIDResponse(TaskGetMetdataResponse metadata, TaskGetEntityResponse entity) {
        super();
        this.metadata = metadata;
        this.entity = entity;
    }

    @ApiModelProperty(required = true, value = "metadata")
    private TaskGetMetdataResponse metadata;
    
    @ApiModelProperty(required = true, value = "entity")
    private TaskGetEntityResponse entity;

    public TaskGetMetdataResponse getMetadata() {
        return metadata;
    }

    public void setMetadata(TaskGetMetdataResponse metadata) {
        this.metadata = metadata;
    }

    public TaskGetEntityResponse getEntity() {
        return entity;
    }

    public void setEntity(TaskGetEntityResponse entity) {
        this.entity = entity;
    }

}
