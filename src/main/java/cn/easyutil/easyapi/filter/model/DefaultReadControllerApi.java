package cn.easyutil.easyapi.filter.model;

import cn.easyutil.easyapi.entity.common.ApidocComment;
import cn.easyutil.easyapi.filter.ReadControllerApiFilter;
import cn.easyutil.easyapi.util.ObjectUtil;
import cn.easyutil.easyapi.util.SpringUtil;
import cn.easyutil.easyapi.util.StringUtil;
import io.swagger.annotations.Api;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 读取controller相关api
 */
public class DefaultReadControllerApi implements ReadControllerApiFilter {

    @Override
    public Set<Class> readControllers(ApplicationContext springContext) {
        Map<String, Object> beansWithAnnotation = springContext.getBeansWithAnnotation(Controller.class);
        Set<Class> set = new HashSet<>();
        Iterator<Map.Entry<String, Object>> iterator = beansWithAnnotation.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Object> next = iterator.next();
            Object value = next.getValue();
            Class<?> aClass = value.getClass();
            if(aClass.getCanonicalName().contains("$$")){
                set.add(aClass.getSuperclass());
                continue;
            }
            set.add(aClass);
        }
        return set;
    }

    @Override
    public boolean readControllerIgnore(Class controller) {
        if(controller.getCanonicalName().startsWith("org.springframework")){
            return true;
        }
        Map<String, ApidocComment> classAnnotation = ObjectUtil.getClassAnnotation(controller, ApidocComment.class);
        if(classAnnotation!=null && classAnnotation.size()>0){
            if(classAnnotation.get(controller.getName()) != null){
                if(classAnnotation.get(controller.getName()).ignore()){
                    //被忽略的控制器
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String readControllerAliasName(Class controller) {
        //最先判断本项目提供的注解
        Map<String, ApidocComment> classAnnotation = ObjectUtil.getClassAnnotation(controller, ApidocComment.class);
        if (classAnnotation != null && !classAnnotation.isEmpty()) {
            return classAnnotation.entrySet().iterator().next().getValue().value();
        }
        // 优先读取swagger注解
        Map<String, Api> map = ObjectUtil.getClassAnnotation(controller, Api.class);
        if (map != null && !map.isEmpty()) {
            Api api = map.entrySet().iterator().next().getValue();
            String comment = api.tags()[0];
            if(StringUtil.isEmpty(comment)){
                comment = api.value();
            }
            if(!StringUtil.isEmpty(comment)){
                return comment;
            }

        }
        return null;
    }

    @Override
    public String readControllerRequestPath(Class controller) {
        String mapping = SpringUtil.getMapping(controller);
        if(StringUtil.isEmpty(mapping)){
            return "";
        }
        // 获取请求路径
        String baseUrl = "";
        baseUrl += mapping;
        baseUrl = parseUrl(baseUrl);
        return baseUrl;
    }


    /**
     * 完善请求路径
     *
     * @param requestUrl
     * @return
     */
    private static String parseUrl(String requestUrl) {
        if(StringUtil.isEmpty(requestUrl)){
            return null;
        }
        if (!requestUrl.startsWith("/")) {
            requestUrl = "/" + requestUrl;
        }
        if (requestUrl.endsWith("/")) {
            requestUrl = requestUrl.substring(0, requestUrl.length() - 1);
        }
        return requestUrl;
    }
}
