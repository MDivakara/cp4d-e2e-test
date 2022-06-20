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

@ApiModel(value = "DRDPostResponseBody")

public class DRDPostResponseBody {

	public DRDPostResponseBody(String drd_id, String task_id) {
		super();
		this.drd_id = drd_id;
		this.task_id = task_id;
	}

	@ApiModelProperty(required = true, value = "drd_id")
	private String drd_id;

	@ApiModelProperty(required = true, value = "task_id")
	private String task_id;

	public String getDrd_id() {
		return drd_id;
	}

	public void setDrd_id(String drd_id) {
		this.drd_id = drd_id;
	}

	public String getTask_id() {
		return task_id;
	}

	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}

}
