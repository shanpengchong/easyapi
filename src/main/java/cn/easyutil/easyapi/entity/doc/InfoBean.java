package cn.easyutil.easyapi.entity.doc;

import cn.easyutil.easyapi.datasource.annotations.Tfd;
import cn.easyutil.easyapi.datasource.annotations.Tie;
import cn.easyutil.easyapi.datasource.annotations.Tne;
import cn.easyutil.easyapi.entity.common.BaseBean;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

@Tne("easyapi_project")
public class InfoBean extends BaseBean {

    @Tfd("TITLE")
    @ApiModelProperty("项目标题")
    private String title;

    @Tfd("NAME")
    private String name;

    @Tfd("REMARK")
    @ApiModelProperty("项目介绍")
    private String remark;

    @Tfd("IMG")
    @ApiModelProperty("项目缩略图")
    private String img;

    @Tfd("DISABLE")
    @ApiModelProperty("0-正常  1-关闭")
    private Integer disable;

    @Tfd("INIT_PROJECT")
    @ApiModelProperty("是否初始项目 0-不是  1-是")
    private Integer initProject;

    @Tie
    @ApiModelProperty("项目请求地址")
    private List<HostBean> requestHost = new ArrayList<HostBean>();


    public String getTitle() {
        return title;
    }

    public String getRemark() {
        return remark;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<HostBean> getRequestHost() {
        return requestHost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRequestHost(String name, String host) {
        HostBean bean = new HostBean();
        bean.setName(name);
        bean.setHost(host);
        this.requestHost.add(bean);
    }

    public void setRequestHost(List<HostBean> requestHost) {
        this.requestHost = requestHost;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Integer getDisable() {
        return disable;
    }

    public void setDisable(Integer disable) {
        this.disable = disable;
    }

    public Integer getInitProject() {
        return initProject;
    }

    public void setInitProject(Integer initProject) {
        this.initProject = initProject;
    }
}