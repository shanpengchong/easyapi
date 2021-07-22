package cn.easyutil.easyapi.logic;

import cn.easyutil.easyapi.datasource.EasyapiBindSqlExecution;
import cn.easyutil.easyapi.datasource.bean.EasyapiBindSQLExecuter;
import cn.easyutil.easyapi.datasource.sql.CreateTable;
import cn.easyutil.easyapi.entity.auth.AuthMoudle;
import cn.easyutil.easyapi.entity.auth.User;
import cn.easyutil.easyapi.entity.auth.UserAuth;
import cn.easyutil.easyapi.entity.auth.UserProject;
import cn.easyutil.easyapi.entity.doc.*;
import cn.easyutil.easyapi.entity.read.MethodAlias;
import cn.easyutil.easyapi.interview.controller.ApiDocController;
import cn.easyutil.easyapi.interview.entity.ResponseBody;
import cn.easyutil.easyapi.javadoc.JavaFileReader;
import cn.easyutil.easyapi.configuration.EasyapiConfiguration;
import cn.easyutil.easyapi.configuration.PathContext;
import cn.easyutil.easyapi.util.JsonUtil;
import cn.easyutil.easyapi.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Stream;

public class Starter {

    /**
     * 项目配置文件
     */
    private EasyapiConfiguration configuration;

    private ApplicationContext appContext;

    private EasyapiBindSqlExecution execution;

    private Long projectId;

    public Starter(EasyapiConfiguration configuration, ApplicationContext appContext, EasyapiBindSqlExecution execution){
        this.configuration = configuration;
        this.appContext = appContext;
        this.execution = execution;
    }

    /**
     * 项目初始化后自动推送当前接口文档
     */
    public void aotoSync() {
        User user = new User();
        user.setUsername(configuration.getLoginUserName());
        user.setPassword(configuration.getLoginPassword());
        //如果没有用户信息，不能同步
        if (user == null || StringUtil.isEmpty(user.getUsername()) || StringUtil.isEmpty(user.getPassword())) {
            return;
        }
        //获取配置的同步地址
        String targetAd = configuration.getSyncAddress();
        Integer syncPort = configuration.getSyncPort();
        if (StringUtil.isEmpty(targetAd)) {
            return;
        }
        //如果包含端口，则添加端口
        if (!StringUtil.isEmpty(syncPort) && syncPort != 0) {
            targetAd += ":" + syncPort;
        }
        ResponseBody body = new ApiDocController().manualSyncAll(targetAd, user.getUsername(), user.getPassword());
        if (!StringUtil.isEmpty(body) && body.getCode() == 500) {
            throw new RuntimeException(body.getRemark());
        }

    }

    /**
     * 使用文档
     */
    public void start() {
        //初始化文件
//        PathContext.init(configuration);
        //查找本项目的项目id
        InfoBean bean = execution.selectOne(EasyapiBindSQLExecuter.build(new InfoBean()).eq(InfoBean::getInitProject,1));
        if(bean == null){
            bean = addDefaultProject();
            addDefaultUsers();
        }
        projectId = bean.getId();
        //判断是否需要删除数据库
        if(configuration.isDelOldApi()){
            execution.update(CreateTable.clearTableNotUser(projectId));
            UserAuth ua = new UserAuth();
            bean = addDefaultProject();
            execution.update("update easyapi_user_auth set project_id=? where project_id=?",bean.getId(),projectId);
            execution.update("update easyapi_user_projects set project_id=? where project_id=?",bean.getId(),projectId);
            projectId = bean.getId();
        }
        if(configuration.isDelOldUsers()){
            //删除旧用户
            execution.update(CreateTable.clearUsers(projectId));
            //添加默认账号
            addDefaultUsers();
        }
        // 重新扫描生成文件
        if (configuration.isRescan()) {
            // 创建api文档
            doCreateApi();
        }
        //同步接口文档
        if (configuration.isSync()) {
            aotoSync();
        }
        EasyapiRun.initProject = bean;
//        // 对接口数据返回进行mock
//        createApiResponseMock();
        System.out.println("*********************************************");
        System.out.println("**********   接口文档已生成 url=/apidoc.html  ***************");
        System.out.println("*********************************************");
    }

