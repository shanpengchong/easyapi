package cn.easyutil.easyapi.entity.doc;

import cn.easyutil.easyapi.datasource.annotations.Tfd;
import cn.easyutil.easyapi.datasource.annotations.Tie;
import cn.easyutil.easyapi.datasource.annotations.Tne;
import cn.easyutil.easyapi.entity.common.BaseBean;
import io.swagger.annotations.ApiModelProperty;

@Tne("easyapi_interface")
public class InterfaceBean extends BaseBean {

	@Tfd("PROJECT_ID")
 	@ApiModelProperty("项目id") 
 	private Long project_id;

	@Tfd("CONTROLLER_ID")
 	@ApiModelProperty("控制器id") 
 	private Long controller_id;

	@Tfd("CONTROLLER_NAME")
 	private String controller_name;

	@Tfd("JAVA_NAME")
 	@ApiModelProperty("接口方法名") 
 	private String javaName;

	@Tfd("TITLE")
 	@ApiModelProperty("接口标题") 
 	private String title;

	@Tfd("REMARK")
 	@ApiModelProperty("接口说明") 
 	private String remark;

	@Tfd("REQUEST_PARAM_REMARK")
 	@ApiModelProperty("请求参数说明") 
 	private String request_param_remark;

	@Tfd("RESPONSE_PARAM_REMARK")
 	@ApiModelProperty("响应参数说明") 
 	private String response_param_remark;

	@Tfd("REQUEST_URL")
 	@ApiModelProperty("请求地址") 
 	private String request_url;

	@Tfd("REQUEST_METHOD")
 	@ApiModelProperty("请求方式 0-全部  1-get  2-post ") 
 	private Integer request_method;

	@Tfd("REQUEST_TYPE")
 	@ApiModelProperty("请求体类型 0-form  1-json体  2-文件上传 3-XML 4-RAW")
 	private Integer request_type;

	@Tfd("REQUEST_PARAM_NAME")
 	private String request_param_name;

	@Tfd("RESPONSE_PARAM_NAME")
 	private String response_param_name;

	@Tie
 	@ApiModelProperty("返回值是否是集合或数组类型") 
 	private int response_is_array;

	@Tfd("SORT")
 	@ApiModelProperty("排序值") 
 	private Integer sort = 0;
 	
	public Long getProject_id() {
		return project_id;
	}

	public Long getController_id() {
		return controller_id;
	}

	public String getTitle() {
		return title;
	}

	public String getRemark() {
		return remark;
	}

	public String getRequest_url() {
		return request_url;
	}

	public Integer getRequest_method() {
		return request_method;
	}

	public Integer getRequest_type() {
		return request_type;
	}

	public void setProject_id(Long project_id) {
		this.project_id = project_id;
	}

	public void setController_id(Long controller_id) {
		this.controller_id = controller_id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setRequest_url(String request_url) {
		this.request_url = request_url;
	}

	public void setRequest_method(Integer request_method) {
		this.request_method = request_method;
	}

	public void setRequest_type(Integer request_type) {
		this.request_type = request_type;
	}

	public String getController_name() {
		return controller_name;
	}

	public String getRequest_param_name() {
		return request_param_name;
	}

	public String getResponse_param_name() {
		return response_param_name;
	}

	public void setController_name(String controller_name) {
		this.controller_name = controller_name;
	}

	public void setRequest_param_name(String request_param_name) {
		this.request_param_name = request_param_name;
	}

	public void setResponse_param_name(String response_param_name) {
		this.response_param_name = response_param_name;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getJavaName() {
		return javaName;
	}

	public void setJavaName(String javaName) {
		this.javaName = javaName;
	}

	public int getResponse_is_array() {
		return response_is_array;
	}

	public void setResponse_is_array(int response_is_array) {
		this.response_is_array = response_is_array;
	}

	public String getRequest_param_remark() {
		return request_param_remark;
	}

	public void setRequest_param_remark(String request_param_remark) {
		this.request_param_remark = request_param_remark;
	}

	public String getResponse_param_remark() {
		return response_param_remark;
	}

	public void setResponse_param_remark(String response_param_remark) {
		this.response_param_remark = response_param_remark;
	}

	
 	
 
}