package cn.easyutil.easyapi.entity.read;

import cn.easyutil.easyapi.util.StringUtil;

import java.util.List;

/**
 * 接口参数说明
 */
public class MethodParameters {

    //接口方法名称
    private String methodName;

    //接口参数说明
    private List<ParameterComment> parameters;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<ParameterComment> getParameters() {
        return parameters;
    }

    public void setParameters(List<ParameterComment> parameters) {
        this.parameters = parameters;
    }

    @Override
    public boolean equals(Object obj) {
        //非同类，返回false
        if(!(obj instanceof MethodParameters)){
            super.equals(obj);
        }
        if(StringUtil.isEmpty(this.getMethodName())){
            return false;
        }
        MethodParameters mc = (MethodParameters) obj;
        return this.getMethodName().equals(mc.getMethodName());
    }
}
