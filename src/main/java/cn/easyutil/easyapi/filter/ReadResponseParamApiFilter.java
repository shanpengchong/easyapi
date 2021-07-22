package cn.easyutil.easyapi.filter;

import java.util.List;

/**
 * 读取接口返回相关的api
 */
public interface ReadResponseParamApiFilter {

    /**
     * 解析返回值类型
     * @param returnClass
     * @return
     */
    Class parsingResponseType(Class returnClass);

    /**
     * 是否对hidden属性的参数进行mock
     * @return
     */
    default boolean isMockHiddenParam(boolean hidden){
        return !hidden;
    }

    /**
     * 是否对required属性的参数进行mock
     * @return
     */
    default boolean isMockRequiredParam(boolean required){
        return true;
    }
}
