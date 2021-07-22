package cn.easyutil.easyapi.entity.auth;

public class AuthMoudleParseBean {
    private Integer authCode;
    private String moudleRemark;
    private String moudleName;

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
}
