/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.model.drd;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "DRDGetTaskStatusResponse")

public class DRDGetTaskStatusResponse {
	public DRDGetTaskStatusResponse(String status) {
		super();
		this.status = status;
	}

	public DRDGetTaskStatusResponse() {
		super();
	}

	@ApiModelProperty(required = true, value = "Status")
	private String status;

	@ApiModelProperty(required = true, value = "message")
	private String message;

	@ApiModelProperty(required = true, value = "taskId")
	private String task_id;

	public String getTask_id() {
		return task_id;
	}

	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
