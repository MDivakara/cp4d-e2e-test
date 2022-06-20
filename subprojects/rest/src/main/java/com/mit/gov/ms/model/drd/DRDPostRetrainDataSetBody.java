/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.model.drd;

import java.util.ArrayList;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.mit.gov.ms.common.Constants;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "DRDPostRetrainDataSetBody")

public class DRDPostRetrainDataSetBody {

	@ApiModelProperty(required = false, value = "username")
	private String username;

	@ApiModelProperty(required = false, value = "password")
	private String password;

	@ApiModelProperty(required = false, value = "host")
	private String host;

	@ApiModelProperty(required = false, value = "port")
	private String port;

	@ApiModelProperty(required = false, value = "asset_id")
	private String asset_id;

	@ApiModelProperty(required = false, value = "columns")
	private ArrayList<String> columns;

	public DRDPostRetrainDataSetBody(String host, String port, String username, String password, String asset_id, ArrayList<String> columns) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.asset_id = asset_id;
		this.columns = columns;
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ArrayList<String> getColumns() {
		return columns;
	}

	public void setColumns(ArrayList<String> columns) {
		this.columns = columns;
	}

	public String getAsset_id() {
		return asset_id;
	}

	public void setAsset_id(String asset_id) {
		this.asset_id = asset_id;
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject dataSet = new JSONObject();
		dataSet.put(Constants.ASSET_ID, asset_id);
		dataSet.put(Constants.HOST, host);
		dataSet.put(Constants.PORT, port);
		dataSet.put(Constants.USERNAME, username);
		dataSet.put(Constants.PASSWORD, password);
		if(columns == null) columns = new ArrayList<String>();
		dataSet.put(Constants.COLUMNS, new JSONArray(columns));
		return dataSet;
	}
}