    /**
     * 初始化项目
     */
    private InfoBean addDefaultProject() {
        //初始化项目
        InfoBean bean = new InfoBean();
        bean.setTitle(configuration.getProjectName());
        if(StringUtil.isEmpty(bean.getTitle())){
            bean.setTitle("easyapi @ApiComment(mock=) 注解的表达式");
        }
        bean.setRemark(PathContext.javaMockRemark());
        bean.setInitProject(1);
        execution.insert(bean);
        //初始化项目环境
        HostBean host = new HostBean();
        host.setProjectId(bean.getId());
        host.setName("本地");
        String address = "localhost";
        try {
            address = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
        }
        host.setAddress(address);
        String port = appContext.getEnvironment().getProperty("server.port");
        host.setPort(Integer.valueOf(port));
        host.setHost("http://"+address+":"+port);
        execution.insert(host);
        //添加mock
        HostBean mock = new HostBean();
        BeanUtils.copyProperties(host,mock);
        mock.setName("mock");
        mock.setId(null);
        mock.setHost("http://localhost:"+port+"/easyapi/doc/mock?path=");
        execution.insert(mock);

        //添加返回包装
        List<MockOutPackageBean> outPackageBeans = PathContext.initOutPackageFile();
        for (MockOutPackageBean outPackageBean : outPackageBeans) {
            outPackageBean.setProjectId(bean.getId());
        }
        execution.insert(outPackageBeans);
        return bean;
    }

    /**
     * 添加默认用户
     */
    private void addDefaultUsers() {
        //数据库添加默认用户
        User root = new User();
        root.setSuperAdmin(1);
        root.setUsername(configuration.getLoginUserName());
        root.setPassword(StringUtil.toMD5(configuration.getLoginPassword()));
        root.setRemark("超级管理员");
        execution.insert(root);

        User test = new User();
        test.setUsername("test");
        test.setPassword(StringUtil.toMD5("test"));
        test.setRemark("测试账号");
        execution.insert(test);

        //添加测试人员的权限
        List<Integer> defaultAuthCodes = AuthMoudle.getDefaultAuthCodes();
        for (Integer code : defaultAuthCodes) {
            UserAuth auth = new UserAuth();
            auth.setUserId(test.getId());
            auth.setAuthCode(code);
            auth.setProjectId(projectId);
            execution.insert(auth);
        }

        //添加测试人员对应的项目
        UserProject project = new UserProject();
        project.setProjectId(projectId);
        project.setUserId(test.getId());
        execution.insert(project);


    }


