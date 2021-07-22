package cn.easyutil.easyapi.entity.common;

import java.lang.annotation.*;

/** 标记文档说明 */
@Target({ElementType.METHOD,ElementType.FIELD,ElementType.TYPE,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented 
public @interface ApidocComment {
	int USE_FOR_REQUEST = 1;
	int USE_FOR_RESPONSE = 2;

	//隐藏属性作用域，默认全部
	int[] hiddenUseFor()default {USE_FOR_REQUEST,USE_FOR_RESPONSE};
	//必填属性作用域，默认全部
	int[] requiredUseFor()default {USE_FOR_REQUEST,USE_FOR_RESPONSE};
	//字段描述
	String value()default"";
	//字段值样例
	String mockValue()default"";
	//特殊说明,通常用于接口
	String notes()default"";
	//是否必填
	boolean required()default true;
	//是否隐藏
	boolean hidden()default false;
	//是否忽略
	boolean ignore()default false;
	//请求参数说明
	String requestNotes()default"";
	//返回参数说明
	String responseNotes()default"";
	//请求参数类型转换
	RequestParameter[] requestParameterParse() default {};
}
