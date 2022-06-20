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

@ApiModel(value = "GetModelByTimeResponseBody")

public class GetModelByTimeResponseBody {
	
	public GetModelByTimeResponseBody() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GetModelByTimeResponseBody(String name, String description, String model_version_id) {
		super();
		this.name = name;
		this.description = description;
		this.model_version_id = model_version_id;
	}

	@ApiModelProperty(required = true, value = "drd_id.")
    private String name;
	
	@ApiModelProperty(required = true, value = "drd_id.")
    private String description;
	
	@ApiModelProperty(required = true, value = "drd_id.")
    private String model_version_id;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getModel_version_id() {
		return model_version_id;
	}

	public void setModel_version_id(String model_version_id) {
		this.model_version_id = model_version_id;
	}

}
