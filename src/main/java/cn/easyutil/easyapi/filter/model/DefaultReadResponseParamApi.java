package cn.easyutil.easyapi.filter.model;

import cn.easyutil.easyapi.filter.ReadResponseParamApiFilter;

import java.util.List;

/**
 * 读取接口返回相关的api
 */
public class DefaultReadResponseParamApi implements ReadResponseParamApiFilter {

    @Override
    public Class parsingResponseType(Class returnClass) {
        return returnClass;
    }
}
