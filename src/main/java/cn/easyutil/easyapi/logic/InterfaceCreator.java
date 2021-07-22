package cn.easyutil.easyapi.logic;

import cn.easyutil.easyapi.filter.model.DefaultReadInterfaceApi;
import cn.easyutil.easyapi.entity.doc.InterfaceBean;
import cn.easyutil.easyapi.filter.ReadInterfaceApiFilter;
import cn.easyutil.easyapi.util.SpringUtil;
import cn.easyutil.easyapi.util.StringUtil;

import java.lang.reflect.Method;

/**
 * 接口创建
 */
public class InterfaceCreator {

    private Method method;

    private InterfaceBean interfaceBean;

    private ReadInterfaceApiFilter filter = new DefaultReadInterfaceApi();

    private String alias;

    private InterfaceCreator(){}

    public static InterfaceCreator builder(Method method){
        InterfaceCreator creator = new InterfaceCreator();
        creator.method = method;
        creator.alias = method.getName();
        return creator;
    }

    public InterfaceCreator setFilter(ReadInterfaceApiFilter filter){
        if(filter == null){
            return this;
        }
        this.filter = filter;
        return this;
    }

    public InterfaceCreator setAlias(String alias){
        if(StringUtil.isEmpty(alias)){
            return this;
        }
        this.alias =  alias;
        return this;
    }

    public InterfaceBean buildInterface(){
        if(interfaceBean != null){
            return interfaceBean;
        }
        try {
            create();
        }catch (Exception e){
            System.out.println("创建接口:"+method.toGenericString()+"失败,原因:"+e.getMessage());
            e.printStackTrace();
        }
        return interfaceBean;
    }

    /**
     * 创建接口说明
     */
    private void create() {
        interfaceBean = new InterfaceBean();
        interfaceBean.setTitle(filter.readInterfaceAliasName(method));
        //设置接口名称
        if(StringUtil.isEmpty(interfaceBean.getTitle())){
            interfaceBean.setTitle(alias);
        }
        interfaceBean.setController_name(method.getDeclaringClass().getCanonicalName());
        interfaceBean.setJavaName(method.getName());
        interfaceBean.setRemark(filter.readInterfaceComments(method));
        interfaceBean.setRequest_method(SpringUtil.getRequestMethod(method));
        //外面添加
        interfaceBean.setRequest_param_name(null);
        interfaceBean.setRequest_param_remark(filter.readRequestNotes(method));
        interfaceBean.setRequest_type(filter.readInterfaceBodyType(method).getType());
        interfaceBean.setRequest_url(filter.readInterfaceRequestPath(method));
        //忘记什么用处了
//        interfaceBean.setResponse_is_array(0);
        //外面添加
        interfaceBean.setResponse_param_name(null);
        interfaceBean.setResponse_param_remark(filter.readResponseNotes(method));
    }
}
