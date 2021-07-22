package cn.easyutil.easyapi.parameterized;


import java.lang.reflect.*;
import java.util.Collection;

/**
 * 泛型工具
 */
public class ParameterizedUtil {

    /**
     * 获取集合或数组类类型
     * @param type
     * @return
     */
    public static Type getArrayType(Type type){
        if(!isArray(type)){
            return type;
        }
        if(type instanceof ParameterizedType){
            ParameterizedType pt = (ParameterizedType) type;
            return pt.getActualTypeArguments()[0];
        }
        if(type instanceof GenericArrayType){
            GenericArrayType gt = (GenericArrayType) type;
            return gt.getGenericComponentType();
        }
        return ((Class) type).getComponentType();
    }

    /**
     * 获取泛型的真实类型
     * @param type
     * @return
     */
    public static ParameterizedTypeBind parameterizedTypeBind(Type type){
        if(!isParameterizedType(type)) return new ParameterizedTypeBind();
        if(!(type instanceof ParameterizedType)){
            return new ParameterizedTypeBind();
        }
        Type[] types = ((ParameterizedType) type).getActualTypeArguments();
        Class rawClass = (Class) ((ParameterizedType) type).getRawType();
        TypeVariable[] typeParameters = rawClass.getTypeParameters();
        ParameterizedTypeBind bind = new ParameterizedTypeBind();
        for (int i = 0; i < typeParameters.length; i++) {
            bind.bind(typeParameters[i].getName(),types[i]);
        }
        return bind;
    }

    /**
     * 是否集合类型
     * @param type  类型
     * @return
     */
    public static boolean isArray(Type type){
        if(isParameterizedType(type)){
            if(type instanceof ParameterizedType) type = ((ParameterizedType)type).getRawType();
            if(type instanceof TypeVariable) return false;
        }
        if(type instanceof GenericArrayType){
            return true;
        }
        Class clazz = (Class) type;
        boolean isArray = Collection.class.isAssignableFrom(clazz);
        return isArray || clazz.isArray();
    }

    /**
     * 判断是否是泛型类型
     * @param type
     * @return
     */
    public static boolean isParameterizedType(Type type){
        boolean isParameterizedType =  type instanceof ParameterizedType;
        return isParameterizedType || type instanceof TypeVariable;
    }
}
