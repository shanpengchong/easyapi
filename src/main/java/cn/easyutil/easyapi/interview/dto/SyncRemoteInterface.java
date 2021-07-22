package cn.easyutil.easyapi.interview.dto;

import cn.easyutil.easyapi.entity.doc.InterfaceBean;
import cn.easyutil.easyapi.entity.doc.MockBean;
import cn.easyutil.easyapi.entity.doc.ParamBean;

import java.io.Serializable;

/**
 * 接口同步到远程的请求参数
 */
public class SyncRemoteInterface implements Serializable {

    private String projectName;
    //控制器名称
    private String controllerName;
    //接口数据
    private InterfaceBean interfaceBean;
    //接口参数
    private ParamBean requestParam;
    //接口响应
    private ParamBean responseParam;
    //接口响应mock
    private MockBean responseMock;

    private String username;
    private String password;

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

    public InterfaceBean getInterfaceBean() {
        return interfaceBean;
    }

    public void setInterfaceBean(InterfaceBean interfaceBean) {
        this.interfaceBean = interfaceBean;
    }

    public ParamBean getRequestParam() {
        return requestParam;
    }

    public void setRequestParam(ParamBean requestParam) {
        this.requestParam = requestParam;
    }

    public ParamBean getResponseParam() {
        return responseParam;
    }

    public void setResponseParam(ParamBean responseParam) {
        this.responseParam = responseParam;
    }

    public MockBean getResponseMock() {
        return responseMock;
    }

    public void setResponseMock(MockBean responseMock) {
        this.responseMock = responseMock;
    }

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

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
