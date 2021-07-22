package cn.easyutil.easyapi.interview.entity;

import cn.easyutil.easyapi.entity.auth.User;
import cn.easyutil.easyapi.entity.auth.UserAuth;
import cn.easyutil.easyapi.entity.auth.UserProject;

import java.util.List;

public class SessionUser {

    private User user;
    private List<UserAuth> auths;
    private List<UserProject> projects;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<UserAuth> getAuths() {
        return auths;
    }

    public void setAuths(List<UserAuth> auths) {
        this.auths = auths;
    }

    public List<UserProject> getProjects() {
        return projects;
    }

    public void setProjects(List<UserProject> projects) {
        this.projects = projects;
    }
}