    private void doCreateApi() {
        Set<Class> set = configuration.getControllerApiFilter().readControllers(appContext);
        if (set == null || set.isEmpty()) {
            return;
        }

//        // 存放controller列表的文件内容
//        List<ControllerBean> apiControllerBeans = new ArrayList<>();
//        //如果不删除旧的文件，则取出旧文件进行追加
//        if (!configuration.isDelOldApi()) {
//            String apiControllersContent = FileUtil.getFileContent(new File(PathContext.apiControllerPath));
//            apiControllerBeans = JsonUtil.jsonToList(apiControllersContent, ControllerBean.class);
//            if (apiControllerBeans == null) {
//                apiControllerBeans = new ArrayList<>();
//            }
//        }

        ParamsBeanCreator creator = ParamsBeanCreator.builder(configuration);
        Iterator<Class> iterator = set.iterator();
        //解析全部接口
        while (iterator.hasNext()) {
            Class aClass = iterator.next();
            String canonicalName = aClass.getCanonicalName();
            //如果设置了扫描包并且当前类不属于该包，则忽略该类
            if (!StringUtil.isEmpty(configuration.getScanPackage()) && !canonicalName.startsWith(configuration.getScanPackage())) {
                continue;
            }
            //如果设置了忽略则跳过
            if (configuration.getControllerApiFilter().readControllerIgnore(aClass)) {
                continue;
            }
            //获取controller源文件路径
            String controllerPath = PathContext.projectBasePath + File.separator
                    + aClass.getCanonicalName().replace(".", File.separator) + ".java";
            //构建controller
            ControllerBean controllerBean = ControllerCreator.builder(aClass)
                    .setFilter(configuration.getControllerApiFilter())
                    .setAlias(JavaFileReader.readClassComment(new File(controllerPath)))
                    .buildController();

            //如果数据库中不存在则添加到数据库
            ControllerBean queryController = new ControllerBean();
            queryController.setProject_id(projectId);
            queryController.setController_java_name(canonicalName);
            queryController = execution.selectOne(queryController);
            if(queryController == null){
                controllerBean.setController_java_name(canonicalName);
                controllerBean.setProject_id(projectId);
                execution.insert(controllerBean);
                queryController = controllerBean;
            }



//            //完成控制器组装
//            apiControllerBeans.add(controllerBean);

            // 存放interface列表的文件内容
            InterfaceBean queryInterface = new InterfaceBean();
            queryInterface.setProject_id(projectId);
            queryInterface.setController_id(queryController.getId());
            List<InterfaceBean> apiInterfaceBeans = execution.select(queryInterface);
//            String interfaceContent = FileUtil.getFileContent(new File(PathContext.apiInterfacePath+File.separator+controllerBean.getController_java_name()));
//            apiInterfaceBeans = JsonUtil.jsonToList(interfaceContent, InterfaceBean.class);
            if (apiInterfaceBeans == null) {
                apiInterfaceBeans = new ArrayList<>();
            }

            //获取全部接口方法
            List<Method> methods = getApiInterfaceMethod(aClass);

            // 从源文件中获取所有的方法名称
            File sourceFile = new File(PathContext.projectBasePath + aClass.getCanonicalName().replace(".", File.separator) + ".java");
            List<MethodAlias> aliases = JavaFileReader.readMethodAliasName(sourceFile);

            //从源文件读取所有的方法参数说明
            Map<String, Map<String, String>> readMethodParamComment = JavaFileReader.readMethodParamComment(sourceFile);

            m:
            for (Method method : methods) {
                //如果旧的文件中包含该接口，则不进行重新解析
                for (InterfaceBean in : apiInterfaceBeans) {
                    if (in.getJavaName().equals(method.toGenericString())) {
                        continue m;
                    }
                }
                //如果接口设置忽略则跳过
                if (configuration.getInterfaceApiFilter().readInterfaceIgnore(method)) {
                    continue;
                }


                String alias = method.getName();
                if(aliases.size() > 0){
                    Optional<MethodAlias> option = aliases.stream().filter(a -> a.getMethodName().equals(method.getName())).findFirst();
                    if(option != null){
                        alias = option.get().getMethodAliasName();
                    }
                }

                //构建接口
                InterfaceBean interfaceBean = InterfaceCreator.builder(method)
                        .setFilter(configuration.getInterfaceApiFilter())
                        .setAlias(alias)
                        .buildInterface();
                interfaceBean.setRequest_url(configuration.getControllerApiFilter().readControllerRequestPath(aClass) + interfaceBean.getRequest_url());
                String paramName = controllerBean.getApi_path() + aClass.getCanonicalName() + "." + method.getName() + ".json";
                interfaceBean.setRequest_param_name(paramName);
                interfaceBean.setResponse_param_name(paramName);

                //将接口添加到数据库
                interfaceBean.setProject_id(projectId);
                interfaceBean.setController_id(controllerBean.getId());
                interfaceBean.setResponse_param_name(canonicalName+method.getName()+method.getParameters().length);
                interfaceBean.setRequest_param_name(canonicalName+method.getName()+method.getParameters().length);
                execution.insert(interfaceBean);


                //完成接口组装
                apiInterfaceBeans.add(interfaceBean);
                //构建请求参数
                Map<String, ParamBean> requestParam = RequestParamCreator.builder(method,creator)
                        .setBeanFilter(configuration.getBeanApiFilter())
                        .setInterfaceFilter(configuration.getInterfaceApiFilter())
                        .setRequestParamFilter(configuration.getRequestParamApiFilter())
                        .setSourceComment(readMethodParamComment.get(method.getName()))
                        .buildParamBean();

                //将参数存储数据库
                String requestParams = JsonUtil.beanToJson(requestParam);
                InterfaceParamBean reqPram = new InterfaceParamBean();
                reqPram.setType(0);
                reqPram.setProjectId(projectId);
                reqPram.setInterfaceId(interfaceBean.getId());
                reqPram.setParamsJson(requestParams);
                execution.insert(reqPram);

//                //将入参写入文件
//                FileUtil.write(new File(PathContext.apiRequestParamPath + File.separator + controllerBean.getApi_path() + aClass.getCanonicalName() + "."
//                        + method.getName() + ".json"),requestParams );


                //构建返回值
                Map<String, ParamBean> responseParam = ResponseParamCreator.builder(method,creator)
                        .setFilter(configuration.getResponseParamApiFilter())
                        .buildParamBean();
                String res = JsonUtil.beanToJson(responseParam);
                //将返回值写入数据库
                InterfaceParamBean resParam = new InterfaceParamBean();
                resParam.setType(1);
                resParam.setProjectId(projectId);
                resParam.setInterfaceId(interfaceBean.getId());
                resParam.setParamsJson(res);
                execution.insert(resParam);

//                //将返回参数写入文件
//                FileUtil.write(new File(PathContext.apiResponseParamPath + File.separator + controllerBean.getApi_path() + aClass.getCanonicalName() + "."
//                        + method.getName() + ".json"), res);

                if(responseParam==null || responseParam.size()==0){
                    continue ;
                }
                //构建返回参数mock
                try {
                    String mock = buildMock(res);
                    //将mock写入数据库
                    MockBean mockBean = new MockBean();
                    mockBean.setProjectId(projectId);
                    mockBean.setInterfaceId(interfaceBean.getId());
                    mockBean.setMock(mock);
                    execution.insert(mockBean);

//                    FileUtil.write(new File(PathContext.apiResponseMockPath + interfaceBean.getResponse_param_name()),
//                            JsonUtil.beanToJson(mock));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("response mock error:" + interfaceBean.getController_name() + "." + interfaceBean.getJavaName());
                }

            }
//            //生成接口文件
//            String filePath = PathContext.apiInterfacePath + File.separator + controllerBean.getApi_path() + aClass.getCanonicalName() + ".json";
//            FileUtil.write(new File(filePath), JsonUtil.beanToJson(apiInterfaceBeans));
        }
//        // 生成controller列表文件
//        apiControllerBeans.sort(Comparator.comparing(ControllerBean::getController_comment_pinyin));
//        FileUtil.write(new File(PathContext.apiControllerPath), JsonUtil.beanToJson(apiControllerBeans));
    }

