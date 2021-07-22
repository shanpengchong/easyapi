package cn.easyutil.easyapi.entity.doc;

import cn.easyutil.easyapi.datasource.annotations.Tfd;
import cn.easyutil.easyapi.datasource.annotations.Tne;
import cn.easyutil.easyapi.entity.common.BaseBean;

@Tne("easyapi_interface_param")
public class InterfaceParamBean extends BaseBean {

    @Tfd("PROJECT_ID")
    private Long projectId;

    @Tfd("INTERFACE_ID")
    private Long interfaceId;

    //0-请求参数 1-返回参数
    @Tfd("TYPE")
    private Integer type;

    //存储的json
    @Tfd("PARAMS_JSON")
    private String paramsJson;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(Long interfaceId) {
        this.interfaceId = interfaceId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getParamsJson() {
        return paramsJson;
    }

    public void setParamsJson(String paramsJson) {
        this.paramsJson = paramsJson;
    }
}
