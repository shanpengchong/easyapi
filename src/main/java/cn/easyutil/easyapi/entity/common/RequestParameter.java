package cn.easyutil.easyapi.entity.common;

import java.lang.annotation.*;

/**
 * 将请求参数名称对应的实体转换为其他类型
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParameter {

    //参数名称
    String parameterName();

    //参数类型
    Class parameterType();
}
