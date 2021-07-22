package cn.easyutil.easyapi.interview.entity;

import java.io.Serializable;

/**
 * 同步文件
 */
public class SyncFileBean implements Serializable {
	private static final long serialVersionUID = 1L;
	//文件名称
	private String fileName;
	//目标地址
	private String address;
	//0-接口  1-请求参数  2-返回参数  3-mock
	private Integer type;
	//同步得文件内容
	private String json;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	
	
}
