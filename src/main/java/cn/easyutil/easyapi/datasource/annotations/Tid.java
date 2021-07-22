package cn.easyutil.easyapi.datasource.annotations;

import java.lang.annotation.*;

/**
 * 标注表主键名称
 * 如果名字为id，则不用标记
 * @author spc
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Tid {
}
