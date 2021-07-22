package cn.easyutil.easyapi.datasource.bean;

import java.io.Serializable;
import java.util.*;

public class EasyapiBindSQLExecuter implements Serializable {

	private static final long serialVersionUID = 1L;

	// 组装sql
	private StringBuffer sql = new StringBuffer("");

	// mybatis需要的参数语句
	private StringBuffer mybatisSql = new StringBuffer("");
	// limit起始位置
	private Integer limitStart = 0;
	// limit结束位置
	private Integer limitSize = 0;
	// 排序字段
	private List<String> mybatisDescList = new ArrayList<String>();
	// 排序字段
	private List<String> descList = new ArrayList<String>();
	// 自增字段
	private Map<String, Object> incrMap = new HashMap<String, Object>();
	// 返回字段过滤
	private StringBuffer returnParam = new StringBuffer();
	// 参数
	private List<Object> params = new ArrayList<Object>();
	// 数据库映射对象
	private Object t;
	// 参数map
	private Map<String, Object> mybatisParams = new HashMap<String, Object>();
	// 设置字段值为null的集合列表
	private List<String> nullValFields = new ArrayList<String>();

	public EasyapiBindSQLExecuter(Object t) {
		this.t = t;
	}

	public static EasyapiBindSQLExecuter build(Object t){
		EasyapiBindSQLExecuter executer = new EasyapiBindSQLExecuter(t);
		return executer;
	}

	/**
	 * 获取字段值为null的集合列表
	 * 
	 * @return
	 */
	public List<String> getNullValFields() {
		return this.nullValFields;
	}

	/**
	 * 设置数据库操作对象
	 * 
	 * @param t
	 */
	public <T> void setBean(T t) {
		this.t = t;
	}

	public <T> EasyapiBindSQLExecuter like(EasyapiBindLambdaFunction<T,?> function, String like, Boolean... useOr){
		return like(EasyapiBindLamdbaUtil.getFieldName(function),like,useOr);
	}

	public <T> EasyapiBindSQLExecuter like(boolean el,EasyapiBindLambdaFunction<T,?> function, String like, Boolean... useOr){
		if(!el){
			return this;
		}
		return like(EasyapiBindLamdbaUtil.getFieldName(function),like,useOr);
	}
	/**
	 * 模糊匹配
	 * 
	 * @param field
	 *            字段
	 * @param like
	 *            关键字
	 * @param useOr
	 *            是否使用or关键字,默认或不传为and语句
	 * @return
	 */
	public EasyapiBindSQLExecuter like(String field, String like, Boolean... useOr) {
		String key = UUID.randomUUID().toString().replace("-", "");
		if (useOr.length > 0 && useOr[0]) {
			sql.append(" or `" + field + "` like" + " ? ");
			mybatisSql.append(" or `" + field + "` like " + " #{mybatisParams." + key + "} ");
		} else {
			sql.append(" and `" + field + "` like" + " ? ");
			mybatisSql.append(" and `" + field + "` like" + " #{mybatisParams." + key + "} ");
		}
		params.add("%" + like + "%");
		mybatisParams.put(key, "%" + like + "%");
		return this;
	}

	/**
	 * 排序字段,默认正序
	 * 
	 * @param field
	 * @return
	 */
	public EasyapiBindSQLExecuter orderBy(String field) {
		return orderBy(field, true);
	}

	public <T> EasyapiBindSQLExecuter max(EasyapiBindLambdaFunction<T,?> function, String asName){
		return max(EasyapiBindLamdbaUtil.getFieldName(function),asName);
	}

	public EasyapiBindSQLExecuter max(String field) {
		return max(field, null);
	}

	public EasyapiBindSQLExecuter max(String field, String asName) {
		if (asName != null) {
			this.returnParam.append("MAX(" + field + ") as " + asName + " ");
		} else {
			this.returnParam.append("MAX(" + field + ") as " + field + " ");
		}
		this.returnParam.append(",");
		return this;
	}

	public <T> EasyapiBindSQLExecuter min(EasyapiBindLambdaFunction<T,?> function, String asName){
		return min(EasyapiBindLamdbaUtil.getFieldName(function),asName);
	}

	public EasyapiBindSQLExecuter min(String field) {
		return min(field, null);
	}

	public EasyapiBindSQLExecuter min(String field, String asName) {
		if (asName != null) {
			this.returnParam.append("MIN(" + field + ") as " + asName + " ");
		} else {
			this.returnParam.append("MIN(" + field + ") as " + field + " ");
		}
		this.returnParam.append(",");
		return this;
	}

