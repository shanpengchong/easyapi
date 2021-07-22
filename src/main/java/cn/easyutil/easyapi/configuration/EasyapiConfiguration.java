package cn.easyutil.easyapi.configuration;

import cn.easyutil.easyapi.filter.*;
import cn.easyutil.easyapi.filter.model.*;
import cn.easyutil.easyapi.util.ObjectUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;

@Component
@ConfigurationProperties(prefix = "easyapi")
public class EasyapiConfiguration {

    /** 是否开启接口文档*/
    private boolean enable = false;

    /** 是否重新扫描 */
    private boolean rescan = true;

    /** 是否删除旧的构建，默认不删除 */
    private boolean delOldApi = false;

    /** 是否删除用户信息,默认不删除*/
    private boolean delOldUsers = false;

    /** 项目启动后是否自动同步接口文档*/
    private boolean sync = false;

    /** 项目启动后自动同步的地址*/
    private String syncAddress = "";

    /** 项目启动后自动同步的端口*/
    private Integer syncPort = 0;

    /** 项目名字 */
    private String projectName = System.getProperty("user.dir").substring(System.getProperty("user.dir").lastIndexOf(File.separator)+1);

    /** 数据存放文件夹*/
    private String h2DataFolder = "";

    /** 扫描包名称*/
    private String scanPackage = "";

    /** 超级管理员用户名，默认root*/
    private String loginUserName = "admin";

    /** 超级管理员密码，默认root*/
    private String loginPassword = "admin";

    /** 数据库连接池，默认使用H2数据库*/
    private DataSource dataSource;

    /** 读取实体bean拦截器*/
    private Class<? extends ReadBeanApiFilter> readBeanApiFilter;

    /** 读取控制器拦截器*/
    private Class<? extends ReadControllerApiFilter> readControllerApiFilter;

    /** 读取接口拦截器*/
    private Class<? extends ReadInterfaceApiFilter> readInterfaceApiFilter;

    /** 读取请求参数拦截器*/
    private Class<? extends ReadRequestParamApiFilter> readRequestParamApiFilter;

    /** 读取返回参数拦截器*/
    private Class<? extends ReadResponseParamApiFilter> readResponseParamApiFilter;

    /** 读取实体bean拦截器*/
    private ReadBeanApiFilter beanApiFilter;

    /** 读取控制器拦截器*/
    private ReadControllerApiFilter controllerApiFilter;

    /** 读取接口拦截器*/
    private ReadInterfaceApiFilter interfaceApiFilter;

    /** 读取请求参数拦截器*/
    private ReadRequestParamApiFilter requestParamApiFilter;

    /** 读取返回参数拦截器*/
    private ReadResponseParamApiFilter responseParamApiFilter;


    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isRescan() {
        return rescan;
    }

    public void setRescan(boolean rescan) {
        this.rescan = rescan;
    }

    public boolean isDelOldApi() {
        return delOldApi;
    }

    public void setDelOldApi(boolean delOldApi) {
        this.delOldApi = delOldApi;
    }

    public boolean isDelOldUsers() {
        return delOldUsers;
    }

    public void setDelOldUsers(boolean delOldUsers) {
        this.delOldUsers = delOldUsers;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public String getSyncAddress() {
        return syncAddress;
    }

    public void setSyncAddress(String syncAddress) {
        this.syncAddress = syncAddress;
    }

    public Integer getSyncPort() {
        return syncPort;
    }

    public void setSyncPort(Integer syncPort) {
        this.syncPort = syncPort;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getScanPackage() {
        return scanPackage;
    }

    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
    }

    public String getLoginUserName() {
        return loginUserName;
    }

    public void setLoginUserName(String loginUserName) {
        this.loginUserName = loginUserName;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public String getH2DataFolder() {
        return h2DataFolder;
    }

    public void setH2DataFolder(String h2DataFolder) {
        this.h2DataFolder = h2DataFolder;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Class<? extends ReadBeanApiFilter> getReadBeanApiFilter() {
        return readBeanApiFilter;
    }

    public void setReadBeanApiFilter(Class<? extends ReadBeanApiFilter> readBeanApiFilter) {
        this.readBeanApiFilter = readBeanApiFilter;
    }

    public Class<? extends ReadControllerApiFilter> getReadControllerApiFilter() {
        return readControllerApiFilter;
    }

    public void setReadControllerApiFilter(Class<? extends ReadControllerApiFilter> readControllerApiFilter) {
        this.readControllerApiFilter = readControllerApiFilter;
    }

    public Class<? extends ReadInterfaceApiFilter> getReadInterfaceApiFilter() {
        return readInterfaceApiFilter;
    }

    public void setReadInterfaceApiFilter(Class<? extends ReadInterfaceApiFilter> readInterfaceApiFilter) {
        this.readInterfaceApiFilter = readInterfaceApiFilter;
    }

    public Class<? extends ReadRequestParamApiFilter> getReadRequestParamApiFilter() {
        return readRequestParamApiFilter;
    }

    public void setReadRequestParamApiFilter(Class<? extends ReadRequestParamApiFilter> readRequestParamApiFilter) {
        this.readRequestParamApiFilter = readRequestParamApiFilter;
    }

    public Class<? extends ReadResponseParamApiFilter> getReadResponseParamApiFilter() {
        return readResponseParamApiFilter;
    }

    public void setReadResponseParamApiFilter(Class<? extends ReadResponseParamApiFilter> readResponseParamApiFilter) {
        this.readResponseParamApiFilter = readResponseParamApiFilter;
    }

    public ReadBeanApiFilter getBeanApiFilter() {
        if(beanApiFilter != null){
            return beanApiFilter;
        }
        if(getReadBeanApiFilter() != null){
            beanApiFilter = ObjectUtil.newInstance(getReadBeanApiFilter());
            return beanApiFilter;
        }
        beanApiFilter = new DefaultReadBeanApi();
        return beanApiFilter;
    }

    public ReadControllerApiFilter getControllerApiFilter() {
        if(controllerApiFilter != null){
            return controllerApiFilter;
        }
        if(getReadControllerApiFilter() != null){
            controllerApiFilter = ObjectUtil.newInstance(getReadControllerApiFilter());
            return controllerApiFilter;
        }
        controllerApiFilter = new DefaultReadControllerApi();
        return controllerApiFilter;
    }

    public ReadInterfaceApiFilter getInterfaceApiFilter() {
        if(interfaceApiFilter != null){
            return interfaceApiFilter;
        }
        if(getReadInterfaceApiFilter() != null){
            interfaceApiFilter = ObjectUtil.newInstance(getReadInterfaceApiFilter());
            return interfaceApiFilter;
        }
        interfaceApiFilter = new DefaultReadInterfaceApi();
        return interfaceApiFilter;
    }

    public ReadRequestParamApiFilter getRequestParamApiFilter() {
        if(requestParamApiFilter != null){
            return requestParamApiFilter;
        }
        if(getReadRequestParamApiFilter() != null){
            requestParamApiFilter = ObjectUtil.newInstance(getReadRequestParamApiFilter());
            return requestParamApiFilter;
        }
        requestParamApiFilter = new DefaultReadRequestParamApi();
        return requestParamApiFilter;
    }

    public ReadResponseParamApiFilter getResponseParamApiFilter() {
        if(responseParamApiFilter != null){
            return responseParamApiFilter;
        }
        if(getReadResponseParamApiFilter() != null){
            responseParamApiFilter = ObjectUtil.newInstance(getReadResponseParamApiFilter());
            return responseParamApiFilter;
        }
        responseParamApiFilter = new DefaultReadResponseParamApi();
        return responseParamApiFilter;
    }


}
