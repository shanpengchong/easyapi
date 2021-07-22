package cn.easyutil.easyapi.datasource.annotations;

import java.lang.annotation.*;

/**
 * 插入数据库时需要忽略的字段
 * 标注后该字段不会被插入数据库
 * @author spc
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Tie {
}
