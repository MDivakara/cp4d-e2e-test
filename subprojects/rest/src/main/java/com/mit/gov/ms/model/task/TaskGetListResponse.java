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

@ApiModel(value = "TaskGetListResponse")
public class TaskGetListResponse {

    public TaskGetListResponse(String task_id, String task_type, String created_by, String created_at,
            String updated_at, String started_at) {
        super();
        this.task_id = task_id;
        this.task_type = task_type;
        this.created_by = created_by;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.started_at = started_at;
    }

    @ApiModelProperty(required = true, value = "task_id")
    private String task_id;
    
    @ApiModelProperty(required = true, value = "task_type")
    private String task_type;
    
    @ApiModelProperty(required = true, value = "created_by")
    private String created_by;
    
    @ApiModelProperty(required = true, value = "created_at")
    private String created_at;
    
    @ApiModelProperty(required = true, value = "updated_at")
    private String updated_at;
    
    @ApiModelProperty(required = true, value = "started_at")
    private String started_at;

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getTask_type() {
        return task_type;
    }

    public void setTask_type(String task_type) {
        this.task_type = task_type;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getStarted_at() {
        return started_at;
    }

    public void setStarted_at(String started_at) {
        this.started_at = started_at;
    }
}
