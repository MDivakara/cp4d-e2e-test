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

@ApiModel(value = "SCAPostRequestRefColumn")
public class SCAPostRequestRefColumn {
    
    public SCAPostRequestRefColumn(String asset_id, String column_id, String column_name) {
        super();
        this.asset_id = asset_id;
        this.column_id = column_id;
        this.column_name = column_name;
    }

    @ApiModelProperty(required = true, value = "asset_id")
    private String asset_id;
    
    @ApiModelProperty(required = true, value = "column_id")
    private String column_id;
    
    @ApiModelProperty(required = true, value = "column_name")
    private String column_name;

    public String getAsset_id() {
        return asset_id;
    }

    public void setAsset_id(String asset_id) {
        this.asset_id = asset_id;
    }

    public String getColumn_id() {
        return column_id;
    }

    public void setColumn_id(String column_id) {
        this.column_id = column_id;
    }

    public String getColumn_name() {
        return column_name;
    }

    public void setColumn_name(String column_name) {
        this.column_name = column_name;
    }

    public JSONObject toJSON() throws JSONException {

        JSONObject createSCAJSONResp = new JSONObject();
        createSCAJSONResp.put(Constants.ASSET_ID, asset_id);
        createSCAJSONResp.put(Constants.COLUMN_ID, column_id);
        createSCAJSONResp.put(Constants.COLUMN_NAME, column_name);
        return createSCAJSONResp;

    }

}
