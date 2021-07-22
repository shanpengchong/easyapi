package cn.easyutil.easyapi.logic;

import cn.easyutil.easyapi.filter.model.DefaultReadControllerApi;
import cn.easyutil.easyapi.entity.doc.ControllerBean;
import cn.easyutil.easyapi.filter.ReadControllerApiFilter;
import cn.easyutil.easyapi.util.ObjectUtil;
import cn.easyutil.easyapi.util.StringUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

public class ControllerCreator {

    private Class clazz;

    private ControllerBean controllerBean;

    //拦截器
    private ReadControllerApiFilter filter = new DefaultReadControllerApi();

    //控制器名称
    private String alias;

    private ControllerCreator(){}

    public static ControllerCreator builder(Class clazz){
        ControllerCreator creator = new ControllerCreator();
        creator.clazz = clazz;
        creator.alias = clazz.getSimpleName();
        return creator;
    }

    public ControllerCreator setFilter(ReadControllerApiFilter filter){
        if(filter == null){
            return this;
        }
        this.filter = filter;
        return this;
    }

    public ControllerCreator setAlias(String alias){
        if(StringUtil.isEmpty(alias)){
            return this;
        }
        this.alias = alias;
        return this;
    }

    public ControllerBean buildController(){
        if(controllerBean != null){
            return controllerBean;
        }
        try {
            create();
        }catch (Exception e){
            throw new RuntimeException("创建controller:"+clazz.getCanonicalName()+"失败,原因:"+e.getMessage());
        }
        return controllerBean;
    }

    private void create() {
        controllerBean = new ControllerBean();
        controllerBean.setController_comment(filter.readControllerAliasName(clazz));
        if(StringUtil.isEmpty(controllerBean.getController_comment())){
            controllerBean.setController_comment(alias);
        }
        controllerBean.setApi_path(readControllerApiPath(clazz));
        controllerBean.setController_java_name(controllerBean.getApi_path()+clazz.getCanonicalName());
        controllerBean.setController_comment_pinyin(StringUtil.toPinYin(controllerBean.getController_comment()));

    }

    private String readControllerApiPath(Class clazz) {
        Map<String, RequestMapping> classAnnotation = ObjectUtil.getClassAnnotation(clazz, RequestMapping.class);
        String path = "null-";
        if (!StringUtil.isEmpty(classAnnotation)) {
            return apiPathParseToFilePath(classAnnotation.entrySet().iterator().next().getValue().value()[0]);
        }
        Map<String, GetMapping> ga = ObjectUtil.getClassAnnotation(clazz, GetMapping.class);
        if (!StringUtil.isEmpty(ga)) {
            return apiPathParseToFilePath(ga.entrySet().iterator().next().getValue().value()[0]);
        }
        Map<String, PostMapping> pa = ObjectUtil.getClassAnnotation(clazz, PostMapping.class);
        if (!StringUtil.isEmpty(pa)) {
            return apiPathParseToFilePath(pa.entrySet().iterator().next().getValue().value()[0]);
        }
        return path;
    }

    /**
     * 请求路径转换为文件名
     *
     * @param path 请求路径
     * @return 文件名
     */
    private String apiPathParseToFilePath(String path) {
        if (StringUtil.isEmpty(path)) {
            return path;
        }
        path = path.replace("/", "-");
        if (!path.endsWith("-")) {
            return path + "-";
        }
        return path;
    }
}
