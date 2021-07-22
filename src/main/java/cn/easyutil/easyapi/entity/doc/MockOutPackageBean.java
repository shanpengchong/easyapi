package cn.easyutil.easyapi.entity.doc;

import cn.easyutil.easyapi.datasource.annotations.Tfd;
import cn.easyutil.easyapi.datasource.annotations.Tie;
import cn.easyutil.easyapi.datasource.annotations.Tne;
import cn.easyutil.easyapi.entity.common.BaseBean;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 返回数据外包装
 * @author spc
 *
 */
@Tne("easyapi_project_out_package")
public class MockOutPackageBean extends BaseBean {

	@Tfd("PROJECT_ID")
	private Long projectId;

	@Tfd("KEY")
	@ApiModelProperty("包装的key")
	private String key;

	@Tie
	@ApiModelProperty("数据类型  0-数字  1-字符串")
	private Integer type;

	@Tfd("VAL")
	private String oldVal;

	@ApiModelProperty("包装的val")
	private Object val;

	@Tfd("TYPE")
	@ApiModelProperty("0-不是返回数据  1-是返回数据")
	private Integer dataStatus;

	@Tfd("SORT")
	private Integer sort;

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Object getVal() {
		return val;
	}
	public void setVal(Object val) {
		this.val = val;
	}
	public Integer getDataStatus() {
		return dataStatus;
	}
	public void setDataStatus(Integer dataStatus) {
		this.dataStatus = dataStatus;
	}

	public String getOldVal() {
		return oldVal;
	}

	public void setOldVal(String oldVal) {
		this.oldVal = oldVal;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
}
