package cn.easyutil.easyapi.entity.auth;

import cn.easyutil.easyapi.datasource.annotations.Tfd;
import cn.easyutil.easyapi.datasource.annotations.Tne;
import cn.easyutil.easyapi.entity.common.BaseBean;

@Tne("easyapi_user_projects")
public class UserProject extends BaseBean {

    @Tfd("PROJECT_ID")
    private Long projectId;

    @Tfd("USER_ID")
    private Long userId;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
