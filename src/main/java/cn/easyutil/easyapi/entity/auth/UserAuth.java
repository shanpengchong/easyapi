package cn.easyutil.easyapi.entity.auth;

import cn.easyutil.easyapi.datasource.annotations.Tfd;
import cn.easyutil.easyapi.datasource.annotations.Tne;
import cn.easyutil.easyapi.entity.common.BaseBean;

@Tne("easyapi_user_auth")
public class UserAuth extends BaseBean {

    @Tfd("USER_ID")
    private Long userId;

    @Tfd("AUTH_CODE")
    private Integer authCode;

    @Tfd("PROJECT_ID")
    private Long projectId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getAuthCode() {
        return authCode;
    }

    public void setAuthCode(Integer authCode) {
        this.authCode = authCode;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
