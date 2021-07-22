package cn.easyutil.easyapi.entity.read;

import cn.easyutil.easyapi.util.StringUtil;

/**
 * 接口方法注释
 */
public class MethodAlias {

    //方法名
    private String methodName;

    //方法说明
    private String methodAliasName;

    public MethodAlias(){}

    public MethodAlias(String methodName){
        this.methodName = methodName;
    }

    public MethodAlias(String methodName, String methodAliasName){
        this.methodAliasName = methodAliasName;
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodAliasName() {
        return methodAliasName;
    }

    public void setMethodAliasName(String methodAliasName) {
        this.methodAliasName = methodAliasName;
    }

    @Override
    public boolean equals(Object obj) {
        //非同类，返回false
        if(!(obj instanceof MethodAlias)){
            super.equals(obj);
        }
        if(StringUtil.isEmpty(this.getMethodName())){
            return false;
        }
        MethodAlias mc = (MethodAlias) obj;
        return this.getMethodName().equals(mc.getMethodName());
    }
}
