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

@ApiModel(value = "ModelCreationRequestBody")

public class ModelCreationRequestBody {

	public ModelCreationRequestBody() {
		super();
	}

	public ModelCreationRequestBody(String name, String description) {
		super();
		this.name = name;
		this.description = description;
	}

	@ApiModelProperty(required = true, value = "sample model name")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ApiModelProperty(required = true, value = "sample model description")
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
