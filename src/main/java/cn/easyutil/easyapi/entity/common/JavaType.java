package cn.easyutil.easyapi.entity.common;

import cn.easyutil.easyapi.entity.doc.ControllerBean;
import cn.easyutil.easyapi.parameterized.ParameterizedUtil;
import cn.easyutil.easyapi.util.ObjectUtil;
import cn.easyutil.easyapi.util.StringUtil;
import org.springframework.web.multipart.MultipartFile;
import sun.reflect.generics.reflectiveObjects.TypeVariableImpl;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * java类型
 *
 * @author spc
 */
public enum JavaType {

    //1-整数 2-数字 3-布尔 4-字符 5-基本类型数组 6-对象 7-文件 8-map 9-对象数组
    Long(Long.class, "整数", 1),
    Integer(Integer.class, "整数", 1),
    Double(Double.class, "数字", 2),
    Float(Float.class, "数字", 2),
    Byte(Byte.class, "整数", 1),
    Boolean(Boolean.class, "布尔", 3),
    Short(Short.class, "整数", 1),
    String(String.class, "字符", 4),
    Character(Character.class, "字符", 4),
    Array(Collection.class, "基本数组", 5),
    ArrayObject(Collection.class, "对象数组", 9),
    File(MultipartFile.class, "文件", 7),
    Map(java.util.Map.class, "map", 8),
    Object(null, "对象", 6);

    private Class javaClass;
    private String remark;
    private Integer type;
    private static JavaType[] baseType = new JavaType[]{
            Long, Integer, Double, Float, Byte, Boolean, Short, Character, String, File, Map
    };

    private JavaType(Class javaClass, String remark, Integer type) {
        this.javaClass = javaClass;
        this.remark = remark;
        this.type = type;
    }

    /**
     * 判断是否基本类型，单层结构
     *
     * @param type
     * @return
     */
    public static boolean isBaseType(JavaType type) {
        for (JavaType javaType : baseType) {
            if (type == javaType) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否基本类型，单层结构
     *
     * @param type
     * @return
     */
    public static boolean isBaseType(Integer type) {
        if(type == null){
            return false;
        }
        for (JavaType javaType : baseType) {
            if (type == javaType.getType()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据类型获取说明
     *
     * @param type
     * @return
     */
    public static JavaType getJavaType(Type type,Class...generic) {
        java.lang.String tn = type.getTypeName();
        if (tn.equals("long") || tn.equals("java.lang.Long")) {
            return Long;
        }
        if (tn.equals("int") || tn.equals("java.lang.Integer")) {
            return Integer;
        }
        if (tn.equals("double") || tn.equals("java.lang.Double")) {
            return Double;
        }
        if (tn.equals("float") || tn.equals("java.lang.Float")) {
            return Float;
        }
        if (tn.equals("byte") || tn.equals("java.lang.Byte")) {
            return Byte;
        }
        if (tn.equals("short") || tn.equals("java.lang.Short")) {
            return Short;
        }
        if (tn.equals("boolean") || tn.equals("java.lang.Boolean")) {
            return Boolean;
        }
        if (tn.equals("string") || tn.equals("java.lang.String")) {
            return String;
        }
        try {
            Class<?> aClass = Class.forName(tn);
            if(java.util.Map.class.isAssignableFrom(aClass)){
                return Map;
            }
        }catch (Exception e){}
        if(ParameterizedUtil.isArray(type)){
            if(type instanceof ParameterizedType){
                ParameterizedType pt = (ParameterizedType) type;
                Type[] types = pt.getActualTypeArguments();
                if(types.length > 1){
                    return ArrayObject;
                }
                Type t = pt.getActualTypeArguments()[0];
                JavaType javaType = getJavaType(t);
                if(javaType!=Object && javaType!=ArrayObject && javaType!=Array){
                    return Array;
                }
                return ArrayObject;
            }
            JavaType javaType = Object;
            //第二种数组的情况`
            if(type instanceof GenericArrayType){
                GenericArrayType gt = (GenericArrayType) type;
                Type genericComponentType = gt.getGenericComponentType();
                javaType = getJavaType(genericComponentType);
            }
            if(javaType != Object){
                return Array;
            }
            return ArrayObject;
        }
        return Object;
//        try {
//            if (type instanceof ParameterizedType) {
//                ParameterizedType pt = (ParameterizedType) type;
//                // 得到泛型里的class类型对象
//                Type t = pt.getActualTypeArguments()[0];
//                if(TypeVariableImpl.class.isAssignableFrom(t.getClass())){
//                    if (isBaseType(getJavaType(generic[0]))) {
//                        return Array;
//                    }
//                    return ArrayObject;
//                }
//                Class<?> actualTypeArgument = (Class<?>) t;
//                if (isBaseType(getJavaType(actualTypeArgument))) {
//                    return Array;
//                }
//                return ArrayObject;
//            } else if (type.getTypeName().endsWith("[]")) {
//                String typeName = type.getTypeName();
//                Class<?> actualTypeArgument = ObjectUtil.forName(typeName.substring(0, typeName.indexOf("[]")));
//                if (isBaseType(getJavaType(actualTypeArgument))) {
//                    return Array;
//                }
//                return ArrayObject;
//            }
//
//            return getJavaType(Class.forName(type.getTypeName()));
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
    }

    /**
     * 根据类型获取说明
     *
     * @param clazz
     * @return
     */
    public static JavaType getJavaType(Class clazz) {
        if (clazz.getName().equals("long")) {
            return Long;
        }
        if (clazz.getName().equals("int")) {
            return Integer;
        }
        if (clazz.getName().equals("double")) {
            return Double;
        }
        if (clazz.getName().equals("float")) {
            return Float;
        }
        if (clazz.getName().equals("byte")) {
            return Byte;
        }
        if (clazz.getName().equals("short")) {
            return Short;
        }
        if (clazz.getName().equals("boolean")) {
            return Boolean;
        }
        if (clazz.getName().equals("string")) {
            return String;
        }
        if(java.util.Map.class.isAssignableFrom(clazz)){
            return Map;
        }
        if(clazz.isArray()){
            return Array;
        }
        JavaType[] values = JavaType.values();
        for (JavaType javaType : values) {
            Class javaClass2 = javaType.getJavaClass();
            if (javaClass2 == null) {
                continue;
            }
            if (javaClass2.isAssignableFrom(clazz)) {
                return javaType;
            }
        }
        return Object;
    }

    public static JavaType getType(Integer type){
        if(StringUtil.isEmpty(type)){
            return null;
        }
        JavaType[] values = JavaType.values();
        for (JavaType value : values) {
            if(value.getType() == type){
                return value;
            }
        }
        return null;
    }


    public Class getJavaClass() {
        return javaClass;
    }

    public String getRemark() {
        return remark;
    }

    public void setJavaClass(Class javaClass) {
        this.javaClass = javaClass;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }


}
