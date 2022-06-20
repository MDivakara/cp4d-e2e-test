/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.model.sca;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.mit.gov.ms.common.Constants;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "SCACreateDataClassRequestBody")
public class SCACreateDataClassRequestBody {
    
    public SCACreateDataClassRequestBody(String class_name, String class_description, String reference_column,
            String workspace_name) {
        super();
        this.class_name = class_name;
        this.class_description = class_description;
        this.reference_column = reference_column;
        this.workspace_name = workspace_name;
    }

    @ApiModelProperty(required = true, value = "class_name")
    private String class_name;
    
    @ApiModelProperty(required = true, value = "class_description")
    private String class_description;
    
    @ApiModelProperty(required = true, value = "reference_column")
    private String reference_column;
    
    @ApiModelProperty(required = true, value = "workspace_name")
    private String workspace_name;

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getClass_description() {
        return class_description;
    }

    public void setClass_description(String class_description) {
        this.class_description = class_description;
    }

    public String getReference_column() {
        return reference_column;
    }

    public void setReference_column(String reference_column) {
        this.reference_column = reference_column;
    }

    public String getWorkspace_name() {
        return workspace_name;
    }

    public void setWorkspace_name(String workspace_name) {
        this.workspace_name = workspace_name;
    }
    
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        
        jsonObject.put(Constants.CLASS_NAME, class_name);
        jsonObject.put(Constants.CLASS_DESCRIPTION, class_description);
        jsonObject.put(Constants.REFERENCE_COLUMN, reference_column);
        jsonObject.put(Constants.WORKSPACE_NAME, workspace_name);
        return jsonObject;
        
    }

}
