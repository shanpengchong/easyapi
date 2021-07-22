package cn.easyutil.easyapi.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.List;
import java.util.Map;

/** JSON帮助类 */
public class JsonUtil {
	
	/** 将JSON字符串转换为map */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jsonToMap(String json) {
		Map<String, Object> map = (Map<String, Object>) JSONObject.parse(json);
		return map;
	}
	/** 将obj转换成map */
	public static Map<String, Object> beanToMap(Object obj) {
		return jsonToMap(beanToJson(obj));
	}
	
	/** 将map转换成obj */
	public static <T>T mapToBean(Map<String, Object> map, Class<T> beanClass) {
//		return jsonToMap(objToJson(obj));
		return jsonToBean(beanToJson(map), beanClass);
	}
	
	/** 字符串转json */
	public static String beanToJson(Object obj) {
		return JSONObject.toJSONString(obj,SerializerFeature.DisableCircularReferenceDetect);
	}

	public static <T>T parse(Object obj,Class<T> clazz){
		return JsonUtil.jsonToBean(JsonUtil.beanToJson(obj),clazz);
	}
	
	/** 将json字符串转换为对象 */
	public static <T> T jsonToBean(String jsonStr, Class<T> beanClass) {
		return (T) JSONObject.parseObject(jsonStr, beanClass);
	}
	
	/** 将json字符串转换为列表 */
	public static <T> List<T> jsonToList(String jsonStr, Class<T> beanClass) {
		return (List<T>) JSONObject.parseArray(jsonStr, beanClass);
	}
	
	public static JSONObject JSONObject(){
		return new JSONObject();
	}
	
	public static JSONArray JSONArray(){
		return new JSONArray();
	}
	/** 测试JSON格式是否正常 */
	public static boolean isJson(String json) {
    	try {
    		JSONObject.parse(json);
			return true;
		} catch (Exception e) {
			return false;
		}
    }
}
