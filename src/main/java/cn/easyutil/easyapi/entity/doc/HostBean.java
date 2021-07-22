package cn.easyutil.easyapi.entity.doc;

import cn.easyutil.easyapi.datasource.annotations.Tfd;
import cn.easyutil.easyapi.datasource.annotations.Tne;
import cn.easyutil.easyapi.entity.common.BaseBean;

import java.io.Serializable;

/**
 * 环境配置bean
 */
@Tne("easyapi_project_host")
public class HostBean extends BaseBean {

	//环境名称
	@Tfd("NAME")
	private String name;
	//环境地址(address+port)
	@Tfd("HOST")
	private String host;
	//环境ip或域名
	@Tfd("ADDRESS")
	private String address;
	//环境端口
	@Tfd("PORT")
	private Integer port;

	@Tfd("PROJECT_ID")
	private Long projectId;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
}