    private List<Method> getApiInterfaceMethod(Class aClass) {

        List<Method> result = new ArrayList<Method>();
        Method[] methods = aClass.getDeclaredMethods();
        for (Method method : methods) {
            Annotation[] ans = method.getDeclaredAnnotations();
            for (Annotation an : ans) {
                if (an instanceof RequestMapping) {
                    result.add(method);
                    continue;
                } else if (an instanceof GetMapping) {
                    result.add(method);
                    continue;
                } else if (an instanceof PostMapping) {
                    result.add(method);
                    continue;
                } else if (an instanceof PutMapping) {
                    result.add(method);
                    continue;
                } else if (an instanceof DeleteMapping) {
                    result.add(method);
                    continue;
                }
            }
        }
        return result;
    }

    private String buildMock(String responseParam){
        MockExecuter executer = new MockExecuter(configuration.getBeanApiFilter());
        executer.setMockHidden(configuration.getResponseParamApiFilter().isMockHiddenParam(true));
        executer.setMockShow(configuration.getResponseParamApiFilter().isMockHiddenParam(false));
        executer.setMockRequired(configuration.getResponseParamApiFilter().isMockRequiredParam(true));
        executer.setMockUnRequired(configuration.getResponseParamApiFilter().isMockRequiredParam(false));
        Object mockValue = executer.mockByApiBean(responseParam);
        return JsonUtil.beanToJson(mockValue);
    }

//    private void createApiResponseMock() {
//        List<File> fileList = new ArrayList<File>();
//        FileUtil.fetchFileList(new File(PathContext.apiInterfacePath), fileList);
//        for (File file : fileList) {
//            List<InterfaceBean> beans = JsonUtil.jsonToList(FileUtil.getFileContent(file),
//                    InterfaceBean.class);
//            if (beans == null || beans.size() == 0) {
//                continue;
//            }
//            for (InterfaceBean bean : beans) {
//                String response_param_name = bean.getResponse_param_name();
//                // 获取到返回参数
//                String fileContent = FileUtil
//                        .getFileContent(new File(PathContext.apiResponseParamPath + response_param_name));
////				createApiResponseMockByJson(fileContent, response_param_name);
//                MockExecuter executer = new MockExecuter(configuration.getBeanApiFilter());
//                executer.setMockHidden(configuration.getResponseParamApiFilter().isMockHiddenParam(true));
//                executer.setMockShow(configuration.getResponseParamApiFilter().isMockHiddenParam(false));
//                executer.setMockRequired(configuration.getResponseParamApiFilter().isMockRequiredParam(true));
//                executer.setMockUnRequired(configuration.getResponseParamApiFilter().isMockRequiredParam(false));
//                try {
//                    Object mockValue = executer.mockByApiBean(fileContent);
//                    FileUtil.write(new File(PathContext.apiResponseMockPath + response_param_name),
//                            JsonUtil.beanToJson(mockValue));
//                } catch (Exception e) {
//                    throw new RuntimeException("response mock error:" + bean.getController_name() + "." + bean.getJavaName(), e);
//                }
//            }
//        }
//    }
}
