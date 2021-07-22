package cn.easyutil.easyapi.entity.auth;

import java.io.Serializable;
import java.util.List;

/**
 * 用户权限bean
 */
public class AuthResultParseBean implements Serializable {
	private static final long serialVersionUID = 1L;
	//权限到期时间
	private Long authEndTime;
	//权限集合
	private List<Integer> auths;
	public Long getAuthEndTime() {
		return authEndTime;
	}
	public void setAuthEndTime(Long authEndTime) {
		this.authEndTime = authEndTime;
	}
	public List<Integer> getAuths() {
		return auths;
	}
	public void setAuths(List<Integer> auths) {
		this.auths = auths;
	}
	
	
	
}
