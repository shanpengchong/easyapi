package cn.easyutil.easyapi.filter;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 读取请求参数相关的api
 *
 */
public interface ReadRequestParamApiFilter {

    /**
     * 是否解析对应的请求参数
     * @param cl 请求参数
     */
    boolean ignore(Class cl);

}
