package cn.easyutil.easyapi.javadoc.html;

import cn.easyutil.easyapi.datasource.EasyapiBindSqlExecution;
import cn.easyutil.easyapi.datasource.bean.EasyapiBindSQLExecuter;
import cn.easyutil.easyapi.entity.doc.*;
import cn.easyutil.easyapi.util.JsonUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 生成可供访问的静态页面
 */
public class CreateHtml {
    //页面
    private StringBuffer html = new StringBuffer();
    //js
    private StringBuffer js = new StringBuffer();

    private EasyapiBindSqlExecution sqlExecution;

    public CreateHtml(EasyapiBindSqlExecution sqlExecution){
        this.sqlExecution = sqlExecution;
    }

    public void createHtml(){

    }

    public CreateHtml createJs(Long projectId){
        EasyapiBindSQLExecuter build = EasyapiBindSQLExecuter.build(new InfoBean());
        if(projectId != null){
            build.eq(InfoBean::getId, projectId  );
        }else{
            build.eq(InfoBean::getInitProject, 1);
        }
        InfoBean infoBean = sqlExecution.selectOne(build);
        if(infoBean == null){
            return null;
        }
        projectId = infoBean.getId();
        //先定义控制器列表
        List<ControllerBean> controllerBeans = sqlExecution.select(EasyapiBindSQLExecuter.build(new ControllerBean()).eq(ControllerBean::getProject_id, projectId));
        //接口列表
        List<InterfaceBean> interfaces = sqlExecution.select(EasyapiBindSQLExecuter.build(new InterfaceBean()).eq(InterfaceBean::getProject_id, projectId));
        //请求参数列表
        List<InterfaceParamBean> requests = sqlExecution.select(EasyapiBindSQLExecuter.build(new InterfaceParamBean()).eq(InterfaceParamBean::getProjectId, projectId).eq(InterfaceParamBean::getType, 0));;
        //响应参数列表
        List<InterfaceParamBean> responses = sqlExecution.select(EasyapiBindSQLExecuter.build(new InterfaceParamBean()).eq(InterfaceParamBean::getProjectId, projectId).eq(InterfaceParamBean::getType, 1));;
        //响应数据mock列表
        List<MockBean> mocks = sqlExecution.select(EasyapiBindSQLExecuter.build(new MockBean()).eq(MockBean::getProjectId, projectId));;


        //接口文档信息
        js.append("function getInfo(){");
        js.append("return "+ JsonUtil.beanToJson(infoBean)+";");
        js.append("}");
        js.append(newLine());

        //拼接控制器列表
        js.append("function getControllers(){");
            js.append("return "+ JsonUtil.beanToJson(controllerBeans)+";");
        js.append("}");
        js.append(newLine());


        //再定义获取接口信息的方法
        js.append("function getInterfaces(controllerId){");
            js.append("switch(controllerId){");
            //循环添加全部接口
            Map<Long, List<InterfaceBean>> interfaceMap = interfaces.stream().collect(Collectors.groupingBy(InterfaceBean::getController_id));
            interfaceMap.keySet().forEach(im -> {
                js.append("case "+im+": ");
                js.append("return "+ JsonUtil.beanToJson(interfaceMap.get(im))+";");
            });
            js.append("}");
        js.append("}");
        js.append(newLine());


        //定义获取接口参数信息的方法
        js.append("function getRequests(interfaceId){");
            js.append("switch(interfaceId){");
            Map<Long, List<InterfaceParamBean>> reqMap = requests.stream().collect(Collectors.groupingBy(InterfaceParamBean::getInterfaceId));
            reqMap.keySet().forEach(reqm -> {
                js.append("case "+reqm+": ");
                js.append("return "+ JsonUtil.beanToJson(reqMap.get(reqm))+";");
            });
            js.append("}");
        js.append("}");
        js.append(newLine());

        //定义获取接口返回参数信息的方法
        js.append("function getResponses(interfaceId){");
            js.append("switch(interfaceId){");
            Map<Long, List<InterfaceParamBean>> resMap = responses.stream().collect(Collectors.groupingBy(InterfaceParamBean::getInterfaceId));
            reqMap.keySet().forEach(resm -> {
                js.append("case "+resm+": ");
                js.append("return "+ JsonUtil.beanToJson(resMap.get(resMap))+";");
            });
            js.append("}");
        js.append("}");
        js.append(newLine());

        //定义获取接口返回mock的方法
        js.append("function getMocks(interfaceId){");
            js.append("switch(interfaceId){");
            Map<Long, List<MockBean>> mockMap = mocks.stream().collect(Collectors.groupingBy(MockBean::getInterfaceId));
            mockMap.keySet().forEach(mm -> {
                js.append("case "+mm+": ");
                js.append("return "+ JsonUtil.beanToJson(mockMap.get(mm))+";");
            });
            js.append("}");
        js.append("}");
        js.append(newLine());

        return this;
    }

    public String getJs(){
        return js.toString();
    }


    private String newLine(){
        return "\r\n";
    }


}
