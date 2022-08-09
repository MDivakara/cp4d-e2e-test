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
//import java.util.List;
//import java.util.Map;
//
//import org.apache.wink.json4j.JSONArray;
//import org.apache.wink.json4j.JSONException;
//import io.swagger.annotations.ApiModelProperty;
//
//public class AnomalyScoreInputRequestModel {
//
//	@ApiModelProperty(required = true, value = "List of input_rows.")
//	private List<Map<String, String>> input_rows;
//
//	public List<Map<String, String>> getInput_rows() {
//		return input_rows;
//	}
//
//	public void setInput_rows(List<Map<String, String>> input_rows) {
//		this.input_rows = input_rows;
//	}
//
//	public JSONArray inputRowsAsJSONArray() {
//		JSONArray resp = null;
//		try {
//			resp = new JSONArray(input_rows);
//		} catch (JSONException e) {
//			// TODO add error message
//		}
//		return resp;
//	}
//	
//}
