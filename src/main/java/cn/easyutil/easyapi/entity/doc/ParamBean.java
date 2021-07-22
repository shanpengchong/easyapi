package cn.easyutil.easyapi.entity.doc;

import cn.easyutil.easyapi.datasource.annotations.Tfd;
import cn.easyutil.easyapi.datasource.annotations.Tie;
import cn.easyutil.easyapi.datasource.annotations.Tne;
import cn.easyutil.easyapi.entity.common.BaseBean;
import io.swagger.annotations.ApiModelProperty;

import java.util.UUID;

@Tne("easyapi_param")
public class ParamBean extends BaseBean {

	@Tfd("PROJECT_ID")
 	@ApiModelProperty("项目id") 
 	private Long project_id;

	@Tfd("INTERFACE_ID")
	@ApiModelProperty("接口id")
	private Long interfaceId;

	@Tfd("INTERFACE_PARAM_TYPE")
	@ApiModelProperty("接口参数类型:0-请求参数  1-响应参数")
	private Integer interface_param_type;

	@Tfd("JAVA_NAME")
 	@ApiModelProperty("包名+类名") 
 	private String java_name;

	@Tfd("NAME")
 	@ApiModelProperty("参数名称") 
 	private String name;

	@Tfd("REMARK")
 	@ApiModelProperty("参数说明") 
 	private String remark;

	@Tfd("TYPE")
 	@ApiModelProperty("参数类型 0-字符串") 
 	private Integer type;

	@Tfd("MOCK_VALUE")
 	@ApiModelProperty("默认值") 
 	private String mockValue;

	@Tfd("OLD_MOCK_VALUE")
 	@ApiModelProperty("原始未被替换的mockvalue的值") 
 	private String oldMockValue;

	@Tfd("REQUIRED")
 	@ApiModelProperty("是否必填 0-不必填  1-必填") 
 	private Integer required;

	@Tfd("SHOW")
 	@ApiModelProperty("是否显示  0-不显示  1-显示") 
 	private Integer show;

	@Tfd("SORT")
	private Integer sort;

 	@Tie
 	private Object children;

 	@Tie
 	private String uuid;
 	
 	public ParamBean(){
 		this.uuid = UUID.randomUUID().toString().replace("-", "");
 	}
 	

	public String getUuid() {
		this.uuid = UUID.randomUUID().toString().replace("-", "");
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = UUID.randomUUID().toString().replace("-", "");
	}

	public Long getProject_id() {
		return project_id;
	}

	public String getJava_name() {
		return java_name;
	}

	public String getName() {
		return name;
	}

	public String getRemark() {
		return remark;
	}

	public Integer getType() {
		return type;
	}

	public Integer getRequired() {
		return required;
	}

	public Integer getShow() {
		return show;
	}

	public void setProject_id(Long project_id) {
		this.project_id = project_id;
	}

	public void setJava_name(String java_name) {
		this.java_name = java_name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	
	public String getMockValue() {
		return mockValue;
	}

	public void setMockValue(String mockValue) {
		this.mockValue = mockValue;
	}


	public void setRequired(Integer required) {
		this.required = required;
	}

	public void setShow(Integer show) {
		this.show = show;
	}

	public Object getChildren() {
		return children;
	}

	public void setChildren(Object children) {
		this.children = children;
	}


	public Long getInterfaceId() {
		return interfaceId;
	}


	public void setInterfaceId(Long interfaceId) {
		this.interfaceId = interfaceId;
	}


	public String getOldMockValue() {
		return oldMockValue;
	}


	public void setOldMockValue(String oldMockValue) {
		this.oldMockValue = oldMockValue;
	}

	public Integer getInterface_param_type() {
		return interface_param_type;
	}

	public void setInterface_param_type(Integer interface_param_type) {
		this.interface_param_type = interface_param_type;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
}