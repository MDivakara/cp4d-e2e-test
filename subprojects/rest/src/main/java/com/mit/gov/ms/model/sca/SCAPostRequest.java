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

@ApiModel(value = "SCAPostRequest")
public class SCAPostRequest {

    public SCAPostRequest(SCAPostRequestRefColumn reference_column) {
        super();
        this.reference_column = reference_column;
    }

    @ApiModelProperty(required = true, value = "reference_column")
    private SCAPostRequestRefColumn reference_column;

    public SCAPostRequestRefColumn getReference_column() {
        return reference_column;
    }

    public void setReference_column(SCAPostRequestRefColumn reference_column) {
        this.reference_column = reference_column;
    }

    public JSONObject toJSON() throws JSONException {

        JSONObject createSCCJSONResp = new JSONObject();
        createSCCJSONResp.put(Constants.REFERENCE_COLUMN, reference_column.toJSON());
        return createSCCJSONResp;

    }

}
