///* 
// * Author : Shaik Magdhum Nawaz
// * Email : shaik.nawaz@mastechinfotrellis.com
// * 
// * Mastech InfoTrellis Confidential
// * Copyright InfoTrellis India Pvt. Ltd. 2022
// * The source code for this program is not published. 
// */
//
//package com.mit.gov.ms.model;
//
//import org.apache.wink.json4j.JSONException;
//import org.apache.wink.json4j.JSONObject;
//
//import io.swagger.annotations.ApiModelProperty;
//
//public class EnableDisableResp {
//	public String getEnable() {
//		return enable;
//	}
//
//	public void setEnable(String enable) {
//		this.enable = enable;
//	}
//
//	@ApiModelProperty(required = true, example = "true", value = "enable")
//	private String enable;
//	
//	public JSONObject toJSON() throws JSONException {
//
//		JSONObject sccEnableRespJSON = new JSONObject();
//		sccEnableRespJSON.put("enable", enable);
//
//		return sccEnableRespJSON;
//
//	}
//}
