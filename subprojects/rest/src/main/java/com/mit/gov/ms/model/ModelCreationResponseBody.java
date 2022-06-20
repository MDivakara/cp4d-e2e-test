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

@ApiModel(value = "ModelCreationResponseBody")

public class ModelCreationResponseBody {
	
	public ModelCreationResponseBody() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ModelCreationResponseBody(String model_id, String model_version_id) {
		super();
		this.model_id = model_id;
		this.model_version_id = model_version_id;
	}

	@ApiModelProperty(required = true, value = "sample model ID")
    private String model_id;
	
	@ApiModelProperty(required = true, value = "sample model versionID.")
    private String model_version_id;

	public String getModel_id() {
		return model_id;
	}

	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}

	public String getModel_version_id() {
		return model_version_id;
	}

	public void setModel_version_id(String model_version_id) {
		this.model_version_id = model_version_id;
	}

	

}
