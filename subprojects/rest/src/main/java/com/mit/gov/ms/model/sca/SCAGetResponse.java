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

@ApiModel(value = "SCAGetResponse")
public class SCAGetResponse {

    public SCAGetResponse(SCAGetMetadataResponse metadata, SCAGetEntityResponse entity) {
        super();
        this.metadata = metadata;
        this.entity = entity;
    }

    @ApiModelProperty(required = true, value = "metadata")
    private SCAGetMetadataResponse metadata;
    
    @ApiModelProperty(required = true, value = "entity")
    private SCAGetEntityResponse entity;

    public SCAGetMetadataResponse getMetadata() {
        return metadata;
    }

    public void setMetadata(SCAGetMetadataResponse metadata) {
        this.metadata = metadata;
    }

    public SCAGetEntityResponse getEntity() {
        return entity;
    }

    public void setEntity(SCAGetEntityResponse entity) {
        this.entity = entity;
    }

}
