package cn.easyutil.easyapi.filter;

import cn.easyutil.easyapi.entity.common.BodyType;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 读取接口方法相关的api
 */
public interface ReadInterfaceApiFilter {

    /**
     * 读取接口是否忽略
     * @param method
     * @return  true：忽略  false：非忽略
     */
    boolean readInterfaceIgnore(Method method);


    /**
     * 读取接口请求体类型
     * @param method
     * @return
     */
    BodyType readInterfaceBodyType(Method method);

    /**
     * 读取接口请求路径
     * @param method    接口方法
     * @return  请求路径
     */
    String readInterfaceRequestPath(Method method);

    /**
     * 读取接口名称
     * @param method    接口方法
     * @return  接口名称
     */
    String readInterfaceAliasName(Method method);

    /**
     * 读取接口说明
     * @param method    接口方法
     * @return  接口说明
     */
    String readInterfaceComments(Method method);

    /**
     * 读取接口请求说明
     * @param method    接口方法
     * @return  请求参数说明
     */
    String readRequestNotes(Method method);

    /**
     * 读取接口请求参数说明
     * @param parameter    接口方法
     * @return  请求参数说明
     */
    String readRequestParamComments(Parameter parameter);

    String readRequestParamMockValue(Parameter parameter);

    /**
     * 读取接口请求参数名称
     * @param parameter    接口方法
     * @return  请求参数说明
     */
    String readRequestParamName(Parameter parameter);

    /**
     * 读取接口返回说明
     * @param method    接口方法
     * @return  返回参数说明
     */
    String readResponseNotes(Method method);

}
