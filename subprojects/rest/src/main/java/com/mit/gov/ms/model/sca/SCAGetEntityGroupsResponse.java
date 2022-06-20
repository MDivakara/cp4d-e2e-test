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

//get response
@ApiModel(value = "SCAGetEntityGroupsResponse")
public class SCAGetEntityGroupsResponse {
    
    public SCAGetEntityGroupsResponse(String group_id, String num_of_cols, String num_of_datasets,
            String max_similarity, String min_similarity, String state,
            SCAGetEntityGroupsReferenceColumnsResponse reference_column) {
        super();
        this.group_id = group_id;
        this.num_of_cols = num_of_cols;
        this.num_of_datasets = num_of_datasets;
        this.max_similarity = max_similarity;
        this.min_similarity = min_similarity;
        this.state = state;
        this.reference_column = reference_column;
    }

    @ApiModelProperty(required = true, value = "group_id")
    private String group_id;
    
    @ApiModelProperty(required = true, value = "num_of_cols")
    private String num_of_cols;
    
    @ApiModelProperty(required = true, value = "num_of_datasets")
    private String num_of_datasets;
    
    @ApiModelProperty(required = true, value = "max_similarity")
    private String max_similarity;
    
    @ApiModelProperty(required = true, value = "min_similarity")
    private String min_similarity;
    
    @ApiModelProperty(required = true, value = "state")
    private String state;
    
    @ApiModelProperty(required = true, value = "reference_column")
    private SCAGetEntityGroupsReferenceColumnsResponse reference_column;

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getNum_of_cols() {
        return num_of_cols;
    }

    public void setNum_of_cols(String num_of_cols) {
        this.num_of_cols = num_of_cols;
    }

    public String getNum_of_datasets() {
        return num_of_datasets;
    }

    public void setNum_of_datasets(String num_of_datasets) {
        this.num_of_datasets = num_of_datasets;
    }

    public String getMax_similarity() {
        return max_similarity;
    }

    public void setMax_similarity(String max_similarity) {
        this.max_similarity = max_similarity;
    }

    public String getMin_similarity() {
        return min_similarity;
    }

    public void setMin_similarity(String min_similarity) {
        this.min_similarity = min_similarity;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public SCAGetEntityGroupsReferenceColumnsResponse getReference_column() {
        return reference_column;
    }

    public void setReference_column(SCAGetEntityGroupsReferenceColumnsResponse reference_column) {
        this.reference_column = reference_column;
    }

}
