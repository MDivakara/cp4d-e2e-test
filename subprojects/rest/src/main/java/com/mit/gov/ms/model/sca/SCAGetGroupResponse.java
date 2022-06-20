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

@ApiModel(value = "SCAGetGroupResponse")
public class SCAGetGroupResponse {
    
    public SCAGetGroupResponse(SCAGetGroupMetadataResponse metadata, SCAGetGroupResponseEntity entity) {
        super();
        this.metadata = metadata;
        this.entity = entity;
    }

    @ApiModelProperty(required = true, value = "metadata")
    private SCAGetGroupMetadataResponse metadata;
    
    @ApiModelProperty(required = true, value = "entity")
    private SCAGetGroupResponseEntity entity;

    public SCAGetGroupMetadataResponse getMetadata() {
        return metadata;
    }

    public void setMetadata(SCAGetGroupMetadataResponse metadata) {
        this.metadata = metadata;
    }

    public SCAGetGroupResponseEntity getEntity() {
        return entity;
    }

    public void setEntity(SCAGetGroupResponseEntity entity) {
        this.entity = entity;
    }

}
