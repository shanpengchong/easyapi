package cn.easyutil.easyapi.interview.entity;

import cn.easyutil.easyapi.util.HttpUtilBAK;
import cn.easyutil.easyapi.util.JsonUtil;
import cn.easyutil.easyapi.util.ObjectUtil;
import cn.easyutil.easyapi.util.StringUtil;
import com.alibaba.fastjson.JSONObject;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.Map.Entry;

/**
 * 模拟请求的请求体
 */
public class HttpEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	//请求地址
	private String url;
	//请求方式 
	private String method;
	//请求类型 0-form  1-json体  2-文件上传 3-XML 4-RAW
	private Integer type;
	//请求参数json
	private String jsonParams;
	//请求头json
	private String jsonHeaders;
	//文件上传参数名
	private String fileParamName;
	
	public String getUrl() {
		return url;
	}
	public String getMethod() {
		return method;
	}
	public Integer getType() {
		return type;
	}
	public String getJsonParams() {
		return jsonParams;
	}
	public String getJsonHeaders() {
		return jsonHeaders;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public void setJsonParams(String jsonParams) {
		this.jsonParams = jsonParams;
	}
	public void setJsonHeaders(String jsonHeaders) {
		this.jsonHeaders = jsonHeaders;
	}
	public String getFileParamName() {
		return fileParamName;
	}
	public void setFileParamName(String fileParamName) {
		this.fileParamName = fileParamName;
	}
	
	public static Map<String, String> formToMap(String formData){
		if(StringUtil.isEmpty(formData)){
			return null;
		}
		String[] split = formData.split("&");
		Map<String, String> result = new HashMap<String, String>();
		for (String s : split) {
			if(!s.contains("=")){
				continue;
			}
			String[] sp = s.split("=");
			if(sp.length == 2){
				result.put(sp[0], sp[1]);
			}
		}
		return result;
	}
	
	public static String jsonToForm(String json){
		if(StringUtil.isEmpty(json)){
			return null;
		}
		Map<String, Object> param = new HashMap<String, Object>();
		StringBuffer listParam = new StringBuffer();
		JSONObject obj = JSONObject.parseObject(json);
		Set<Entry<String, Object>> entrySet = obj.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if(ObjectUtil.isBaseObject(value)){
				param.put(key, value);
				continue;
			}
			//循环嵌套map
			if(Map.class.isAssignableFrom(value.getClass())){
				Map<String,Object> map  = (Map<String, Object>) value;
				if(!map.isEmpty()){
					nestedMap(key, map,listParam, param);
				}
				continue;
			}
			//循环嵌套list
			if(Collection.class.isAssignableFrom(value.getClass())){
				List<Object> list = (List<Object>) value;
				if(!list.isEmpty()){
					nestedList(key, list, listParam,param);
				}
			}
		}
		HttpUtilBAK util = new HttpUtilBAK();
		util.setRequestParam(param);
		return util.getRequestParam()+listParam.toString();
	}
	
	/**
	 * 循环嵌套map
	 * @param key	参数key
	 * @param map	嵌套的map
	 * @param listParam 
	 * @param param	最终的参数
	 */
	private static void nestedMap(String key,Map<String,Object> map,StringBuffer listParam, Map<String, Object> param){
		Set<Entry<String, Object>> entrySet = map.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			String key2 = entry.getKey();
			Object value = entry.getValue();
			if(ObjectUtil.isBaseObject(value)){
				param.put(key+"."+key2, value);
				continue;
			}
			//循环嵌套map
			if(Map.class.isAssignableFrom(value.getClass())){
				Map<String,Object> map2  = (Map<String, Object>) value;
				if(!map.isEmpty()){
					nestedMap(key+"."+key2, map2,listParam, param);
				}
				continue;
			}
			//循环嵌套list
			if(Collection.class.isAssignableFrom(value.getClass())){
				List<Object> list2 = (List<Object>) value;
				if(!list2.isEmpty()){
					nestedList(key+"."+key2, list2, listParam,param);
				}
			}
		}
	}
	
	private static void nestedList(String key,List<Object> list,StringBuffer listParam,Map<String, Object> param){
		int i = 0;
		for (Object obj : list) {
			if(ObjectUtil.isBaseObject(obj)){
				listParam.append("&"+key+"="+obj);
				continue;
			}
			key = key+"["+i+"]";
			//嵌套map
			if(Map.class.isAssignableFrom(obj.getClass())){
				Map<String,Object> map2  = (Map<String, Object>) obj;
				if(!map2.isEmpty()){
					nestedMap(key, map2,listParam, param);
				}
				continue;
			}
			//循环嵌套list
			if(Collection.class.isAssignableFrom(obj.getClass())){
				List<Object> list2 = (List<Object>) obj;
				if(!list.isEmpty()){
					nestedList(key, list2, listParam,param);
				}
			}
			i++;
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		HttpEntity entity = new HttpEntity();
		entity.setUrl("http://house.easyutil.cn/apis/file/down?url=L3dvcmtzcGFjZS9ob3VzZS1sZWFzZS91cGxvYWQtZmlsZS85YzMwMGJiZjc3NDk0ZmU5YjI4ZmIzMTZkMDVlMDc1OQ==.jpeg");
		entity.setMethod("POST");
		entity.setType(0);
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", "root");
		map.put("password", "123456");
		entity.setJsonParams(JsonUtil.beanToJson(map));
		HttpUtilBAK util = new HttpUtilBAK("http://localhost:8089/yifei-apidoc/doUrl");
		util.setFormRequestParam(entity);
		System.out.println(util.doUrl());
	}
}
