package cn.easyutil.easyapi.logic;

import cn.easyutil.easyapi.filter.model.DefaultReadResponseParamApi;
import cn.easyutil.easyapi.entity.common.JavaType;
import cn.easyutil.easyapi.entity.doc.ParamBean;
import cn.easyutil.easyapi.filter.ReadResponseParamApiFilter;
import cn.easyutil.easyapi.parameterized.ParameterizedUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 创建返回值
 */
public class ResponseParamCreator {

    private Method method;

    private ReadResponseParamApiFilter filter = new DefaultReadResponseParamApi();

    private Map<String,ParamBean> params = new HashMap<>();

    private ParamsBeanCreator paramsBeanCreator;

    private ResponseParamCreator(){}

    /**
     * 构造器
     * @param method
     * @return
     */
    public static ResponseParamCreator builder(Method method,ParamsBeanCreator paramsBeanCreator) {
        ResponseParamCreator creator = new ResponseParamCreator();
        creator.method = method;
        creator.paramsBeanCreator = paramsBeanCreator;
        return creator;
    }

    public ResponseParamCreator setFilter(ReadResponseParamApiFilter filter){
        if(filter == null){
            return this;
        }
        this.filter = filter;
        return this;
    }

    public Map<String, ParamBean> buildParamBean(){
        if(this.params.size() > 0){
            return this.params;
        }
        try {
            create();
        }catch (Exception e){
            System.out.println("创建返回值:"+method.toGenericString()+"失败");
            e.printStackTrace();
        }
        return this.params;
    }

    /**
     * 处理参数
     */
    private void create() {
        Class aClass = filter.parsingResponseType(method.getReturnType());
        Type type = method.getGenericReturnType();
        if(type == Void.TYPE){
            return ;
        }
        if(aClass != method.getReturnType()){
            type = aClass;
        }
        //组装参数
        ParamBean info = new ParamBean();
        info.setName(ParamsBeanCreator.nullKey);
        info.setType(JavaType.getJavaType(type).getType());
        info.setRequired(1);
        info.setShow(1);
        info.setJava_name(aClass.getCanonicalName());
        //重头戏
        switch (JavaType.getType(info.getType())){
            case Map:
                return ;
            case Object:
                this.params.putAll(paramsBeanCreator.readBean(type,aClass));
                break;
            case ArrayObject:
                info.setChildren(paramsBeanCreator.readArrayObject(type));
                this.params.put(info.getName(),info);
                break;
            case Array:
                info.setChildren(paramsBeanCreator.readArray(ParameterizedUtil.getArrayType(type)));
                this.params.put(info.getName(),info);
                break;
            default:
                this.params.put(info.getName(),info);
                break;
        }

    }

}
