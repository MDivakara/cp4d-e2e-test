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

@ApiModel(value = "SCAGetEntityGroupsReferenceColumnsResponse")
public class SCAGetEntityGroupsReferenceColumnsResponse {
    
    public SCAGetEntityGroupsReferenceColumnsResponse(String column_name, String column_rid,
            List<String> sample_values) {
        super();
        this.column_name = column_name;
        this.column_rid = column_rid;
        this.sample_values = sample_values;
    }

    @ApiModelProperty(required = true, value = "column_name")
    private String column_name;
    
    @ApiModelProperty(required = true, value = "column_rid")
    private String column_rid;
    
    @ApiModelProperty(required = true, value = "sample_values")
    private List<String> sample_values;

    public String getColumn_name() {
        return column_name;
    }

    public void setColumn_name(String column_name) {
        this.column_name = column_name;
    }

    public String getColumn_rid() {
        return column_rid;
    }

    public void setColumn_rid(String column_rid) {
        this.column_rid = column_rid;
    }

    public List<String> getSample_values() {
        return sample_values;
    }

    public void setSample_values(List<String> sample_values) {
        this.sample_values = sample_values;
    }

}
