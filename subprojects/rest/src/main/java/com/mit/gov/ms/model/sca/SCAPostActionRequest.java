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

@ApiModel(value = "SCAPostActionRequest")
public class SCAPostActionRequest {

    public SCAPostActionRequest(String action, String class_name, String description, double confidence_threshold) {
        super();
        this.action = action;
        this.class_name = class_name;
        this.description = description;
        this.confidence_threshold = confidence_threshold;
    }

    @ApiModelProperty(required = true, value = "action", example = "accept/reject/restore")
    String action;
    
    @ApiModelProperty(required = false, value = "class_name")
    String class_name;
    
    @ApiModelProperty(required = false, value = "description")
    String description;
    
    @ApiModelProperty(required = false, value = "confidence_threshold")
    double confidence_threshold;

    public JSONObject toJSON() throws JSONException {
        JSONObject groupAction = new JSONObject();
        groupAction.put(Constants.ACTION, action);
        groupAction.put(Constants.CLASS_NAME, class_name);
        groupAction.put(Constants.CLASS_DESCRIPTION, description);
        groupAction.put(Constants.CONFIDENCE, confidence_threshold);
        return groupAction;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getConfidence_threshold() {
        return confidence_threshold;
    }

    public void setConfidence_threshold(double confidence_threshold) {
        this.confidence_threshold = confidence_threshold;
    }

}
