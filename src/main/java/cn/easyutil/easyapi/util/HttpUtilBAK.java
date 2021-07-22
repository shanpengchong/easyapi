package cn.easyutil.easyapi.util;

import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;
import java.util.Map.Entry;

/** URL请求工具 */
public class HttpUtilBAK {
	public static final String requestMethod_POST="POST";
	public static final String requestMethod_GET="GET";
	public static final String requestMethod_PUT="PUT";
	public static final String requestMethod_DELETE="DELETE";
	public static final String requestMethod_PATCH="PATCH";
	/**
	 * 请求地址
	 */
	private String requestUrl;
	
	/**
	 * 响应数据编码
	 */
	private String outCharSet = "UTF-8";
	
	/**
	 * 请求参数
	 */
	private String requestParam;
	
	/**
	 * 请求方法 get,post
	 */
	private String requestMethod;
	
	/**
	 * 请求头
	 */
	private Map<String, String> headers = new HashMap<String, String>();
	
	/**
	 * 请求返回的header
	 */
	private Map<String, String> responseHeaders = new HashMap<String, String>();
	
	/** 服务器返回的session*/
	private String responseSessionId;

	public HttpUtilBAK(){
		this.outCharSet = "utf-8";
		this.requestMethod = requestMethod_GET;
	}
	
	public HttpUtilBAK(String requestUrl){
		this.requestUrl = requestUrl;
		this.outCharSet = "utf-8";
		this.requestMethod = requestMethod_GET;
	}
	
	public String getRequestUrl() {
		return requestUrl;
	}