	public <T> EasyapiBindSQLExecuter count(EasyapiBindLambdaFunction<T,?> function, String asName){
		return count(EasyapiBindLamdbaUtil.getFieldName(function),asName);
	}

	public EasyapiBindSQLExecuter count(String field) {
		return count(field, null);
	}

	public EasyapiBindSQLExecuter count(String field, String asName) {
		if (asName != null) {
			this.returnParam.append("COUNT(" + field + ") as " + asName + " ");
		} else {
			this.returnParam.append("COUNT(" + field + ") as " + field + " ");
		}
		this.returnParam.append(",");
		return this;
	}

	public <T> EasyapiBindSQLExecuter sum(EasyapiBindLambdaFunction<T,?> function, String asName){
		return sum(EasyapiBindLamdbaUtil.getFieldName(function),asName);
	}

	public EasyapiBindSQLExecuter sum(String field) {
		return sum(field, null);
	}

	public EasyapiBindSQLExecuter sum(String field, String asName) {
		if (asName != null) {
			this.returnParam.append("SUM(" + field + ") as " + asName + " ");
		} else {
			this.returnParam.append("SUM(" + field + ") as " + field + " ");
		}
		this.returnParam.append(",");
		return this;
	}

	public <T> EasyapiBindSQLExecuter orderBy(EasyapiBindLambdaFunction<T,?> function, boolean desc){
		return orderBy(EasyapiBindLamdbaUtil.getFieldName(function),desc);
	}

	public <T> EasyapiBindSQLExecuter orderBy(EasyapiBindLambdaFunction<T,?> function) {
		return orderBy(EasyapiBindLamdbaUtil.getFieldName(function), true);
	}


	/**
	 * 排序
	 * 
	 * @param field
	 *            字段
	 * @param desc
	 *            true:正序 false:倒序
	 * @return
	 */
	public EasyapiBindSQLExecuter orderBy(String field, boolean desc) {
		return orderBy(field, null, null, desc);
	}

	public <T> EasyapiBindSQLExecuter orderByIf(EasyapiBindLambdaFunction<T,?> function, Object val, boolean desc){
		return orderByIf(EasyapiBindLamdbaUtil.getFieldName(function),val,desc);
	}

	/**
	 * 排序，并且指定值放在最前或最后
	 * 
	 * @param field
	 * @param val
	 * @param desc
	 * @return
	 */
	public EasyapiBindSQLExecuter orderByIf(String field, Object val, boolean desc) {
		return orderBy(field, val, null, desc);
	}


