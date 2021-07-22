package cn.easyutil.easyapi.datasource.annotations;

import java.lang.annotation.*;

/**
 * 标注数据库表的字段名称
 * 如果与本地名称相同，则不用标记
 * @author spc
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Tfd {

    /** 与数据库不同的字段*/
    String value()default"";
    String name()default"";
}
