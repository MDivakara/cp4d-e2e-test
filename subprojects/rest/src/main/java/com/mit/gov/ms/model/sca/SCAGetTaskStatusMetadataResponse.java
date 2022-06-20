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

@ApiModel(value = "SCAGetTaskStatusMetadataResponse")
public class SCAGetTaskStatusMetadataResponse {
    
    public SCAGetTaskStatusMetadataResponse(String task_id, String workspace_id) {
        super();
        this.task_id = task_id;
        this.workspace_id = workspace_id;
    }

    @ApiModelProperty(required = true, value = "task_id")
    private String task_id;
    
    @ApiModelProperty(required = true, value = "workspace_id")
    private String workspace_id;

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getWorkspace_id() {
        return workspace_id;
    }

    public void setWorkspace_id(String workspace_id) {
        this.workspace_id = workspace_id;
    }

}