	public <T> EasyapiBindSQLExecuter orderByIn(EasyapiBindLambdaFunction<T,?> function, Collection in, boolean desc){
		return orderByIn(EasyapiBindLamdbaUtil.getFieldName(function),in,desc);
	}
	/**
	 * 排序,并且指定集合值放在最前或最后
	 * 
	 * @param field
	 * @param in
	 * @param desc
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public EasyapiBindSQLExecuter orderByIn(String field, Collection in, boolean desc) {
		return orderBy(field, null, in, desc);
	}

	@SuppressWarnings("rawtypes")
	private EasyapiBindSQLExecuter orderBy(String field, Object val, Collection in, boolean desc) {
		String descStr = "";
		if (!desc) {
			descStr = " desc ";
		}
		if (val != null) {
			String key = UUID.randomUUID().toString().replace("-", "");
			mybatisDescList.add(" if (`" + field + "`=#{mybatisParams." + key + "},0,1),`" + field + "` " + descStr);
			descList.add(" if (`" + field + "`=?,0,1),`" + field + "` " + descStr);
			mybatisParams.put(key, val);
			params.add(val);
		} else if (in != null) {
			StringBuffer mybatisSql = new StringBuffer("`" + field + "` in(");
			StringBuffer sql = new StringBuffer("`" + field + "` in(");
			for (Object obj : in) {
				String key = UUID.randomUUID().toString().replace("-", "");
				mybatisSql.append("#{mybatisParams." + key + "},");
				sql.append("?,");
				params.add(obj);
				mybatisParams.put(key, obj);
			}
			mybatisDescList.add(mybatisSql.deleteCharAt(mybatisSql.length() - 1).toString() + ")," + field);
			descList.add(sql.deleteCharAt(sql.length() - 1).toString() + ")," + field);
		} else {
			mybatisDescList.add(" `" + field + "` " + descStr);
			descList.add(" `" + field + "` " + descStr);
		}
		return this;
	}

	public <T> EasyapiBindSQLExecuter in(EasyapiBindLambdaFunction<T,?> function, Collection<T> in, Boolean... useOr){
		return in(EasyapiBindLamdbaUtil.getFieldName(function),in,useOr);
	}

	public <T> EasyapiBindSQLExecuter in(boolean el,EasyapiBindLambdaFunction<T,?> function, Collection<T> in, Boolean... useOr){
		if(!el){
			return this;
		}
		return in(EasyapiBindLamdbaUtil.getFieldName(function),in,useOr);
	}

	/**
	 * in语句
	 * 
	 * @param <T>
	 * @param field
	 *            字段
	 * @param in
	 * @param useOr
	 *            是否使用or关键字,默认或不传为and语句
	 * @return
	 */
	public <T> EasyapiBindSQLExecuter in(String field, Collection<T> in, Boolean... useOr) {
		if (in == null || in.size() == 0) {
			return this;
		}
		if (useOr.length > 0 && useOr[0]) {
			sql.append(" or `" + field + "` in (");
			mybatisSql.append(" or `" + field + "` in (");
		} else {
			sql.append(" and `" + field + "` in (");
			mybatisSql.append(" and `" + field + "` in (");
		}
		for (Object o : in) {
			String key = UUID.randomUUID().toString().replace("-", "");
			sql.append("?,");
			mybatisSql.append("#{mybatisParams." + key + "},");
			mybatisParams.put(key, o);
			params.add(o);
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(") ");
		mybatisSql.deleteCharAt(mybatisSql.length() - 1);
		mybatisSql.append(") ");
		return this;
	}

	public <T> EasyapiBindSQLExecuter notIn(EasyapiBindLambdaFunction<T,?> function, Collection<T> in, Boolean... useOr){
		return notIn(EasyapiBindLamdbaUtil.getFieldName(function),in,useOr);
	}

	public <T> EasyapiBindSQLExecuter notIn(boolean el,EasyapiBindLambdaFunction<T,?> function, Collection<T> in, Boolean... useOr){
		if(!el){
			return this;
		}

		return notIn(EasyapiBindLamdbaUtil.getFieldName(function),in,useOr);
	}

	/**
	 * sql not in 语句
	 * 
	 * @param <T>
	 * @param field
	 * @param in
	 * @param useOr
	 *            是否使用or关键字,默认或不传为and语句
	 * @return
	 */
	public <T> EasyapiBindSQLExecuter notIn(String field, Collection<T> in, Boolean... useOr) {
		if (in == null || in.size() == 0) {
			return this;
		}
		if (useOr.length > 0 && useOr[0]) {
			sql.append(" or `" + field + "` not in (");
			mybatisSql.append(" or `" + field + "` not in (");
		} else {
			sql.append(" and `" + field + "` not in (");
			mybatisSql.append(" and `" + field + "` not in (");
		}
		for (Object o : in) {
			String key = UUID.randomUUID().toString().replace("-", "");
			sql.append("?,");
			mybatisSql.append("#{mybatisParams." + key + "},");
			mybatisParams.put(key, o);
			params.add(o);
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(") ");
		mybatisSql.deleteCharAt(mybatisSql.length() - 1);
		mybatisSql.append(") ");
		return this;
	}

	public <T> EasyapiBindSQLExecuter lte(EasyapiBindLambdaFunction<T,?> function, Object obj, Boolean... useOr){
		return lte(EasyapiBindLamdbaUtil.getFieldName(function),obj,useOr);
	}

	public <T> EasyapiBindSQLExecuter lte(boolean el,EasyapiBindLambdaFunction<T,?> function, Object obj, Boolean... useOr){
		if(!el){
			return this;
		}
		return lte(EasyapiBindLamdbaUtil.getFieldName(function),obj,useOr);
	}

	/**
	 * 小于等于语句 <=
	 * 
	 * @param field
	 * @param obj
	 * @param useOr
	 *            是否使用or关键字,默认或不传为and语句
	 * @return
	 */
	public EasyapiBindSQLExecuter lte(String field, Object obj, Boolean... useOr) {
		if (obj == null) {
			return this;
		}
		String key = UUID.randomUUID().toString().replace("-", "");
		if (useOr.length > 0 && useOr[0]) {
			sql.append(" or `" + field + "` <= ?");
			mybatisSql.append(" or `" + field + "` <= #{mybatisParams." + key + "} ");
		} else {
			sql.append(" and `" + field + "` <= ?");
			mybatisSql.append(" and `" + field + "` <= #{mybatisParams." + key + "} ");
		}
		params.add(obj);
		mybatisParams.put(key, obj);
		return this;
	}

	public <T> EasyapiBindSQLExecuter gte(EasyapiBindLambdaFunction<T,?> function, Object obj, Boolean... useOr){
		return gte(EasyapiBindLamdbaUtil.getFieldName(function),obj,useOr);
	}

	public <T> EasyapiBindSQLExecuter gte(boolean el,EasyapiBindLambdaFunction<T,?> function, Object obj, Boolean... useOr){
		if(!el){
			return this;
		}

		return gte(EasyapiBindLamdbaUtil.getFieldName(function),obj,useOr);
	}


	/**
	 * 大于等于语句
	 * 
	 * @param field
	 * @param obj
	 * @param useOr
	 *            是否使用or关键字,默认或不传为and语句
	 * @return
	 */
	public EasyapiBindSQLExecuter gte(String field, Object obj, Boolean... useOr) {
		if (obj == null) {
			return this;
		}
		String key = UUID.randomUUID().toString().replace("-", "");
		if (useOr.length > 0 && useOr[0]) {
			sql.append(" or `" + field + "` >= ?");
			mybatisSql.append(" or `" + field + "` >= #{mybatisParams." + key + "} ");
		} else {
			sql.append(" and `" + field + "` >= ?");
			mybatisSql.append(" and `" + field + "` >= #{mybatisParams." + key + "} ");
		}
		params.add(obj);
		mybatisParams.put(key, obj);
		return this;
	}

	public <T> EasyapiBindSQLExecuter setNull(EasyapiBindLambdaFunction<T,?> function){
		return setNull(EasyapiBindLamdbaUtil.getFieldName(function));
	}

	public <T> EasyapiBindSQLExecuter setNull(boolean el,EasyapiBindLambdaFunction<T,?> function){
		if(!el){
			return this;
		}
		return setNull(EasyapiBindLamdbaUtil.getFieldName(function));
	}

	/**
	 * 设置字段值为null
	 * 
	 * @param field
	 *            字段名
	 * @param fields
	 *            字段名
	 * @return
	 */
	public EasyapiBindSQLExecuter setNull(String field, String... fields) {
		nullValFields.add(field);
		if (fields.length > 0) {
			nullValFields.addAll(Arrays.asList(fields));
		}
		return this;

	}

	public final <T> EasyapiBindSQLExecuter eq(EasyapiBindLambdaFunction<T,?> function, Object obj, Boolean... useOr){
		return eq(EasyapiBindLamdbaUtil.getFieldName(function),obj,useOr);
	}

	public final <T> EasyapiBindSQLExecuter eq(boolean el,EasyapiBindLambdaFunction<T,?> function, Object obj, Boolean... useOr){
		if(!el){
			return this;
		}
		return eq(EasyapiBindLamdbaUtil.getFieldName(function),obj,useOr);
	}

	/**
	 * 相等语句
	 * 
	 * @param field
	 * @param obj
	 * @param useOr
	 *            是否使用or关键字,默认或不传为and语句
	 * @return
	 */
	public EasyapiBindSQLExecuter eq(String field, Object obj, Boolean... useOr) {
		if (obj == null) {
			return this;
		}
		String key = UUID.randomUUID().toString().replace("-", "");
		if (useOr.length > 0 && useOr[0]) {
			sql.append(" or `" + field + "` = ? ");
			mybatisSql.append(" or `" + field + "` = #{mybatisParams." + key + "} ");
		} else {
			sql.append(" and `" + field + "` = ? ");
			mybatisSql.append(" and `" + field + "` = #{mybatisParams." + key + "} ");
		}
		params.add(obj);
		mybatisParams.put(key, obj);
		return this;
	}

	public <T> EasyapiBindSQLExecuter notEquals(EasyapiBindLambdaFunction<T,?> function, Object obj, Boolean... useOr){
		return notEquals(EasyapiBindLamdbaUtil.getFieldName(function),obj,useOr);
	}

	public <T> EasyapiBindSQLExecuter notEquals(boolean el,EasyapiBindLambdaFunction<T,?> function, Object obj, Boolean... useOr){
		if(!el){
			return this;
		}
		return notEquals(EasyapiBindLamdbaUtil.getFieldName(function),obj,useOr);
	}

	/**
	 * 不等于语句
	 * 
	 * @param field
	 * @param obj
	 * @param useOr
	 *            是否使用or关键字,默认或不传为and语句
	 * @return
	 */
	public EasyapiBindSQLExecuter notEquals(String field, Object obj, Boolean... useOr) {
		if (obj == null) {
			return this;
		}
		String key = UUID.randomUUID().toString().replace("-", "");
		if (useOr.length > 0 && useOr[0]) {
			sql.append(" or `" + field + "` != ? ");
			mybatisSql.append(" or `" + field + "` != #{mybatisParams." + key + "} ");
		} else {
			sql.append(" and `" + field + "` != ? ");
			mybatisSql.append(" and `" + field + "` != #{mybatisParams." + key + "} ");
		}
		params.add(obj);
		mybatisParams.put(key, obj);
		return this;
	}

	public <T> EasyapiBindSQLExecuter incr(EasyapiBindLambdaFunction<T,?> function, Number num){
		return incr(EasyapiBindLamdbaUtil.getFieldName(function),num);
	}

	public <T> EasyapiBindSQLExecuter incr(boolean el,EasyapiBindLambdaFunction<T,?> function, Number num){
		if(!el){
			return this;
		}
		return incr(EasyapiBindLamdbaUtil.getFieldName(function),num);
	}

	/**
	 * 属性值自增
	 * 
	 * @param field
	 *            自增值，正数或负数
	 * @return
	 */
	public EasyapiBindSQLExecuter incr(String field, Number num) {
		if (num == null) {
			return this;
		}
		if (num instanceof Double || num instanceof Float) {
			incrMap.put(field, num.doubleValue());
		} else {
			incrMap.put(field, num.longValue());
		}
		return this;
	}

	/**
	 * 获取生成的原生sql
	 * 
	 *            数据库映射bean属性名规则
	 * @return
	 */
	public String getSql() {
		StringBuffer orderby = new StringBuffer("");
		if (this.descList != null && this.descList.size() > 0) {
			orderby.append(" order by ");
			for (String desc : this.descList) {
				orderby.append(" " + desc + ",");
			}
			orderby.deleteCharAt(orderby.length() - 1);
		}
		return this.sql.toString() + orderby.toString();
	}

	/**
	 * 获取sql语句的参数
	 * 
	 * @return
	 */
	public List<Object> getParams() {
		return this.params;
	}

	/**
	 * 获取数据库映射对象
	 * 
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getBean() {
		return (T) this.t;
	}

	/**
	 * 添加左括号 '('
	 * 
	 * @return
	 */
	public EasyapiBindSQLExecuter leftBrackets(Boolean... useOr) {
		if (useOr.length > 0 && useOr[0]) {
			sql.append(" or ( 1=1 ");
			mybatisSql.append(" or ( 1=1 ");
		} else {
			sql.append(" and ( 1=1 ");
			mybatisSql.append(" and ( 1=1 ");
		}
		return this;
	}

	/**
	 * 添加右括号 ')'
	 * 
	 * @return
	 */
	public EasyapiBindSQLExecuter rightBrackets() {
		sql.append(" ) ");
		mybatisSql.append(" ) ");
		return this;
	}

	/**
	 * 获取mybatis需要的语句
	 * 
	 * 数据库映射bean属性名规则
	 * @return
	 */
	public String getMybatisSql() {
		StringBuffer orderby = new StringBuffer("");
		if (this.mybatisDescList != null && this.mybatisDescList.size() > 0) {
			orderby.append(" order by ");
			for (String desc : this.mybatisDescList) {
				orderby.append(" " + desc + ",");
			}
			orderby.deleteCharAt(orderby.length() - 1);
		}
		return this.mybatisSql.toString() + orderby.toString();
	}

	public Map<String, Object> getMybatisParams() {
		return mybatisParams;
	}

	public Map<String, Object> getIncrMap() {
		return incrMap;
	}

	public String getReturnParam() {
		if (this.returnParam.length() > 0 && this.returnParam.toString().endsWith(",")) {
			return this.returnParam.toString().substring(0, this.returnParam.lastIndexOf(","));
		}
		return returnParam.toString();
	}

	public EasyapiBindSQLExecuter setReturnParam(String...field) {
		if (field.length == 0) {
			return this;
		}
		for (String s : field) {
			this.returnParam.append("`" + s + "`,");
		}
		return this;
	}

	public Integer getLimitStart() {
		return limitStart;
	}

	public EasyapiBindSQLExecuter setLimitStart(Integer limitStart) {
		this.limitStart = limitStart;
		return this;
	}

	public Integer getLimitSize() {
		return limitSize;
	}

	public EasyapiBindSQLExecuter setLimitSize(Integer limitSize) {
		this.limitSize = limitSize;
		return this;
	}
}
