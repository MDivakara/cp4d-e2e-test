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

@ApiModel(value = "ModelDeletionResponseBody")

public class ModelDeltionResponseBody {
    
    public ModelDeltionResponseBody() {
        super();
        // TODO Auto-generated constructor stub
    }

    public ModelDeltionResponseBody(String model_id, String model_version_id) {
        super();
        this.model_id = model_id;
    }

    @ApiModelProperty(required = true, value = "sample model ID")
    private String model_id;
    
    public String getModel_id() {
        return model_id;
    }

    public void setModel_id(String model_id) {
        this.model_id = model_id;
    }
    
}
