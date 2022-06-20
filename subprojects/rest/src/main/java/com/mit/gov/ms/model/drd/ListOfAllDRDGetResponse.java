/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.model.drd;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ListOfAllDRDGetResponse")
public class ListOfAllDRDGetResponse {
	public ListOfAllDRDGetResponse(List<DRDGetTaskInfoResponse> resources, Integer offset, Integer limit) {
		super();
		this.resources = resources;
		this.offset = offset;
		this.limit = limit;
	}

	@ApiModelProperty(required = true, value = "resources")
	private List<DRDGetTaskInfoResponse> resources;

	@ApiModelProperty(required = true, value = "offset", example = "0")
	private Integer offset;

	@ApiModelProperty(required = true, value = "limit", example = "50")
	private Integer limit;

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public List<DRDGetTaskInfoResponse> getResources() {
		return resources;
	}

	public void setResources(List<DRDGetTaskInfoResponse> resources) {
		this.resources = resources;
	}
}
