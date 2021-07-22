package cn.easyutil.easyapi.entity.common;


import cn.easyutil.easyapi.datasource.annotations.Tfd;
import cn.easyutil.easyapi.datasource.annotations.Tid;
import cn.easyutil.easyapi.datasource.annotations.Tie;

import java.io.Serializable;

/**
 * 所有文件存储类的父类
 */
public class BaseBean implements Serializable{
	private static final long serialVersionUID = 1L;
	@Tid
	@Tfd("ID")
	protected Long id;

	@Tfd("CREATE_TIME")
 	protected Long createTime;

	@Tfd("UPDATE_TIME")
	protected Long updateTime;

	@Tfd("DELETED")
	protected Integer deleted;

 	@Tie
	protected String json;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getDeleted() {
		return deleted;
	}

	public void setDeleted(Integer deleted) {
		this.deleted = deleted;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}
}
