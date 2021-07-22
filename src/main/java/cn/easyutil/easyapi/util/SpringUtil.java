package cn.easyutil.easyapi.util;

import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;

/**
 * @Description: SpringBoot 相关工具类
 * @Author: peng.liu
 * @CreateDate: 2018/7/25 1:17
 */
public class SpringUtil {

    /**
     * 解析SpringBoot的路由注解 得到路由映射mapping
     * 解析以下注解
     *
     * @param claszz 类
     * @see RequestMapping
     * @see GetMapping
     * @see PostMapping
     * @see PutMapping
     * @see DeleteMapping
     * @see PatchMapping
     * 查看spring源码可以看到，RequestMapping中有多个同等意义的属性，这里分别处理
     * value name path
     */
    public static String getMapping(Class claszz) {
        RequestMapping requestMapping = (RequestMapping) claszz.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            //
            if (!StringUtil.isEmpty(requestMapping.value())) {
                return requestMapping.value()[0];
            }
            if (!StringUtil.isEmpty(requestMapping.name())) {
                return requestMapping.name();
            }
            if (!StringUtil.isEmpty(requestMapping.path())) {
                return requestMapping.path()[0];
            }
        }

        GetMapping getMapping = (GetMapping) claszz.getAnnotation(GetMapping.class);
        if (getMapping != null) {
            if (!StringUtil.isEmpty(getMapping.value())) {
                return getMapping.value()[0];
            }
            if (!StringUtil.isEmpty(getMapping.name())) {
                return getMapping.name();
            }
            if (!StringUtil.isEmpty(getMapping.path())) {
                return getMapping.path()[0];
            }
        }

        PostMapping postMapping = (PostMapping) claszz.getAnnotation(PostMapping.class);
        if (postMapping != null) {
            if (!StringUtil.isEmpty(postMapping.value())) {
                return postMapping.value()[0];
            }
            if (!StringUtil.isEmpty(postMapping.name())) {
                return postMapping.name();
            }
            if (!StringUtil.isEmpty(postMapping.path())) {
                return postMapping.path()[0];
            }
        }

        PutMapping putMapping = (PutMapping) claszz.getAnnotation(PutMapping.class);
        if (putMapping != null) {
            if (!StringUtil.isEmpty(putMapping.value())) {
                return putMapping.value()[0];
            }
            if (!StringUtil.isEmpty(putMapping.name())) {
                return putMapping.name();
            }
            if (!StringUtil.isEmpty(putMapping.path())) {
                return putMapping.path()[0];
            }
        }

        DeleteMapping deleteMapping = (DeleteMapping) claszz.getAnnotation(DeleteMapping.class);
        if (deleteMapping != null) {
            if (!StringUtil.isEmpty(deleteMapping.value())) {
                return deleteMapping.value()[0];
            }
            if (!StringUtil.isEmpty(deleteMapping.name())) {
                return deleteMapping.name();
            }
            if (!StringUtil.isEmpty(deleteMapping.path())) {
                return deleteMapping.path()[0];
            }
        }
        return null;
    }

    /**
     * 解析SpringBoot的路由注解 得到路由映射mapping
     * 解析以下注解
     *
     * @param method 方法
     * @see RequestMapping
     * @see GetMapping
     * @see PostMapping
     * @see PutMapping
     * @see DeleteMapping
     * @see PatchMapping
     * 查看spring源码可以看到，RequestMapping中有多个同等意义的属性，这里分别处理
     * value name path
     */
    public static String getMapping(Method method) {
        RequestMapping requestMapping = (RequestMapping) method.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            //
            if (!StringUtil.isEmpty(requestMapping.value()) && requestMapping.value().length>0) {
                return requestMapping.value()[0];
            }
            if (!StringUtil.isEmpty(requestMapping.name())) {
                return requestMapping.name();
            }
            if (!StringUtil.isEmpty(requestMapping.path()) && requestMapping.path().length>0) {
                return requestMapping.path()[0];
            }
        }

        GetMapping getMapping = (GetMapping) method.getAnnotation(GetMapping.class);
        if (getMapping != null) {
            if (!StringUtil.isEmpty(getMapping.value()) && getMapping.value().length>0) {
                return getMapping.value()[0];
            }
            if (!StringUtil.isEmpty(getMapping.name())) {
                return getMapping.name();
            }
            if (!StringUtil.isEmpty(getMapping.path()) && getMapping.path().length>0) {
                return getMapping.path()[0];
            }
        }

        PostMapping postMapping = (PostMapping) method.getAnnotation(PostMapping.class);
        if (postMapping != null) {
            if (!StringUtil.isEmpty(postMapping.value()) && postMapping.value().length>0) {
                return postMapping.value()[0];
            }
            if (!StringUtil.isEmpty(postMapping.name())) {
                return postMapping.name();
            }
            if (!StringUtil.isEmpty(postMapping.path()) && postMapping.path().length>0) {
                return postMapping.path()[0];
            }
        }

        PutMapping putMapping = (PutMapping) method.getAnnotation(PutMapping.class);
        if (putMapping != null) {
            if (!StringUtil.isEmpty(putMapping.value()) && putMapping.value().length>0) {
                return putMapping.value()[0];
            }
            if (!StringUtil.isEmpty(putMapping.name())) {
                return putMapping.name();
            }
            if (!StringUtil.isEmpty(putMapping.path()) && putMapping.path().length>0) {
                return putMapping.path()[0];
            }
        }

        DeleteMapping deleteMapping = (DeleteMapping) method.getAnnotation(DeleteMapping.class);
        if (deleteMapping != null) {
            if (!StringUtil.isEmpty(deleteMapping.value()) && deleteMapping.value().length>0) {
                return deleteMapping.value()[0];
            }
            if (!StringUtil.isEmpty(deleteMapping.name())) {
                return deleteMapping.name();
            }
            if (!StringUtil.isEmpty(deleteMapping.path()) && deleteMapping.path().length>0) {
                return deleteMapping.path()[0];
            }
        }
        return null;
    }

    /**
     * 获取请求方式
     * 请求方式 0-全部  1-get  2-post 3-put  4-delete
     *
     * @param method 类
     * @return String 请求方式 eg: get 或者 get,put,post 或者 all(所有请求方式)
     * @see RequestMethod
     */
    public static Integer getRequestMethod(Method method) {
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            RequestMethod[] requestMethods = requestMapping.method();
            if (requestMethods.length > 0) {
                for (RequestMethod requestMethod : requestMethods) {
                	if(requestMethod == RequestMethod.POST){
                		return 2;
                	}else if(requestMethod == RequestMethod.GET){
                		return 1;
                	}else if(requestMethod == RequestMethod.DELETE){
                		return 4;
                	}else if(requestMethod == RequestMethod.PUT){
                		return 3;
                	}
                }
            }
        }
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        if (getMapping != null) {
            return 1;
        }
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        if (postMapping != null) {
            return 2;
        }
        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        if (putMapping != null) {
            return 3;
        }
        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        if (deleteMapping != null) {
            return 4;
        }
        return 0;
    }


}
