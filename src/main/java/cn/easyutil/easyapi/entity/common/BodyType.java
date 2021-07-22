package cn.easyutil.easyapi.entity.common;

import cn.easyutil.easyapi.util.StringUtil;

/**
 * 请求体类型
 */
public enum BodyType {
    //0-form  1-json体  2-文件上传 3-XML 4-RAW
    FORM(0),
    JSON(1),
    FILE(2),
    XML(3),
    RAW(4),
    UNKNOW(-1);

    private Integer type;

    private BodyType(Integer type){
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public static BodyType parse(Integer type){
        if(StringUtil.isEmpty(type)){
            return UNKNOW;
        }
        BodyType[] values = BodyType.values();
        for (BodyType value : values) {
            if(value.getType().equals(type)){
                return value;
            }
        }
        return UNKNOW;
    }
}
