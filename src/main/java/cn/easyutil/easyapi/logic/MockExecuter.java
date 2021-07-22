package cn.easyutil.easyapi.logic;

import cn.easyutil.easyapi.filter.model.DefaultReadBeanApi;
import cn.easyutil.easyapi.entity.common.JavaType;
import cn.easyutil.easyapi.entity.doc.ParamBean;
import cn.easyutil.easyapi.filter.ReadBeanApiFilter;
import cn.easyutil.easyapi.util.JsonUtil;
import cn.easyutil.easyapi.util.StringUtil;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

/**
 * 根据入参或返回mock数据
 */
public class MockExecuter {

    //mock模板解析
    private ReadBeanApiFilter filter;
    //显示属性是否mock
    private boolean mockShow = true;
    //隐藏属性是否mock
    private boolean mockHidden = false;
    //必填属性是否mock
    private boolean mockRequired = true;
    //非必填属性是否mock
    private boolean mockUnRequired = true;

    public MockExecuter(ReadBeanApiFilter filter){
        this.filter = filter;
    }

    public ReadBeanApiFilter readBeanApiFilter(){
        if(this.filter == null){
            filter = new DefaultReadBeanApi();
        }
        return filter;
    }

    /**
     * 根据入参或返回的json参数mock数据
     * @param jsonParam 模板json
     */
    public Object mockByApiBean(String jsonParam){
        if(StringUtil.isEmpty(jsonParam) || !JsonUtil.isJson(jsonParam)){
            return mockVoid();
        }
        return mockByApiBean(JSONObject.parseObject(jsonParam,Map.class));
    }

    /**
     * 根据api模板创建mock值
     * @param obj
     * @return
     */
    public Object mockByApiBean(Map<String, ParamBean> obj){
        if(obj==null || obj.isEmpty()){
            return mockVoid();
        }
        //最终map返回
        Map<String,Object> result = new HashMap<>();
        //最终map返回
        Map<String,Object> result1 = new HashMap<>();
        //最终List返回
        List<Object> resultList = new ArrayList<>();
        Iterator<Map.Entry<String, ParamBean>> iterator = obj.entrySet().iterator();
        //是否集合返回
        boolean isArray = false;
        while (iterator.hasNext()){
            Map.Entry<String, ParamBean> next = iterator.next();
            String key = next.getKey();
            ParamBean bean = JsonUtil.jsonToBean(JsonUtil.beanToJson(next.getValue()), ParamBean.class);
            if(!isMock(bean.getShow(),bean.getRequired())){
                continue;
            }
            JavaType javaType = JavaType.getType(bean.getType());
            //先处理不需要复杂逻辑的数据
            if(key.equals(ParamsBeanCreator.nullKey) && javaType!=JavaType.ArrayObject){
                return mockBase(bean);
            }
            if(key.equals(ParamsBeanCreator.nullKey)){
                isArray = true;
            }
            mockMapResult(key,bean,result);
            mockMapResult(key,bean,result1);
        }
        Object returnResult = result;
        Object returnResult1 = result1;
        if(result.size()>0 && result.get(ParamsBeanCreator.nullKey)!=null){
            //集合返回值
            returnResult = result.get(ParamsBeanCreator.nullKey);
            List arrayResult = (List) returnResult;
            List arrayResult1 = (List) result1.get(ParamsBeanCreator.nullKey);
            arrayResult.add(arrayResult1.get(0));
            resultList = arrayResult;
            //去除全部的nullKey
            List<Object> fr = new ArrayList<>();
            clearNullKey(resultList,fr);
            resultList = fr;
        }
        if(isArray){
            return resultList;
        }
        return returnResult;
    }

    private void clearNullKey(List<Object> resultList,List<Object> fr) {

        for (Object o : resultList) {
            if(!(o instanceof Map)){
                fr.add(o);
                continue;
            }
            Map<String,Object> map = (Map<String, Object>) o;
            if(map.get(ParamsBeanCreator.nullKey)==null){
                fr.add(o);
                continue;
            }
            List<Object> frv = new ArrayList<>();
            fr.add(frv);
            if(Collection.class.isAssignableFrom(map.get(ParamsBeanCreator.nullKey).getClass())){
                clearNullKey((List<Object>) map.get(ParamsBeanCreator.nullKey),frv);
            }
        }
    }


    /**
     * 本方法参与递归
     * @param paramKey  当前返回值key名字
     * @param bean  当前返回值类
     * @param result    递归传递的返回结果
     */
    private void mockMapResult(String paramKey, ParamBean bean, Map<String,Object> result){
        if(StringUtil.isEmpty(paramKey) || StringUtil.isEmpty(bean.getType())){
            return;
        }
        JavaType javaType = JavaType.getType(bean.getType());
        if(JavaType.isBaseType(javaType) || javaType==JavaType.Array){
            result.put(paramKey,mockBase(bean));
            return;
        }
        switch (javaType){
            case Object:{
                //获取对象全部属性
                Map<String, ParamBean> childrenField = (Map<String, ParamBean>) bean.getChildren();
                Map<String,Object> childMockValue = new HashMap<>();
                keyIterator(childrenField,childMockValue);
                result.put(paramKey,childMockValue);
                break;
            }
            case ArrayObject:{
                //处理集合对象类型
                List<Map<String,Object>> childMockValueList = new ArrayList<>();
                List<Object> list = (List<Object>) bean.getChildren();
                if(list.size() == 0){
                    break;
                }
                Map<String, ParamBean> childrenField = (Map<String, ParamBean>) list.get(0);
                Map<String,Object> childMockValue = new HashMap<>();
                Map<String,Object> childMockValue1 = new HashMap<>();
                keyIterator(childrenField,childMockValue);
                keyIterator(childrenField,childMockValue1);
                childMockValueList.add(childMockValue);
                childMockValueList.add(childMockValue1);
                result.put(paramKey,childMockValueList);
                break;
            }
        }
    }

