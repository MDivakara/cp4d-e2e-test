/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.model.task;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "TaskGetResponse")
public class TaskGetResponse {
    
    public TaskGetResponse(List<TaskGetListResponse> tasks) {
        super();
        this.tasks = tasks;
    }

    @ApiModelProperty(required = true, value = "tasks")
    private List<TaskGetListResponse> tasks;

    public List<TaskGetListResponse> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskGetListResponse> tasks) {
        this.tasks = tasks;
    }
    
}
