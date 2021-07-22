package cn.easyutil.easyapi.filter.model;

import cn.easyutil.easyapi.filter.ReadRequestParamApiFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 读取请求参数相关的api
 *
 */
public class DefaultReadRequestParamApi implements ReadRequestParamApiFilter {

    private List<Class> notParseClass;

    @Override
    public boolean ignore(Class cl) {
        if(this.notParseClass == null){
            this.notParseClass = new ArrayList<>();
            this.notParseClass.add(HttpServletRequest.class);
            this.notParseClass.add(HttpServletResponse.class);
        }
        for (Class clazz : notParseClass) {
            if (cl.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }
}
