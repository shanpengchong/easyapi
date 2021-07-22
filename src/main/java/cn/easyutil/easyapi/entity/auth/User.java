package cn.easyutil.easyapi.entity.auth;

import cn.easyutil.easyapi.datasource.annotations.Tfd;
import cn.easyutil.easyapi.datasource.annotations.Tie;
import cn.easyutil.easyapi.datasource.annotations.Tne;
import cn.easyutil.easyapi.entity.common.BaseBean;

import java.util.List;

/**
 * 用户
 */
@Tne("easyapi_user")
public class User extends BaseBean {
    @Tfd("USERNAME")
    private String username;
    @Tfd("PASSWORD")
    private String password;
    //是否超级管理员
    @Tfd("SUPER_ADMIN")
    private Integer superAdmin;
    @Tfd("REMARK")
    private String remark;

    @Tfd("LAST_LOGIN_TIME")
    private Long lastLoginTime;

    @Tfd("DISABLE")
    private Integer disable;

    @Tie
    private List<Integer> auths;

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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<Integer> getAuths() {
        return auths;
    }

    public void setAuths(List<Integer> auths) {
        this.auths = auths;
    }

    public Integer getSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(Integer superAdmin) {
        this.superAdmin = superAdmin;
    }

    public Long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Integer getDisable() {
        return disable;
    }

    public void setDisable(Integer disable) {
        this.disable = disable;
    }
}
