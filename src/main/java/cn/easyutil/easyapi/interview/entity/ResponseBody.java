package cn.easyutil.easyapi.interview.entity;

import cn.easyutil.easyapi.entity.common.Page;
import cn.easyutil.easyapi.util.ObjectUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class ResponseBody implements Serializable{

	private static final long serialVersionUID = 1L;
	//状态码
	private Integer code;
	//说明
	private String remark;
	//数据
	private Object data;
	//分页信息
	private Page page;

	public Integer getCode() {
		return code;
	}

	public String getRemark() {
		return remark;
	}

	public Object getData() {
		return data;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}
	
	public static ResponseBody error(String remark){
		return error(500, remark);
	}
	
	public static ResponseBody error(Integer code, String remark){
		ResponseBody body = new ResponseBody();
		body.setCode(code);
		body.setRemark(remark);
		return body;
	}
	
	public static ResponseBody error(){
		return error(500, "请求失败");
	}

	public static ResponseBody successObj(Object obj){
		ResponseBody body = new ResponseBody();
		body.setCode(200);
		body.setRemark("请求成功");
		body.setData(obj);
		body.setPage(null);
		return body;
	}
	
	public static ResponseBody successObj(){
		return successObj(null);
	}
	
	public static <T> ResponseBody successList(Collection<T> list){
		list = list==null?new ArrayList<T>():list;
		ResponseBody body = new ResponseBody();
		body.setCode(200);
		body.setRemark("请求成功");
		body.setData(list);
		if(!list.isEmpty()){
			T t = list.iterator().next();
			Object val = ObjectUtil.getAttributeValue(t, "page");
			if(val != null){
				body.setPage((Page)val);
			}
		}
		
		return body;
	}
	
	public static ResponseBody successList(){
		return successList(null);
	}
	
}
