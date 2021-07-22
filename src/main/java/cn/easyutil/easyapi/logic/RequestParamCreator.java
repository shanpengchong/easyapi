package cn.easyutil.easyapi.logic;

import cn.easyutil.easyapi.entity.common.ApidocComment;
import cn.easyutil.easyapi.entity.common.RequestParameter;
import cn.easyutil.easyapi.filter.model.DefaultReadBeanApi;
import cn.easyutil.easyapi.filter.model.DefaultReadInterfaceApi;
import cn.easyutil.easyapi.filter.model.DefaultReadRequestParamApi;
import cn.easyutil.easyapi.entity.common.JavaType;
import cn.easyutil.easyapi.entity.doc.ParamBean;
import cn.easyutil.easyapi.filter.ReadBeanApiFilter;
import cn.easyutil.easyapi.filter.ReadInterfaceApiFilter;
import cn.easyutil.easyapi.filter.ReadRequestParamApiFilter;
import cn.easyutil.easyapi.parameterized.ParameterizedUtil;
import cn.easyutil.easyapi.util.StringUtil;
import org.springframework.core.DefaultParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 请求参数创建
 */
public class RequestParamCreator {

    private Map<String,Parameter> parameters = new HashMap<>();

    private Map<String, ParamBean> params = new HashMap<>();

    private Method method;

    private ReadBeanApiFilter filter = new DefaultReadBeanApi();

    private ReadInterfaceApiFilter inFilter = new DefaultReadInterfaceApi();

    private ReadRequestParamApiFilter reFilter = new DefaultReadRequestParamApi();

    private Map<String, String> comment = new HashMap<>();

    private ParamsBeanCreator paramsBeanCreator;

    private RequestParamCreator(){}

    public static RequestParamCreator builder(Method method,ParamsBeanCreator paramsBeanCreator){
        RequestParamCreator creator = new RequestParamCreator();
        DefaultParameterNameDiscoverer discover = new DefaultParameterNameDiscoverer();
        // 实际方法参数形参名称
        String[] parameterNames = discover.getParameterNames(method);
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameterNames.length; i++) {
            creator.parameters.put(parameterNames[i],parameters[i]);
        }
        creator.method = method;
        creator.paramsBeanCreator = paramsBeanCreator;
        return creator;
    }

    public RequestParamCreator setBeanFilter(ReadBeanApiFilter filter){
        if(filter == null){
            return this;
        }
        this.filter = filter;
        return this;
    }

    public RequestParamCreator setInterfaceFilter(ReadInterfaceApiFilter filter){
        if(filter == null){
            return this;
        }
        this.inFilter = filter;
        return this;
    }

    public RequestParamCreator setRequestParamFilter(ReadRequestParamApiFilter filter){
        if(filter == null){
            return this;
        }
        this.reFilter = filter;
        return this;
    }

    /**
     * 参数注释
     * @param comment   key:参数名  val:参数注释
     * @return
     */
    public RequestParamCreator setSourceComment(Map<String, String> comment){
        if(comment == null){
            return this;
        }
        //参数注释
        this.comment = comment;
        return this;
    }

    public Map<String, ParamBean> buildParamBean(){
        if(params.size() > 0){
            return params;
        }
        try {
            create();
        }catch (Exception e){
            System.out.println("创建入参:"+method.toGenericString()+"失败,原因:"+e.getMessage());
            e.printStackTrace();
        }
        return params;
    }

    private void create() {
        if(parameters.size() == 0){
            return ;
        }
        ApidocComment api = method.getDeclaredAnnotation(ApidocComment.class);
        Map<String,Class> requestParameterParse = new HashMap<>();
        if(api != null){
            //处理参数类型转换
            RequestParameter[] requestParameters = api.requestParameterParse();
            if(requestParameters!=null && requestParameters.length>0){
                for (RequestParameter parameter : requestParameters) {
                    if(!StringUtil.isEmpty(parameter.parameterName()) && !StringUtil.isEmpty(parameter.parameterType())){
                        requestParameterParse.put(parameter.parameterName(), parameter.parameterType());
                    }
                }
            }
        }
        Iterator<Map.Entry<String, Parameter>> iterator = parameters.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Parameter> next = iterator.next();
            String parameterName = next.getKey();
            Parameter parameter = next.getValue();
            //获取参数类型
            Type type = parameter.getParameterizedType();
            Class<?> aClass = parameter.getType();
            if(reFilter.ignore(aClass)){
                continue;
            }
            if(api!=null && api.ignore()){
                continue;
            }
            //处理参数被转换问题
            if(requestParameterParse.get(parameterName) != null){
                type = requestParameterParse.get(parameterName);
            }
            //组装参数
            ParamBean info = new ParamBean();
            info.setType(JavaType.getJavaType(type).getType());
            info.setRequired(1);
            info.setShow(1);
            info.setJava_name(parameter.getType().getCanonicalName());
            //设置参数名称
            String parameterName1 = getParameterName(parameter);
            info.setName(parameterName1==null?parameterName:parameterName1);
            info.setRemark(getParameterComment(parameter,parameterName));
            //mock
            info.setOldMockValue(inFilter.readRequestParamMockValue(parameter));
            //mock
            info.setMockValue(filter.parseMock(info.getOldMockValue()));
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

    private String getParameterName(Parameter parameter){
        //获取参数名称
        String paramName = inFilter.readRequestParamName(parameter);
        if (StringUtil.isEmpty(paramName)) {
            return null;
        }
        return paramName;
    }

    private String getParameterComment(Parameter parameter,String parameterName){
        //获取参数说明
        String comment = inFilter.readRequestParamComments(parameter);
        if (StringUtil.isEmpty(comment)) {
            //获取参数注释的说明
            comment = this.comment.get(parameterName);
            if(comment == null){
                return parameterName;
            }
        }
        return comment;
    }
}