	public HttpUtilBAK setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
		return this;
	}

	public String getOutCharSet() {
		return outCharSet;
	}

	public HttpUtilBAK setOutCharSet(String outCharSet) {
		this.outCharSet = outCharSet;
		return this;
	}

	public String getRequestParam() {
		return requestParam;
	}

	public HttpUtilBAK setRequestParam(String param) {
		this.requestParam = param;
		return this;
	}
	
	public <T> HttpUtilBAK setFormRequestParam(T t){
		Map<String, Object> map = JsonUtil.beanToMap(t);
		return setRequestParam(map);
	}
	
	public Map<String, String> getResponseHeaders() {
		return responseHeaders;
	}
	
	public void clearParams(){
		this.requestParam = null;
	}

	public <T> HttpUtilBAK setJsonRequestParam(T t){
		this.setHeaders("Content-Type", "application/json");
		return setRequestParam(JsonUtil.beanToJson(t));
	}
	
	public String getResponseSessionId() {
		return responseSessionId;
	}

	/**
	 * 添加session保持
	 * @param sessionId	服务器返回的sessionId
	 */
	public void addSession(String sessionId) {
		if(StringUtil.isEmpty(sessionId)){
			return;
		}
		if(sessionId.toUpperCase().startsWith("JSESSIONID=")){
			this.headers.put("Cookie", sessionId);
		}else{
			this.headers.put("Cookie", "JSESSIONID="+sessionId);
		}
	}

	public HttpUtilBAK setRequestParam(String key, String val){
		if(this.requestParam == null){
			this.requestParam = key+"="+val;
		}else{
			this.requestParam += "&"+key+"="+val;
		}
		return this;
	}
	
	public HttpUtilBAK setRequestParam(@SuppressWarnings("rawtypes") Map paramMap){
		String paramStr = "";
		if (paramMap != null) {
			for (Object key : paramMap.keySet()) {
				paramStr += key + "=";
				paramStr += paramMap.get(key) + "&";
			}
			if(!StringUtil.isEmpty(paramStr)){
				paramStr = paramStr.substring(0, paramStr.length() - 1);
			}
		}
		if(this.requestParam == null){
			this.requestParam = paramStr;
		}else{
			this.requestParam += "&"+paramStr;
		}
		return this;
	}
	
	public String getRequestMethod() {
		return requestMethod;
	}

	public HttpUtilBAK setRequestMethod(String method) {
		this.requestMethod = method;
		return this;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public HttpUtilBAK setHeaders(Map<String, String> headers) {
		if(headers == null){
			headers = new HashMap<String, String>();
		}
		this.headers = headers;
		return this;
	}
	
	public HttpUtilBAK setHeaders(String key, String val){
		this.headers.put(key, val);
		return this;
	}

	private String getNotNullUrl(){
		String url = getRequestUrl();
		if(url==null || url.length()==0){
			throw new RuntimeException("url  is  null");
		}
		return url;
	}
	
	/**
	 * json数据转form表单提交
	 * @param json
	 * @return
	 */
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
	
	/**
	 * 循环嵌套list
	 * @param key
	 * @param list
	 * @param listParam
	 * @param param
	 */
	private static void nestedList(String key,List<Object> list,StringBuffer listParam,Map<String, Object> param){
		for (Object obj : list) {
			if(ObjectUtil.isBaseObject(obj)){
				listParam.append("&"+key+"="+obj);
				continue;
			}
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
		}
	}
	
	
	/***
	 * 请求一个url，获取返回值
	 * 
	 * @param webUrl
	 *            url路径
	 * @param method
	 *            请求方式(POST/GET)
	 * @param headers
	 *            请求头
	 * @param paramMap
	 *            请求参数(主要POST请求场景所需)
	 * @return 返回该url的响应结果
	 */
	public static String doUrl(String webUrl, String method, Map<String, String> headers,
			Map<String, String> paramMap) {
		String paramStr = "";
		if (paramMap != null) {
			for (String key : paramMap.keySet()) {
				paramStr += key + "=";
				paramStr += paramMap.get(key) + "&";
			}
			paramStr = paramStr.substring(0, paramStr.length() - 1);
		}
		return doUrl(webUrl, method, headers, paramStr);
	}

	/***
	 * 请求一个url，获取返回值
	 * 
	 * @param webUrl
	 *            url路径
	 * @param method
	 *            请求方式(POST/GET)
	 * @param headers
	 *            请求头
	 * @param paramMap
	 *            请求参数(主要POST请求场景所需)
	 * @return 返回该url的响应结果
	 */
	public static String doUrl(String webUrl, String method, Map<String, String> headers, String message) {
		try {
			URL url = new URL(webUrl);
			URLConnection connect = url.openConnection();
			connect.setUseCaches(false);
			connect.setDoOutput(true);
			// 请求头
			if (headers != null) {
				for (String key : headers.keySet()) {
					connect.setRequestProperty(key, headers.get(key));
				}
			}
			// 请求方式
			if (method != null) {
				((HttpURLConnection) connect).setRequestMethod(method);
				if (message != null && !message.equals("")) {
					connect.getOutputStream().write(message.getBytes());
					connect.getOutputStream().close();
				}
			}

			// 获取响应结果
			HttpURLConnection httpUrlConnection = (HttpURLConnection) connect;
			httpUrlConnection.connect();
			if (httpUrlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				InputStream in = httpUrlConnection.getErrorStream();
				byte[] bts = new byte[1024];
				int length = 0;
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				while ((length = in.read(bts)) > 0) {
					bout.write(bts, 0, length);
					bout.flush();
				}
				throw new RuntimeException(
						httpUrlConnection.getResponseCode() + ":" + new String(bout.toByteArray()));
			}
			// connect;
			// int code = httpUrlConnection.getResponseCode();
			// if (code == 200) {
			InputStream in = httpUrlConnection.getInputStream();
			byte[] bts = new byte[1024];
			int length = 0;
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			while ((length = in.read(bts)) > 0) {
				bout.write(bts, 0, length);
				bout.flush();
			}
			return new String(bout.toByteArray());
			// return null;
			// }
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/***
	 * 获取远程url的input流
	 * 
	 * @param webUrl
	 *            远程url
	 * @param method
	 *            提交方式（POST/GET）
	 * @param headers
	 *            消息头
	 * @param message
	 *            消息体
	 * @return 返回流
	 */
	public static InputStream doInputStreamUrl(String webUrl, String method) {
		try {
			URL url = new URL(webUrl);
			URLConnection connect = url.openConnection();
			connect.setUseCaches(false);

			// 请求方式
			if (method != null) {
				((HttpURLConnection) connect).setRequestMethod(method);
			}

			// 获取响应结果
			return connect.getInputStream();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/***
	 * 请求一个url，获取返回值
	 * 
	 * @param webUrl
	 *            url路径
	 * @param method
	 *            请求方式(POST/GET)
	 * @return 返回该url的响应结果
	 */
	public static String doUrl(String webUrl, String method) {
		return doUrl(webUrl, method, null, "");
	}

	/***
	 * 获取远程url的input流
	 * 
	 * @param webUrl
	 *            远程url
	 * @param method
	 *            提交方式（POST/GET）
	 * @param headers
	 *            消息头
	 * @param message
	 *            消息体
	 * @return 返回流
	 */
	public InputStream doInputStreamUrl() {
		try {
			URL url = new URL(getNotNullUrl());
			URLConnection connect = url.openConnection();
			connect.setUseCaches(false);
			connect.setDoOutput(true);
			addSession(this.responseSessionId);
			// 请求头
			if (headers != null) {
				for (String key : headers.keySet()) {
					connect.setRequestProperty(key, headers.get(key));
				}
			}
			// 请求方式
			if (requestMethod != null) {
				((HttpURLConnection) connect).setRequestMethod(requestMethod);
			}
			if (this.requestParam != null && !this.requestParam.equals("")) {
				connect.getOutputStream().write(this.requestParam.getBytes());
				connect.getOutputStream().close();
			}
			// 获取响应结果
			HttpURLConnection httpUrlConnection = (HttpURLConnection) connect;
			httpUrlConnection.connect();
			if (httpUrlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				InputStream in = httpUrlConnection.getErrorStream();
				byte[] bts = new byte[1024];
				int length = 0;
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				while ((length = in.read(bts)) > 0) {
					bout.write(bts, 0, length);
					bout.flush();
				}
				throw new RuntimeException(
						httpUrlConnection.getResponseCode() + ":" + new String(bout.toByteArray(),this.outCharSet));
			}
			Map<String, List<String>> headers = connect.getHeaderFields();
			Set<Entry<String, List<String>>> entry = headers.entrySet();
			for (Entry<String, List<String>> en : entry) {
				String key = en.getKey();
				List<String> value = en.getValue();
				if(!StringUtil.isEmpty(key) && !StringUtil.isEmpty(value)){
					responseHeaders.put(key, value.toString());
					if(key.equals("Set-Cookie")){
						responseSessionId = value.get(0);
					}
				}
			}
			return connect.getInputStream();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	/***
	 * 请求一个url，获取返回值
	 * 
	 * @param webUrl
	 *            url路径
	 * @param method
	 *            请求方式(POST/GET)
	 * @param headers
	 *            请求头
	 * @param paramMap
	 *            请求参数(主要POST请求场景所需)
	 * @return 返回该url的响应结果
	 */
	public String doUrl() {
		try {
			InputStream in = doInputStreamUrl();
			byte[] bts = new byte[1024];
			int length = 0;
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			while ((length = in.read(bts)) > 0) {
				bout.write(bts, 0, length);
				bout.flush();
			}
			return new String(bout.toByteArray(),this.outCharSet);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 文件上传
	 * @param paramName	文件参数名称
	 * @param file	文件
	 * @return
	 */
	public String doFileUrl(String paramName,File file){
		try {
			return doFileUrl(paramName, new FileInputStream(file),file.getName(), null);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 发送文件
	 * @param input
	 *            文件
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String doFileUrl(String paramName,InputStream input,String fileName,Map params){
		final String newLine = "\r\n";
		final String preFix = "--";
		final String BOUNDARY = "----WebKitFormBoundaryCXRtmcVNK0H70msG";
		try {
			URL url = new URL(getNotNullUrl());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			conn.setRequestProperty("Charsert", "UTF-8");
			if (headers != null && !headers.isEmpty()) {
				Set<Entry<String, String>> set = headers.entrySet();
				for (Entry<String, String> entry : set) {
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			//添加附带参数
			if(params!=null && !params.isEmpty()){
				Set set = params.keySet();
				for (Object object : set) {
					String key = object.toString();
					String val = params.get(object).toString();
					//向请求中写分割线
					out.writeBytes(preFix + BOUNDARY + newLine);
					//向请求拼接参数
					out.writeBytes("Content-Disposition: form-data; " + "name=\"" + key + "\"" + newLine);
					//向请求中拼接空格
                    out.writeBytes(newLine);
                    //写入值
                    out.writeBytes(URLEncoder.encode(val,"utf-8"));
                    out.writeBytes(newLine);
				}
			}
			byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();// 定义最后数据分隔线
			StringBuilder sb = new StringBuilder();
			sb.append("--");
			sb.append(BOUNDARY);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data;name=\""+ URLEncoder.encode(paramName, "utf-8")+ "\";filename=\"" + fileName + "\"\r\n");
			sb.append("Content-Type:application/octet-stream\r\n\r\n");

			byte[] data = sb.toString().getBytes();
			out.write(data);
			DataInputStream in = new DataInputStream(input);
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while ((bytes = in.read(bufferOut)) != -1) {
				out.write(bufferOut, 0, bytes);
			}
			out.write("\r\n".getBytes()); // 多个文件时，二个文件之间加入这个
			in.close();
			out.write(end_data);
			out.flush();
			out.close();
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				InputStream errorIn = conn.getErrorStream();
				if(errorIn != null){
					byte[] bts = new byte[1024];
					int length = 0;
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					while ((length = errorIn.read(bts)) > 0) {
						bout.write(bts, 0, length);
						bout.flush();
					}
					throw new RuntimeException(
							conn.getResponseCode() + ":" + new String(bout.toByteArray(),this.outCharSet));
				}
			}
			
			// 定义BufferedReader输入流来读取URL的响应
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), this.outCharSet));
			String line = null;
			StringBuffer result = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
			Map<String, List<String>> headers = conn.getHeaderFields();
			Set<Entry<String, List<String>>> entry = headers.entrySet();
			for (Entry<String, List<String>> en : entry) {
				String key = en.getKey();
				List<String> value = en.getValue();
				if(!StringUtil.isEmpty(key) && !StringUtil.isEmpty(value)){
					responseHeaders.put(key, value.toString());
					if(key.equals("Set-Cookie")){
						responseSessionId = value.get(0);
						responseSessionId = responseSessionId.substring(0, responseSessionId.indexOf(";"));
					}
				}
			}
			
			return result.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) throws IOException {
		HttpUtilBAK util = new HttpUtilBAK("http://localhost:7103/yifei-apidoc/getInterfaces?controllerName=-back-com.yifei.video.controller.MenuController");
		util.setRequestMethod("GET");
		System.out.println(util.doUrl());
	}
}
