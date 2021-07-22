package cn.easyutil.easyapi.entity.doc;

import cn.easyutil.easyapi.datasource.annotations.Tfd;
import cn.easyutil.easyapi.datasource.annotations.Tne;
import cn.easyutil.easyapi.entity.common.BaseBean;

@Tne("easyapi_interface_mock")
public class MockBean extends BaseBean {

    @Tfd("PROJECT_ID")
    private Long projectId;

    @Tfd("INTERFACE_ID")
    private Long interfaceId;

    @Tfd("MOCK")
    private String mock;

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

    public String getMock() {
        return mock;
    }

    public void setMock(String mock) {
        this.mock = mock;
    }


}
