package cn.easyutil.easyapi.logic;

import cn.easyutil.easyapi.entity.common.JavaType;
import cn.easyutil.easyapi.entity.doc.ParamBean;
import cn.easyutil.easyapi.filter.ReadBeanApiFilter;
import cn.easyutil.easyapi.javadoc.JavaFileReader;
import cn.easyutil.easyapi.parameterized.ParameterizedTypeBind;
import cn.easyutil.easyapi.parameterized.ParameterizedUtil;
import cn.easyutil.easyapi.configuration.EasyapiConfiguration;
import cn.easyutil.easyapi.configuration.PathContext;
import cn.easyutil.easyapi.util.StringUtil;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.*;
import java.util.stream.Collectors;

public class ParamsBeanCreator {
    /**
     * 没有key的参数默认key
     */
    public static final String nullKey = "_null";

    public EasyapiConfiguration configuration;

    public static ParamsBeanCreator builder(EasyapiConfiguration configuration){
        ParamsBeanCreator creator = new ParamsBeanCreator(configuration);
        return creator;
    }

    private ParamsBeanCreator(EasyapiConfiguration configuration){
        this.configuration = configuration;
    }

    //已经创建好的bean集合
    private Map<String, Map<String,ParamBean>> creaters = new HashMap<>();

    private String buildCreatersKey(Class aClass,ParameterizedTypeBind...binds){
        String bindKey = "";
        if(binds.length > 0){
            bindKey = binds[0].keys().stream().collect(Collectors.joining());
        }
        return aClass.getCanonicalName()+bindKey;
    }

    public Map<String,ParamBean> readBean(Type type,Class<?> aClass,ParameterizedTypeBind...binds){
        Map<String,ParamBean> result = new HashMap<>();
        ParameterizedTypeBind bind = ParameterizedUtil.parameterizedTypeBind(type);
        if (aClass == Object.class) {
            return new HashMap();
        }
        if(binds.length == 1){
            binds[0].bind(bind);
        }else{
            binds = new ParameterizedTypeBind[]{bind};
        }
        String creatersKey = buildCreatersKey(aClass, bind);
        if(creaters.containsKey(creatersKey)){
            result.putAll(creaters.get(creatersKey));
            return result;
        }
        creaters.put(creatersKey,new HashMap<>());
        Map<String, ParamBean> bean = getBean(aClass, binds[0]);
        //先放一个占位
        creaters.put(creatersKey,bean);
        return bean;
    }

    public List<Object> readArray(Type type){
        List<Object> childrenArray = new ArrayList<Object>();
        Map<String, ParamBean> childrenMap = new HashMap<>();
        //基本数据类型
        ParamBean childrean = new ParamBean();
        childrean.setRequired(0);
        childrean.setShow(0);
        childrean.setType(JavaType.getJavaType(ParameterizedUtil.getArrayType(type)).getType());
        childrean.setJava_name(type.getTypeName());
        childrenMap.put(nullKey, childrean);
        childrenArray.add(childrenMap);
        return childrenArray;
    }

    public List<Object> readArrayObject(Type type,ParameterizedTypeBind...binds){
        if(binds.length == 0){
            binds = new ParameterizedTypeBind[]{new ParameterizedTypeBind()};
        }
        List<Object> childrenArray = new ArrayList<Object>();
        //获取集合中的真实泛型类型
        Type arrayType = ParameterizedUtil.getArrayType(type);
        //如果泛型仍然是数组，则递归
        if(ParameterizedUtil.isArray(arrayType)){
            JavaType javaType = JavaType.getJavaType(arrayType);

//            if(javaType == JavaType.ArrayObject){
//                childrenArray.add(readArrayObject(arrayType));
//                return childrenArray;
//            }else if(javaType == JavaType.Array){
//                childrenArray.add(readArray(arrayType));
//                return childrenArray;
//            }


            Map<String,ParamBean> arrayParam = new HashMap<>();
            ParamBean bean = new ParamBean();
            arrayParam.put(nullKey,bean);
            bean.setType(javaType.getType());
            bean.setRequired(1);
            bean.setShow(1);
            if(javaType == JavaType.ArrayObject){
                bean.setChildren(readArrayObject(arrayType));
                childrenArray.add(arrayParam);
                return childrenArray;
            }else if(javaType == JavaType.Array){
                bean.setChildren(readArray(arrayType));
                childrenArray.add(arrayParam);
                return childrenArray;
            }
        }

        Class aclass = null;
        if(arrayType instanceof TypeVariable){
            Type rawType = binds[0].getRawType(arrayType.toString());
            arrayType = rawType==null?arrayType:rawType;
        }
        if(ParameterizedUtil.isParameterizedType(arrayType)){
            aclass = ((ParameterizedTypeImpl) arrayType).getRawType();
        }
        if(aclass == null){
            aclass = (Class) arrayType;
        }
        Map<String, ParamBean> map = readBean(arrayType, aclass, binds);
        if(map.size() > 0){
            childrenArray.add(map);
        }
        return childrenArray;
    }

