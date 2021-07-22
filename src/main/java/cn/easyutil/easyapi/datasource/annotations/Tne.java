package cn.easyutil.easyapi.datasource.annotations;

import java.lang.annotation.*;

/** 标记数据库表名 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Tne {
    String value()default"";
    String name()default"";
}
