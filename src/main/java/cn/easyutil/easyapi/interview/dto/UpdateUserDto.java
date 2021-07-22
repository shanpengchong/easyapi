package cn.easyutil.easyapi.interview.dto;

import cn.easyutil.easyapi.entity.auth.User;

public class UpdateUserDto extends User {

    private Long projectId;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
