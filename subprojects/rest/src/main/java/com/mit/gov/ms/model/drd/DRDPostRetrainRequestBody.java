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

@ApiModel(value = "DRDPostRetrainRequestBody")

public class DRDPostRetrainRequestBody {
	public DRDPostRetrainRequestBody(DRDPostRetrainDataSetBody data_asset) {
		super();
		this.data_asset = data_asset;
	}

	@ApiModelProperty(required = true, value = "data_set", name = "data_asset")
	private DRDPostRetrainDataSetBody data_asset;

	public DRDPostRetrainDataSetBody getData_asset() {
		return data_asset;
	}

	public void setData_asset(DRDPostRetrainDataSetBody data_asset) {
		this.data_asset = data_asset;
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject reqBody = new JSONObject();
		if(data_asset != null) {
			reqBody.put(Constants.DATA_SET,data_asset.toJSON());
		} else {
			reqBody.put(Constants.DATA_SET,new JSONObject());
		}
		return reqBody;
	}
}