    private Map<String,ParamBean> getBean(Class beanClass,ParameterizedTypeBind bind){
        ReadBeanApiFilter filter = configuration.getBeanApiFilter();
        Map<String, ParamBean> result = new HashMap<>();
        //先读取源码字段注释
        String path = beanClass.getCanonicalName().replace(".", File.separator) + ".java";
        Map<String, String> fieldComment = JavaFileReader.readFieldComment(new File(PathContext.projectBasePath + path));

        //需要读取父类
        Class clazz = beanClass;
        //存放所有子类及父类的字段
        Set<String> fieldNames = new HashSet<>();
        while (clazz!=null && !clazz.equals(Object.class)) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                //如果该字段标记忽略，则不解析
                if (filter.readBeanFieldIgnore(field)) {
                    continue;
                }
                int size = fieldNames.size();
                fieldNames.add(field.getName());
                if (size == fieldNames.size()) {
                    //父类不覆盖子类，子类最大
                    continue;
                }
                Type type = field.getGenericType();
                Class typeClass = field.getType();
                if(type == null){
                    type = field.getType();
                }
                //转换类型
                type = filter.parseType(type);
                //如果是泛型，则替换泛型的真实类型
                point:if (ParameterizedUtil.isParameterizedType(type)) {
                    if(bind.getRawType(type.getTypeName())==null){
                        break point;
                    }
                    type = bind.getRawType(type.getTypeName());
                    if(ParameterizedUtil.isParameterizedType(type)){
                        typeClass = ((ParameterizedTypeImpl) type).getRawType();
                    }else if(type instanceof WildcardType || type instanceof TypeVariable){
                        continue;
                    }else{
                        typeClass =(Class) type;
                    }

                }
                if(type instanceof WildcardType || type instanceof TypeVariable){
                    continue;
                }
                //读取字段说明
                String comments = filter.readBeanFieldComments(field);
                if (StringUtil.isEmpty(comments)) {
                    //读取结果不存在则读取字段注释
                    comments = fieldComment.get(field.getName());
                    if (StringUtil.isEmpty(comments)) {
                        //注释不存在则使用属性名
                        comments = field.getName();
                    }
                }
                //获取类属性信息
                String mock = filter.readBeanFieldMock(field);
                boolean hidden = filter.readBeanFieldHidden(field);
                boolean required = filter.readBeanFieldRequired(field);
                ParamBean requestBean = new ParamBean();
                requestBean.setRequired(required ? 1 : 0);
                requestBean.setMockValue(StringUtil.isEmpty(mock) ? "" : filter.parseMock(mock));
                requestBean.setOldMockValue(mock);
                requestBean.setShow(hidden ? 0 : 1);
                requestBean.setRemark(comments);
                requestBean.setName(field.getName());
                requestBean.setJava_name(type.getTypeName());
                JavaType javaType = filter.readBeanType(type);
                requestBean.setType(javaType.getType());
                if(typeClass.equals(Object.class)){
                    result.put(requestBean.getName(),requestBean);
                    continue;
                }
//                JavaType costomReadType = filter.readBeanType(field.getType());
//                if (costomReadType != javaType && costomReadType != javaType.Object) {
//                    javaType = costomReadType;
//                }
                switch (JavaType.getType(requestBean.getType())) {
                    case Object:
                        requestBean.setChildren(readBean(type, typeClass,bind));
                        break;
                    case ArrayObject:
                        requestBean.setChildren(readArrayObject(type,bind));
                        break;
                    case Array:
                        requestBean.setChildren(readArray(type));
                    default:
                        break;
                }
                result.put(requestBean.getName(),requestBean);
            }
            clazz = clazz.getSuperclass();
        }
        return result;
    }
}
