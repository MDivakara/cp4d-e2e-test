/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.model;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.mit.gov.ms.common.Constants;

import io.swagger.annotations.ApiModelProperty;

public class CustomClassCreateResp {

	@ApiModelProperty(required = true, example = "demoClassCode", value = "class_code")
	private String class_code;

	@ApiModelProperty(required = true, example = "demoClassName", value = "class_name")
	private String class_name;

	@ApiModelProperty(required = true, example = "accept", value = "action")
	private String action;

	@ApiModelProperty(required = true, example = "da1d0152-c092-4e59-8da0-ca3b47766231", value = "group_id")
	private String group_id;

	@ApiModelProperty(required = false, example = "New Class for Suggested Data Class Demo", value = "class_description")
	private String class_description;

	public String getClass_code() {
		return class_code;
	}

	public void setClass_code(String class_code) {
		this.class_code = class_code;
	}

	public String getClass_name() {
		return class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	public String getClass_description() {
		return class_description;
	}

	public void setClass_description(String class_description) {
		this.class_description = class_description;
	}

	public JSONObject toJSON() throws JSONException {

		JSONObject createSCCJSONResp = new JSONObject();
		createSCCJSONResp.put(Constants.CLASS_CODE, class_code);
		createSCCJSONResp.put(Constants.CLASS_NAME, class_name);
		createSCCJSONResp.put(Constants.ACTION, action);
		createSCCJSONResp.put(Constants.GROUP_ID, group_id);
		createSCCJSONResp.put(Constants.CLASS_DESCRIPTION, class_description);

		return createSCCJSONResp;

	}
}
