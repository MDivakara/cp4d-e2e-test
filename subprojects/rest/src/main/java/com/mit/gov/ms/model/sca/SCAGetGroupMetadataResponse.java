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

@ApiModel(value = "SCAGetGroupMetadataResponse")
public class SCAGetGroupMetadataResponse {
    
    public SCAGetGroupMetadataResponse(String group_id, String state, String total_count) {
        super();
        this.group_id = group_id;
        this.state = state;
        this.total_count = total_count;
    }

    @ApiModelProperty(required = true, value = "group_id")
    private String group_id;
    
    @ApiModelProperty(required = true, value = "state")
    private String state;
    
    @ApiModelProperty(required = true, value = "total_count")
    private String total_count;

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTotal_count() {
        return total_count;
    }

    public void setTotal_count(String total_count) {
        this.total_count = total_count;
    }

}
