/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.model.sca;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "SCAGetEntityResponse")
public class SCAGetEntityResponse {
    
    public SCAGetEntityResponse(List<SCAGetEntityGroupsResponse> groups) {
        super();
        this.groups = groups;
    }

    @ApiModelProperty(required = true, value = "groups")
    private List<SCAGetEntityGroupsResponse> groups;

    public List<SCAGetEntityGroupsResponse> getGroups() {
        return groups;
    }

    public void setGroups(List<SCAGetEntityGroupsResponse> groups) {
        this.groups = groups;
    }

}
