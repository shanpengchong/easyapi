package cn.easyutil.easyapi.filter;

import cn.easyutil.easyapi.entity.common.JavaType;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 读取文档时的配置
 */
public interface ReadBeanApiFilter {

    /**
     * 读取实体bean类型
     * @param type  实体bean类型
     * @return  实体bean类型
     */
    JavaType readBeanType(Type type);

    /**
     * 将实体bean的类型转换为其他类型进行解析
     * @param type  原始类型
     * @return  转换后的类型
     */
    Type parseType(Type type);

    /**
     * 读取实体bean字段说明
     * @param field 属性
     * @return  属性说明
     */
    String readBeanFieldComments(Field field);

    /**
     * 读取实体bean的mock数据
     * @param field  注解上的mock配置
     * @return  实际mock值
     */
    String readBeanFieldMock(Field field);

    /**
     * 解析mock实际值
     * @param template
     * @return
     */
    String parseMock(String template);

    /**
     * 读取实体bean属性是否必须
     * @param field
     * @return  true：必须  false：非必须
     */
    boolean readBeanFieldRequired(Field field);

    /**
     * 读取实体bean属性是否隐藏
     * @param field
     * @return  true：隐藏  false：非隐藏
     */
    boolean readBeanFieldHidden(Field field);

    /**
     * 读取实体bean属性是否忽略
     * @param field
     * @return  true：忽略  false：非忽略
     */
    boolean readBeanFieldIgnore(Field field);

}
