package cn.easyutil.easyapi.interview.dto;

import java.io.Serializable;

/**
 * 修改入参或返回
 */
public class UpdateReqOrResParamDto {
	//接口入参文件名
	private String requestParamName;
	//接口返回文件名
	private String responseParamName;
	//文件内容
	private String json;

	private Long projectId;
	public String getRequestParamName() {
		return requestParamName;
	}
	public void setRequestParamName(String requestParamName) {
		this.requestParamName = requestParamName;
	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	public String getResponseParamName() {
		return responseParamName;
	}
	public void setResponseParamName(String responseParamName) {
		this.responseParamName = responseParamName;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
}
