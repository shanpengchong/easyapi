package cn.easyutil.easyapi.filter.model;

import cn.easyutil.easyapi.entity.common.ApidocComment;
import cn.easyutil.easyapi.entity.common.CustomRemarkEnum;
import cn.easyutil.easyapi.entity.common.JavaType;
import cn.easyutil.easyapi.filter.ReadBeanApiFilter;
import cn.easyutil.easyapi.util.StringUtil;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collection;

/**
 * 读取文档时的配置
 */
public class DefaultReadBeanApi implements ReadBeanApiFilter {


    @Override
    public JavaType readBeanType(Type type) {
        if(type.equals(BigDecimal.class)){
            return JavaType.Long;
        }
        if(type instanceof Class){
            if(MultipartFile.class.isAssignableFrom((Class<?>) type)){
                return JavaType.File;
            }
        }
        return JavaType.getJavaType(type);
    }

    @Override
    public Type parseType(Type type) {
        return type;
    }

    @Override
    public String readBeanFieldComments(Field field) {
        ApidocComment api = field.getDeclaredAnnotation(ApidocComment.class);
        if(api!=null && !StringUtil.isEmpty(api.value())){
            return api.value();
        }
        ApiModelProperty model = field.getDeclaredAnnotation(ApiModelProperty.class);
        if(model!=null && !StringUtil.isEmpty(model.value())){
            return model.value();
        }
        return null;
    }

    @Override
    public String parseMock(String template) {
        if(StringUtil.isEmpty(template)){
            return "";
        }
        return CustomRemarkEnum.getExample(template);
    }

    @Override
    public String readBeanFieldMock(Field field) {
        ApidocComment api = field.getDeclaredAnnotation(ApidocComment.class);
        if(api!=null && !StringUtil.isEmpty(api.mockValue())){
            return api.mockValue();
        }
        ApiModelProperty model = field.getDeclaredAnnotation(ApiModelProperty.class);
        if(model!=null && !StringUtil.isEmpty(model.example())){
            return model.example();
        }
        return "";
    }

    @Override
    public boolean readBeanFieldRequired(Field field) {
        ApidocComment api = field.getDeclaredAnnotation(ApidocComment.class);
        if(api!=null){
            return api.required();
        }
        ApiModelProperty model = field.getDeclaredAnnotation(ApiModelProperty.class);
        if(model!=null){
            return model.required();
        }
        return false;
    }

    @Override
    public boolean readBeanFieldHidden(Field field) {
        ApidocComment api = field.getDeclaredAnnotation(ApidocComment.class);
        if(api!=null){
            return api.hidden();
        }
        ApiModelProperty model = field.getDeclaredAnnotation(ApiModelProperty.class);
        if(model!=null){
            return model.hidden();
        }
        return false;
    }

    @Override
    public boolean readBeanFieldIgnore(Field field) {
        if(field.getType().isAssignableFrom(Serializable.class) && field.getGenericType()==null){
            return true;
        }
        if(field.getName().equals("serialVersionUID")){
            return true;
        }
        ApidocComment api = field.getDeclaredAnnotation(ApidocComment.class);
        if(api!=null){
            return api.ignore();
        }
        if(field.getType().equals(Class.class)){
            return true;
        }
        return false;
    }
}
