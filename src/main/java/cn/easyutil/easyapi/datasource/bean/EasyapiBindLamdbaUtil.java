package cn.easyutil.easyapi.datasource.bean;

import cn.easyutil.easyapi.datasource.annotations.Tfd;
import cn.easyutil.easyapi.util.StringUtil;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class EasyapiBindLamdbaUtil {

    /**
     * 获取表达式方法中的字段名
     * @param fun   表达式
     * @return
     */
    public static <T> String getFieldName(EasyapiBindLambdaFunction<T,?> fun){
        return getFieldName(fun,true);
    }

    /**
     * 获取表达式方法中的字段名
     * @param fun   表达式
     * @param conver    是否驼峰转下划线
     * @return
     */
    public static <T> String getFieldName(EasyapiBindLambdaFunction<T,?> fun, boolean conver) {
        Field field = getField(fun);
        if(field == null){
            return null;
        }
        Tfd an = field.getDeclaredAnnotation(Tfd.class);
        if(an != null){
            if(!StringUtil.isEmpty(an.value())){
                return an.value();
            }
            if(!StringUtil.isEmpty(an.name())){
                return an.name();
            }
        }
        if(conver){
            return StringUtil.conversionMapUnderscore(field.getName());
        }
        return field.getName();
    }

    private static <T,R> Field getField(EasyapiBindLambdaFunction<T,R> fun){
        SerializedLambda lambda = getSerializedLambda(fun);
        // 获取方法名
        String methodName = lambda.getImplMethodName();
        String fieldName = null;
        if(methodName.startsWith("get")){
            fieldName = methodName.substring(3);
        }else if(methodName.startsWith("is")){
            fieldName = methodName.substring(2);
        }
        if(fieldName == null){
            return null;
        }
        fieldName = fieldName.toUpperCase();
        String className = lambda.getImplClass().replace("/", ".");
        Field field = null;
        try {
            Class<?> aClass = Class.forName(className);
            Class clazz = aClass;
            while (clazz != Object.class){
                Field[] fields = aClass.getDeclaredFields();
                for (Field f : fields) {
                    if(f.getName().toUpperCase().equals(fieldName)){
                        return f;
                    }
                }
                clazz = clazz.getSuperclass();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关键在于这个方法
     */
    public static SerializedLambda getSerializedLambda(Serializable fn) {
        // 提取SerializedLambda并缓存
        try {
            Method method = fn.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            return (SerializedLambda) method.invoke(fn);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}