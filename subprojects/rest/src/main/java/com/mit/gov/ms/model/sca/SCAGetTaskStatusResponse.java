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

@ApiModel(value = "SCAGetTaskStatusResponse")
public class SCAGetTaskStatusResponse {

    public SCAGetTaskStatusResponse(SCAGetTaskStatusMetadataResponse metadata, SCAGetTaskStatusEntityResponse entity) {
        super();
        this.metadata = metadata;
        this.entity = entity;
    }

    @ApiModelProperty(required = true, value = "metadata")
    private SCAGetTaskStatusMetadataResponse metadata;
    
    @ApiModelProperty(required = true, value = "entity")
    private SCAGetTaskStatusEntityResponse entity;

    public SCAGetTaskStatusMetadataResponse getMetadata() {
        return metadata;
    }

    public void setMetadata(SCAGetTaskStatusMetadataResponse metadata) {
        this.metadata = metadata;
    }

    public SCAGetTaskStatusEntityResponse getEntity() {
        return entity;
    }

    public void setEntity(SCAGetTaskStatusEntityResponse entity) {
        this.entity = entity;
    }
    
    
}
