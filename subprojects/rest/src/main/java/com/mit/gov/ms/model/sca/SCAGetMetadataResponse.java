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

@ApiModel(value = "SCAGetMetadataResponse")
public class SCAGetMetadataResponse {
    
    public SCAGetMetadataResponse(String workspace_id, String started_at, String started_by, String num_groups,
            String total_count) {
        super();
        this.workspace_id = workspace_id;
        this.started_at = started_at;
        this.started_by = started_by;
        this.num_groups = num_groups;
        this.total_count = total_count;
    }

    @ApiModelProperty(required = true, value = "workspace_id")
    private String workspace_id;
    
    @ApiModelProperty(required = true, value = "started_at")
    private String started_at;
    
    @ApiModelProperty(required = true, value = "started_by")
    private String started_by;
    
    @ApiModelProperty(required = true, value = "num_groups")
    private String num_groups;

    @ApiModelProperty(required = true, value = "total_count")
    private String total_count;
    
    public String getWorkspace_id() {
        return workspace_id;
    }

    public void setWorkspace_id(String workspace_id) {
        this.workspace_id = workspace_id;
    }

    public String getStarted_at() {
        return started_at;
    }

    public void setStarted_at(String started_at) {
        this.started_at = started_at;
    }

    public String getStarted_by() {
        return started_by;
    }

    public void setStarted_by(String started_by) {
        this.started_by = started_by;
    }

    public String getNum_groups() {
        return num_groups;
    }

    public void setNum_groups(String num_groups) {
        this.num_groups = num_groups;
    }

    public String getTotal_count() {
        return total_count;
    }

    public void setTotal_count(String total_count) {
        this.total_count = total_count;
    }
    

}
