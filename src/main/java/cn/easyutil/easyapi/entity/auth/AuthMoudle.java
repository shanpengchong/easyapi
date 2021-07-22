package cn.easyutil.easyapi.entity.auth;

import cn.easyutil.easyapi.util.JsonUtil;
import cn.easyutil.easyapi.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 权限功能模块
 */
public enum AuthMoudle {

    /** 同步接口*/
    manualSync(10001,"同步接口","MANUALSYNC"),

    manualSyncAll(10002,"同步全部文档","MANUALSYNCALL"),

    getInfo(20001,"查看文档说明","GETINFO",1),

    updateInfo(20201,"修改文档说明","UPDATEINFO"),

    getOutPackage(20002,"查看返回外包装","GETOUTPACKAGE",1),

    updateOutPackage(20201,"修改返回外包装","UPDATEOUTPACKAGE"),

    getControllers(30001,"查看控制器列表","GETCONTROLLERS",1),

    findInterfaces(40001,"搜索接口","FINDINTERFACES",1),

    getInterfaces(40002,"查看全部接口","GETINTERFACES",1),

    addInterfaces(40101,"添加接口","ADDINTERFACES"),

    updateInteface(40201,"修改接口信息","UPDATEINTEFACE"),

    delInterfaces(40301,"删除接口","DELINTERFACES"),

    mock(50001,"mock返回数据","MOCK",1),

    updateMock(50201,"修改mock数据","UPDATEMOCK"),

    getRequestParam(60001,"查看请求参数","GETREQUESTPARAM",1),

    updateRequestParams(60201,"修改请求参数","UPDATEREQUESTPARAMS"),

    getResponseParam(70001,"查看响应参数","GETRESPONSEPARAM",1),

    updateResponseParams(70201,"修改响应参数","UPDATERESPONSEPARAMS"),

    doUrl(80001,"真实环境请求","DOURL"),

    unknow(-1,"未知功能","unknow");

    private Integer authCode;
    //是否默认通用权限
    private Integer defaultStatus = 0;
    private String moudleRemark;
    private String moudleName;

    private AuthMoudle(Integer authCode,String moudleRemark,String moudleName){
        this.authCode = authCode;
        this.moudleRemark = moudleRemark;
        this.moudleName = moudleName;
    }

    private AuthMoudle(Integer authCode,String moudleRemark,String moudleName,Integer defaultStatus){
        this.authCode = authCode;
        this.moudleRemark = moudleRemark;
        this.moudleName = moudleName;
        this.defaultStatus = defaultStatus;
    }

    public static boolean hasAuth(Integer authCode, List<Integer> auths){
        if(StringUtil.isEmpty(authCode) || auths==null){
            return false;
        }
        AuthMoudle authMoudle = get(authCode);
        if(authMoudle == unknow){
            return false;
        }
        return auths.contains(authMoudle.authCode);
    }

    public static AuthMoudle get(String moudleName){
        AuthMoudle[] values = AuthMoudle.values();
        for (AuthMoudle value : values) {
            if(value.moudleName.equalsIgnoreCase(moudleName)){
                return value;
            }
        }
        return unknow;
    }

    public static AuthMoudle get(Integer authCode){
        AuthMoudle[] values = AuthMoudle.values();
        for (AuthMoudle value : values) {
            if(value.authCode.equals(authCode)){
                return value;
            }
        }
        return unknow;
    }

    public static List<AuthMoudleParseBean> parseBeans(){
        List<AuthMoudleParseBean> result = new ArrayList<>();
        AuthMoudle[] values = AuthMoudle.values();
        for (AuthMoudle value : values) {
            AuthMoudleParseBean bean = new AuthMoudleParseBean();
            bean.setAuthCode(value.getAuthCode());
            bean.setMoudleName(value.getMoudleName());
            bean.setMoudleRemark(value.getMoudleRemark());
            result.add(bean);
        }
        return result;
    }

    public static List<Integer> getDefaultAuthCodes(){
        List<Integer> defaultCodes = new ArrayList<>();
        AuthMoudle[] values = AuthMoudle.values();
        for (AuthMoudle value : values) {
            if(value.getDefaultStatus() == 1){
                defaultCodes.add(value.getAuthCode());
            }
        }
        return defaultCodes;
    }

    public static List<Integer> getAllAuthCodes(){
        List<Integer> allCodes = new ArrayList<>();
        AuthMoudle[] values = AuthMoudle.values();
        for (AuthMoudle value : values) {
            allCodes.add(value.getAuthCode());
        }
        return allCodes;
    }

    public Integer getAuthCode() {
        return authCode;
    }

    public void setAuthCode(Integer authCode) {
        this.authCode = authCode;
    }

    public String getMoudleRemark() {
        return moudleRemark;
    }

    public void setMoudleRemark(String moudleRemark) {
        this.moudleRemark = moudleRemark;
    }

    public String getMoudleName() {
        return moudleName;
    }

    public void setMoudleName(String moudleName) {
        this.moudleName = moudleName;
    }

    public Integer getDefaultStatus() {
        return defaultStatus;
    }

    public void setDefaultStatus(Integer defaultStatus) {
        this.defaultStatus = defaultStatus;
    }
}
