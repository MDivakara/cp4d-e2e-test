/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.model.drd;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.mit.gov.ms.common.Constants;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "DRDPostRequestBody")

public class DRDPostRequestBody {

	public DRDPostRequestBody(String workspace_rid, String drd_name, String drd_description,
            DRDPostReqBodyDataSet data_asset) {
        super();
        this.workspace_rid = workspace_rid;
        this.drd_name = drd_name;
        this.drd_description = drd_description;
        this.data_asset = data_asset;
    }

    @ApiModelProperty(required = true, value = "workspace_rid")
	private String workspace_rid;

	@ApiModelProperty(required = true, value = "drd_name")
	private String drd_name;

	@ApiModelProperty(required = false, value = "drd_description")
	private String drd_description;

	@ApiModelProperty(required = true, value = "data_set",  name = "data_asset")
	private DRDPostReqBodyDataSet data_asset;

	public String getDrd_description() {
		return drd_description;
	}

	public void setDrd_description(String drd_description) {
		this.drd_description = drd_description;
	}

	public String getDrd_name() {
		return drd_name;
	}

	public void setDrd_name(String drd_name) {
		this.drd_name = drd_name;
	}

	public DRDPostReqBodyDataSet getData_asset() {
		return data_asset;
	}

	public void setData_asset(DRDPostReqBodyDataSet data_asset) {
		this.data_asset = data_asset;
	}
	
	public String getWorkspace_rid() {
        return workspace_rid;
    }

    public void setWorkspace_rid(String workspace_rid) {
        this.workspace_rid = workspace_rid;
    }
    
	public JSONObject toJSON() throws JSONException {
		JSONObject bodyJSON = new JSONObject();
		bodyJSON.put(Constants.DRD_NAME, drd_name);
		bodyJSON.put(Constants.DRD_DESCRIPTION, drd_description);
		bodyJSON.put(Constants.WORKSPACE_RID, workspace_rid);
		bodyJSON.put(Constants.DATA_SET, data_asset.toJSON() );
		return bodyJSON;
	}
}