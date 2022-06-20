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

@ApiModel(value = "SCAGetEntityGroupsColumnsArrayResponse")
public class SCAGetEntityGroupsColumnsArrayResponse {
    
    public SCAGetEntityGroupsColumnsArrayResponse(String column_description, String column_name, String column_rid,
            String database_name, String host_name, String inferred_data_class, String max_value, String min_value,
            String mean_value, String num_of_distinct_values, String schema_name, String similarity,
            String standard_deviation, String table_name, String table_rid, String total_num_of_values,
            String workspace_name, String location, String last_import, List<String> sample_values) {
        super();
        this.column_description = column_description;
        this.column_name = column_name;
        this.column_rid = column_rid;
        this.database_name = database_name;
        this.host_name = host_name;
        this.inferred_data_class = inferred_data_class;
        this.max_value = max_value;
        this.min_value = min_value;
        this.mean_value = mean_value;
        this.num_of_distinct_values = num_of_distinct_values;
        this.schema_name = schema_name;
        this.similarity = similarity;
        this.standard_deviation = standard_deviation;
        this.table_name = table_name;
        this.table_rid = table_rid;
        this.total_num_of_values = total_num_of_values;
        this.workspace_name = workspace_name;
        this.location = location;
        this.last_import = last_import;
        this.sample_values = sample_values;
    }

    @ApiModelProperty(required = true, value = "column_description")
    private String column_description;
    
    @ApiModelProperty(required = true, value = "column_name")
    private String column_name;
    
    @ApiModelProperty(required = true, value = "column_rid")
    private String column_rid;
    
    @ApiModelProperty(required = true, value = "database_name")
    private String database_name;
    
    @ApiModelProperty(required = true, value = "host_name")
    private String host_name;
    
    @ApiModelProperty(required = true, value = "inferred_data_class")
    private String inferred_data_class;
    
    @ApiModelProperty(required = true, value = "max_value")
    private String max_value;
    
    @ApiModelProperty(required = true, value = "min_value")
    private String min_value;
    
    @ApiModelProperty(required = true, value = "mean_value")
    private String mean_value;
    
    @ApiModelProperty(required = true, value = "num_of_distinct_values")
    private String num_of_distinct_values;
    
    @ApiModelProperty(required = true, value = "schema_name")
    private String schema_name;
    
    @ApiModelProperty(required = true, value = "similarity")
    private String similarity;
    
    @ApiModelProperty(required = true, value = "standard_deviation")
    private String standard_deviation;
    
    @ApiModelProperty(required = true, value = "table_name")
    private String table_name;

    @ApiModelProperty(required = true, value = "table_rid")
    private String table_rid;
    
    @ApiModelProperty(required = true, value = "total_num_of_values")
    private String total_num_of_values;
    
    @ApiModelProperty(required = true, value = "workspace_name")
    private String workspace_name;
    
    @ApiModelProperty(required = true, value = "location", example = "host_name>schema_name>database_name>table_name>column_name")
    private String location;
    
    @ApiModelProperty(required = true, value = "last_import")
    private String last_import;
    
    @ApiModelProperty(required = true, value = "sample_values")
    private List<String> sample_values;

    public String getColumn_description() {
        return column_description;
    }

    public void setColumn_description(String column_description) {
        this.column_description = column_description;
    }

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

    public String getDatabase_name() {
        return database_name;
    }

    public void setDatabase_name(String database_name) {
        this.database_name = database_name;
    }

    public String getHost_name() {
        return host_name;
    }

    public void setHost_name(String host_name) {
        this.host_name = host_name;
    }

    public String getInferred_data_class() {
        return inferred_data_class;
    }

    public void setInferred_data_class(String inferred_data_class) {
        this.inferred_data_class = inferred_data_class;
    }

    public String getMax_value() {
        return max_value;
    }

    public void setMax_value(String max_value) {
        this.max_value = max_value;
    }

    public String getMin_value() {
        return min_value;
    }

    public void setMin_value(String min_value) {
        this.min_value = min_value;
    }

    public String getMean_value() {
        return mean_value;
    }

    public void setMean_value(String mean_value) {
        this.mean_value = mean_value;
    }

    public String getNum_of_distinct_values() {
        return num_of_distinct_values;
    }

    public void setNum_of_distinct_values(String num_of_distinct_values) {
        this.num_of_distinct_values = num_of_distinct_values;
    }

    public String getSchema_name() {
        return schema_name;
    }

    public void setSchema_name(String schema_name) {
        this.schema_name = schema_name;
    }

    public String getSimilarity() {
        return similarity;
    }

    public void setSimilarity(String similarity) {
        this.similarity = similarity;
    }

    public String getStandard_deviation() {
        return standard_deviation;
    }

    public void setStandard_deviation(String standard_deviation) {
        this.standard_deviation = standard_deviation;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public String getTable_rid() {
        return table_rid;
    }

    public void setTable_rid(String table_rid) {
        this.table_rid = table_rid;
    }

    public String getTotal_num_of_values() {
        return total_num_of_values;
    }

    public void setTotal_num_of_values(String total_num_of_values) {
        this.total_num_of_values = total_num_of_values;
    }

    public String getWorkspace_name() {
        return workspace_name;
    }

    public void setWorkspace_name(String workspace_name) {
        this.workspace_name = workspace_name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLast_import() {
        return last_import;
    }

    public void setLast_import(String last_import) {
        this.last_import = last_import;
    }

    public List<String> getSample_values() {
        return sample_values;
    }

    public void setSample_values(List<String> sample_values) {
        this.sample_values = sample_values;
    }

}
