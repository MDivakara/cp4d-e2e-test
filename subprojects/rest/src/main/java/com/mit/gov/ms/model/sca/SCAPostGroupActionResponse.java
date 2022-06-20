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

@ApiModel(value = "SCAPostGroupActionResponse")
public class SCAPostGroupActionResponse {
    
    public SCAPostGroupActionResponse(String action) {
        super();
        this.action = action;
    }

    @ApiModelProperty(required = true, value = "action", example = "accept/reject/restore")
    String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

}