    private void keyIterator(Map<String, ParamBean> childrenField , Map<String,Object> childMockValue) {
        Iterator<Map.Entry<String, ParamBean>> iterator = childrenField.entrySet().iterator();
        while (iterator.hasNext()){
            //遍历对象属性
            Map.Entry<String, ParamBean> next = iterator.next();
            String key = next.getKey();
            ParamBean value = JsonUtil.parse(next.getValue(), ParamBean.class);
            if(!isMock(value.getShow(),value.getRequired())){
                continue;
            }
            mockMapResult(key,value,childMockValue);
        }
    }

    /**
     * 处理无复杂逻辑的返回
     * @param bean
     * @return
     */
    private Object mockBase(ParamBean bean){
        if(!isMock(bean.getShow(),bean.getRequired())){
            return mockVoid();
        }
        JavaType javaType = JavaType.getType(bean.getType());
        //1.基本类型返回
        //2.基本类型数组返回
        //3.map
        //4.map数组
        //取出数组内的类型
        boolean isArray = false;
        if(javaType==JavaType.Array){
            isArray = true;
            if(bean.getChildren() == null){
                return mockVoid();
            }
            List<Object> childrens = (List<Object>) bean.getChildren();
            Map<String,Object> children = JsonUtil.jsonToMap(JsonUtil.beanToJson(childrens.get(0)));
            Object obj = children.entrySet().iterator().next().getValue();
            Integer childrenType = JsonUtil.parse(obj, ParamBean.class).getType();
            javaType = JavaType.getType(childrenType);
        }
        Object result = mockVoid();
        List<Object> resultList = new ArrayList<>();
        //获取mock模板
        String mockTemplate = bean.getOldMockValue();
        if(StringUtil.isEmpty(mockTemplate)){
            mockTemplate = "";
        }
        //处理返回的基本类型值
        switch (javaType){
            case File:
            case Map:{
                resultList.add(result);
                resultList.add(result);
                break;
            }
            case Character:
            case String:{
                String mockValue = readBeanApiFilter().parseMock(mockTemplate);
                result = StringUtil.isEmpty(mockValue)?StringUtil.getRandomString(4):mockValue;
                resultList.add(result);
                mockValue = readBeanApiFilter().parseMock(mockTemplate);
                resultList.add(StringUtil.isEmpty(mockValue)?StringUtil.getRandomString(4):mockValue);
                break;
            }
            case Integer:
            case Byte:
            case Short:
            case Long:{
                String mockValue = readBeanApiFilter().parseMock(mockTemplate);
                result = StringUtil.isEmpty(mockValue)?new Random().nextInt(2):Long.valueOf(mockValue);
                resultList.add(result);
                mockValue = readBeanApiFilter().parseMock(mockTemplate);
                resultList.add(StringUtil.isEmpty(mockValue)?new Random().nextInt(2):Long.valueOf(mockValue));
                break;
            }
            case Boolean:{
                result = true;
                resultList.add(true);
                resultList.add(false);
                break;
            }
            case Float:
            case Double:{
                String mockValue = readBeanApiFilter().parseMock(mockTemplate);
                result = StringUtil.isEmpty(mockValue)?new Random().nextInt(3)+new Random().nextDouble():Double.valueOf(mockValue);
                resultList.add(result);
                mockValue = readBeanApiFilter().parseMock(mockTemplate);
                resultList.add(StringUtil.isEmpty(mockValue)?new Random().nextInt(3)+new Random().nextDouble():Double.valueOf(mockValue));
                break;
            }
        }
        if(!isArray){
            //非数组直接返回结果
            return result;
        }
        //数组则返回相同的两条val
        return resultList;
    }

    /**
     * 无参
     * @return
     */
    private static Object mockVoid(){
        return new Object();
    }

    /**
     * 是否参与mock
     * @param show
     * @param required
     * @return
     */
    private boolean isMock(Integer show,Integer required){
        if(show==null && required==null){
            return false;
        }
        if(required!=null){
            if(required > 0){
                return mockRequired;
            }
            return mockUnRequired;
        }
        if(show > 0){
            return mockShow;
        }
        return mockHidden;
    }

    public boolean isMockShow() {
        return mockShow;
    }

    public MockExecuter setMockShow(boolean mockShow) {
        this.mockShow = mockShow;
        return this;
    }

    public boolean isMockHidden() {
        return mockHidden;
    }

    public MockExecuter setMockHidden(boolean mockHidden) {
        this.mockHidden = mockHidden;
        return this;
    }

    public boolean isMockRequired() {
        return mockRequired;
    }

    public MockExecuter setMockRequired(boolean mockRequired) {
        this.mockRequired = mockRequired;
        return this;
    }

    public boolean isMockUnRequired() {
        return mockUnRequired;
    }

    public MockExecuter setMockUnRequired(boolean mockUnRequired) {
        this.mockUnRequired = mockUnRequired;
        return this;
    }

//        public static void main(String[] args) {
//        List<File> list = new ArrayList<>();
//        FileUtil.fetchFileList(new File("D:/self/easyapi/easyapi/test-api-apidoc/api-response-param"),list);
//        for (File file : list) {
//            if(!file.getName().toLowerCase().contains("testrescontroller")){
//                continue;
//            }
//            String content = FileUtil.getFileContent(file);
//            System.out.println("方法："+file.getName()+"的mock值为");
//            MockExecuter executer = new MockExecuter(new DefaultReadBeanApi());
//            Object result = executer.mockByApiBean(content);
//            System.out.println(JsonUtil.beanToJson(result));
//            System.out.println();
//        }
//    }

}
