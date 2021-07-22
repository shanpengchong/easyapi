package cn.easyutil.easyapi.interview.dto;

import cn.easyutil.easyapi.entity.doc.ControllerBean;
import cn.easyutil.easyapi.entity.doc.InfoBean;
import cn.easyutil.easyapi.entity.doc.MockOutPackageBean;

import java.io.Serializable;
import java.util.List;

/**
 * 基础文档同步到远程
 */
public class SyncRemoteAll implements Serializable {

    private InfoBean project;
    private List<ControllerBean> controllerBeans;
    private List<MockOutPackageBean> outPackageBeans;
    private String username;
    private String password;

    public InfoBean getProject() {
        return project;
    }

    public void setProject(InfoBean project) {
        this.project = project;
    }

    public List<ControllerBean> getControllerBeans() {
        return controllerBeans;
    }

    public void setControllerBeans(List<ControllerBean> controllerBeans) {
        this.controllerBeans = controllerBeans;
    }

    public String getUsername() {
        return username;
    }

    public List<MockOutPackageBean> getOutPackageBeans() {
        return outPackageBeans;
    }

    public void setOutPackageBeans(List<MockOutPackageBean> outPackageBeans) {
        this.outPackageBeans = outPackageBeans;
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
}
