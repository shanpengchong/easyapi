
package cn.easyutil.easyapi.datasource;

import cn.easyutil.easyapi.datasource.annotations.Tfd;
import cn.easyutil.easyapi.datasource.annotations.Tid;
import cn.easyutil.easyapi.datasource.annotations.Tie;
import cn.easyutil.easyapi.datasource.annotations.Tne;
import cn.easyutil.easyapi.datasource.bean.EasyApiBindSqlResult;
import cn.easyutil.easyapi.datasource.bean.EasyapiBindPage;
import cn.easyutil.easyapi.datasource.bean.EasyapiBindSQLExecuter;
import cn.easyutil.easyapi.entity.common.BaseBean;
import cn.easyutil.easyapi.util.ClobUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;

/**
 * easySql的具体执行类 内置基础增删改查和分页,事务</br>
 * 在使用之前请务必了解一下内容:</br>
 * 一:本工具提供了以下4个注解</br>
 * &nbsp;&nbsp;&nbsp;1:Tne(name="") 在你需要操作的bean类上添加，用来标注数据库表名,此为必填项</br>
 * &nbsp;&nbsp;&nbsp;2:Tid 在你需要操作的bean类属性上添加，用来标注数据库表主键名称，此为必填项</br>
 * &nbsp;&nbsp;&nbsp;3:Tfd(name="")
 * 在你需要操作的bean类属性上添加，用来标注数据库表字段,如一致，则非必填</br>
 * &nbsp;&nbsp;&nbsp;4:Tie
 * 在你需要操作的bean类属性上添加，用来标注数据库表中没有的字段，新增时不会添加进表</br>
 * 二:如果你需要操作的类属性有非基本数据，则无法使用本工具
 * 
 * @author shyFly
 *
 */
public class EasyapiBindSqlExecution {
	private DataSource dataSource;
	/** 当前连接 */
	private static ThreadLocal<Connection> connect = new ThreadLocal<>();
	private boolean autuoCommit = true;
	/** 是否打印日志 */
	private boolean doLogger = true;
	private String driver = "MYSQL";

	/** 默认连接池别名 */
	private static final String CONFIG_DEFAULT = "CONFIG_DEFAULT";
	private static final String USER_DEFAULT = "USER_DEFAULT";

	public EasyapiBindSqlExecution(Connection connect) {
		this.connect.set(connect);
	}

//	/**
//	 * 传入一个连接池，并作为本次对象使用的连接池
//	 *
//	 * @param dataSource
//	 *            外部传入连接池
//	 */
//	public EasyapiBindSqlExecution(DataSource dataSource) {
//		this.dataSource = dataSource;
//	}

	public EasyapiBindSqlExecution(){}

	/**
	 * 获取当前连接池
	 * 
	 * @return	返回当前连接池
	 */
	public DataSource getDataSource() {
		return this.dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/** 获取当前持有的connection连接 */
	public Connection getConcurrentConnect() {
		Connection connection = connect.get();
		try {
			if(connection!=null && !connection.isClosed()){
				return connection;
			}
			if(this.dataSource!=null){
				connection = this.dataSource.getConnection();
			}
			if(connection == null){
				throw new RuntimeException("无可用连接");
			}
			if(connection.isClosed()){
				throw new RuntimeException("连接已关闭,无可用连接");
			}
			connection.setAutoCommit(autuoCommit);
		} catch (SQLException throwables) {
			throw new RuntimeException(throwables);
		}
		connect.set(connection);
		return connection;
	}

	/** 获取当前连接,并默认手动提交事务 */
	private Connection getConnect() {
		return getConcurrentConnect();
	}

	/** 新增时获取主键 */
	private Object executeInsert(String sql, List<Object> params) {
		if (!sql.toUpperCase().startsWith("INSERT")) {
			throw new RuntimeException("当前非新增语句");
		}
		Connection connect = getConnect();
		PreparedStatement statement;
		try {
			statement = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			for (int i = 0; i < params.size(); i++) {
				statement.setObject(i + 1, params.get(i));
			}
			executeUpdate(statement);
			printLogger("==============================");
			printLogger("执行新增语句:" + sql);
			printLogger("执行参数为:" + params);
			printLogger("==============================");
			ResultSet result = statement.getGeneratedKeys();
			if (result.next()) {
				return result.getObject(1);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	/** 装载statment对象 */
	private PreparedStatement executeSql(String sql, List<Object> params) {
		Connection connect = getConnect();
		PreparedStatement statement;
		try {
			statement = connect.prepareStatement(sql);
			printLogger("==============================");
			printLogger("执行语句:" + sql);
			printLogger("执行参数为:" + params);
			printLogger("==============================");
			if (params == null || params.size() == 0) {
				return statement;
			}
			for (int i = 0; i < params.size(); i++) {
				statement.setObject(i + 1, params.get(i));
			}
			return statement;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/** 多条查询 */
	private List<Map<String, Object>> executeQuery(PreparedStatement statement) {
		try {
			ResultSet result = statement.executeQuery();
			ResultSetMetaData res = result.getMetaData();
			int count = res.getColumnCount();
			List<Map<String, Object>> list = new ArrayList<>();
			while (result.next()) {
				Map<String, Object> map = new HashMap<>();
				for (int i = 1; i <= count; i++) {
					map.put(res.getColumnLabel(i), result.getObject(i));
				}
				list.add(map);
			}
			return list;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** 修改 */
	private int executeUpdate(PreparedStatement statement) {
		try {
			return statement.executeUpdate();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** 查询条目数 */
	private Double executeCount(PreparedStatement statement) {
		try {
			ResultSet result = statement.executeQuery();
			Object count = null;
			while(result.next()){
				count = result.getObject(1);
			}
			if (doLogger) {
				printLogger("当前查询到总条数为:" + count);
			}
			toCommit();
			if(count == null){
				return 0.0;
			}
			return Double.valueOf(count.toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 设置提交模式
	 * 
	 * @param autoCommit
	 *            true:自动提交
	 */
	public void setAutoCommit(boolean autoCommit) {
		this.autuoCommit = autoCommit;
		try {
			getConcurrentConnect().setAutoCommit(autoCommit);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/** 提交事务 */
	private void toCommit() {
		Connection connection = connect.get();
		try {
			if(connection!=null && !connection.isClosed()){
				connection.commit();
				connection.close();
			}
			connect.remove();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** 提交事务 */
	public void commit() {
		toCommit();
		printLogger("====当前事务被提交===");
	}

	/** 回滚事务 */
	public void rollBack() {
	}

	/**
	 * 自定义语句分页查询
	 * 
	 * @param sql
	 *            自定义sql
	 * @param currentPageParam
	 *            当前页
	 * @param showCountParam
	 *            每页显示条目数
	 * @return	返回分页数据和分页信息
	 */
	@SuppressWarnings("rawtypes")
	public EasyApiBindSqlResult<Map> queryListPage(String sql, Integer currentPageParam, Integer showCountParam) {
		Object[] objs = new Object[] {};
		return queryListPage(sql, currentPageParam, showCountParam, objs);
	}

	/**
	 * 自定义语句分页查询
	 * 
	 * @param sql
	 *            自定义sql
	 * @param currentPageParam
	 *            当前页
	 * @param showCountParam
	 *            每页显示条目数
	 * @param params
	 *            sql参数
	 * @return	返回分页数据
	 */
	@SuppressWarnings("rawtypes")
	public EasyApiBindSqlResult<Map> queryListPage(String sql, Integer currentPageParam, Integer showCountParam,
												   Object... params) {
		return queryListPage(sql, Map.class, currentPageParam, showCountParam, params);
	}

	/**
	 * 自定义语句分页查询
	 * 
	 * @param sql
	 *            自定义sql
	 * @param clazz
	 *            封装的返回值类型
	 * @param currentPageParam
	 *            当前页
	 * @param showCountParam
	 *            每页显示条目数
	 * @return	返回分页数据
	 */
	public <T> EasyApiBindSqlResult<T> queryListPage(String sql, Class<T> clazz, Integer currentPageParam,
													 Integer showCountParam) {
		Object[] objs = new Object[] {};
		return queryListPage(sql, clazz, currentPageParam, showCountParam, objs);
	}

	/**
	 * 自定义语句分页查询
	 * 
	 * @param sql
	 *            自定义sql
	 * @param clazz
	 *            封装的返回值类型
	 * @param currentPageParam
	 *            当前页
	 * @param showCountParam
	 *            每页显示条目数
	 * @param params
	 *            sql参数
	 * @return	返回分页数据
	 */
	public <T> EasyApiBindSqlResult<T> queryListPage(String sql, Class<T> clazz, Integer currentPageParam,
													 Integer showCountParam, Object... params) {
		if (sql != null && !sql.trim().toUpperCase().startsWith("SELECT")) {
			throw new RuntimeException("当前非查询语句");
		}
		String countSql = "select count(1) from (" + sql + ") tmp";
		int totalResult = executeCount(executeSql(countSql, Arrays.asList(params))).intValue();
		EasyApiBindSqlResult<T> easySqlResult = new EasyApiBindSqlResult<>();
		if (totalResult == 0) {
			return easySqlResult;
		}
		EasyapiBindPage page = new EasyapiBindPage();
		page.setShowCount(showCountParam);
		page.setCurrentPage(currentPageParam);
		// 设置总记录数
		page.setTotalResult(totalResult);
		// 设置总页数
		page.setTotalPage((totalResult - 1) / page.getShowCount() + 1);
		Integer currentPage = page.getCurrentPage();
		Integer showCount = page.getShowCount();
		// // 开始的数据
		// Integer start = (currentPage - 1) * showCount;
		// // 结束的数据
		// Integer end = showCount;
		// // 如果当前页大于总页数,则返回最后一页
		// if (start >= totalResult) {
		// start = (page.getTotalPage() - 1) * showCount;
		// end = -1;
		// }
		// start = start < 0 ? 0 : start;
		// String limitSql = sql + " limit " + start + "," + end;
		String limitSql = getListPageSql(sql, currentPage, showCount);
		List<T> list = new ArrayList<T>();
		List<Map<String, Object>> result = query(limitSql, params);
		if (result.size() > 0) {
			list = mapToBean(result, clazz);
			easySqlResult.setResultList(list);
			easySqlResult.setPage(page);
		}
		return easySqlResult;
	}

	/**
	 * 自定义语句查询
	 * 
	 * @param sql
	 *            自定义sql
	 *
	 * @return	自定义sql的参数
	 */
	public Map<String, Object> queryOne(String sql) {
		Object[] objs = new Object[] {};
		return queryOne(sql, objs);
	}

	/**
	 * 自定义语句查询
	 * 
	 * @param sql
	 *            自定义sql
	 * @param params
	 *            自定义sql的参数
	 * @return	返回自定义查询结果
	 */
	public Map<String, Object> queryOne(String sql, Object... params) {
		if (sql.toUpperCase().contains("LIMIT")) {
			sql = sql.substring(0, sql.indexOf("limit")) + " limit 1";
		} else {
			sql = sql + " limit 1";
		}
		List<Map<String, Object>> list = query(sql, params);
		if (list != null && list.size() == 0) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * 自定义语句查询
	 * 
	 * @param sql
	 * @return
	 */
	public <T> T queryOne(String sql, Class<T> clazz) {
		Object[] objs = new Object[] {};
		return queryOne(sql, clazz, objs);
	}

	/**
	 * 自定义语句查询
	 * 
	 * @param sql
	 *            自定义sql
	 * @param clazz
	 *            返回值类型
	 * @param params
	 *            自定义sql的参数
	 * @return
	 */
	public <T> T queryOne(String sql, Class<T> clazz, Object... params) {
		sql = sql + " limit 1";
		List<T> list = query(sql, clazz, params);
		if (list == null || list.size() == 0) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * 自定义语句查询
	 * 
	 * @param sql
	 *            自定义sql
	 * @param params
	 *            自定义sql的参数
	 * @return	返回结果集
	 */
	public List<Map<String, Object>> query(String sql, Object... params) {
		if (sql != null && !sql.trim().toUpperCase().startsWith("SELECT")) {
			throw new RuntimeException("当前非查询语句");
		}
		List<Object> list = Arrays.asList(params);
		List<Map<String, Object>> result = executeQuery(executeSql(sql, list));
		toCommit();
		if (result == null) {
			result = new ArrayList<Map<String, Object>>();
		}
		return result;
	}

	/**
	 * 自定义语句查询
	 * 
	 * @param sql
	 * @return
	 */
	public List<Map<String, Object>> query(String sql) {
		Object[] objs = new Object[] {};
		return query(sql, objs);
	}

	/**
	 * 自定义语句查询
	 * 
	 * @param sql
	 * @return	返回结果集
	 */
	public <T> List<T> query(String sql, Class<T> clazz) {
		Object[] objs = new Object[] {};
		return query(sql, clazz, objs);
	}

	public <T> List<T> customQuery(String sql, Class<T> clazz){
		Object[] objs = new Object[] {};
		return customQuery(sql, clazz, objs);
	}
	public List<Map<String, Object>> customQuery(String sql) {
		Object[] objs = new Object[] {};
		return customQuery(sql, objs);
	}
	public List<Map<String, Object>> customQuery(String sql, Object... params) {
		String uSql = sql.trim().toUpperCase();
		if (sql != null && !uSql.startsWith("SELECT") && !uSql.startsWith("CREATE")) {
			throw new RuntimeException("当前非查询语句");
		}
		List<Object> list = Arrays.asList(params);
		List<Map<String, Object>> result = executeQuery(executeSql(sql, list));
		
		toCommit();
		if (result == null) {
			result = new ArrayList<Map<String, Object>>();
		}
		return result;
	}
	
	/**
	 * 自定义语句查询
	 * 
	 * @param sql
	 *            自定义sql
	 * @param clazz
	 *            返回值类型
	 * @param params
	 *            自定义sql的参数
	 * @return
	 */
	public <T> List<T> customQuery(String sql, Class<T> clazz, Object... params) {
		List<Object> list = Arrays.asList(params);
		List<Map<String, Object>> result = executeQuery(executeSql(sql, list));
		
		toCommit();
		if (result == null) {
			result = new ArrayList<Map<String, Object>>();
		}
		return mapToBean(result, clazz);
	}
	
	/**
	 * 自定义语句查询
	 * 
	 * @param sql
	 *            自定义sql
	 * @param clazz
	 *            返回值类型
	 * @param params
	 *            自定义sql的参数
	 * @return
	 */
	public <T> List<T> query(String sql, Class<T> clazz, Object... params) {
		if (sql != null && !sql.trim().toUpperCase().startsWith("SELECT")) {
			throw new RuntimeException("当前非查询语句");
		}
		List<Object> list = Arrays.asList(params);
		List<Map<String, Object>> result = executeQuery(executeSql(sql, list));
		
		toCommit();
		if (result == null) {
			result = new ArrayList<Map<String, Object>>();
		}
		return mapToBean(result, clazz);
	}

	/**
	 * 自定义语句修改
	 * 
	 * @param sql
	 *            自定义sql
	 * @param params
	 *            自定义sql的参数
	 * @return
	 */
	public int update(String sql, Object... params) {
		if (sql != null && sql.trim().toUpperCase().startsWith("SELECT")) {
			throw new RuntimeException("当前非修改语句");
		}
		List<Object> list = Arrays.asList(params);
		int count = executeUpdate(executeSql(sql, list));
		
		toCommit();
		return count;
	}

	/**
	 * 自定义语句修改
	 * 
	 * @param sql
	 * @return
	 */
	public int update(String sql) {
		Object[] objs = new Object[] {};
		return update(sql, objs);
	}

	/**
	 * 依据不为空的条件查询多条数据
	 * 
	 * @return
	 */
	public <T> List<T> select(T obj) {
		return select(new EasyapiBindSQLExecuter(obj));
	}

	/**
	 * 依据不为空的字段查询,支持排序
	 * 
	 * @param obj
	 * @param orderByTfd
	 * @param desc
	 * @return
	 */
	public <T> List<T> select(T obj, String orderByTfd, boolean desc) {
		return select(new EasyapiBindSQLExecuter(obj).orderBy(orderByTfd, desc));
	}

	/**
	 * 依据不为空的条件查询多条数据
	 * 
	 * @return
	 */
	public <T> T selectOne(T obj) {
		return selectOne(obj, null, false);
	}

	/**
	 * 依据不为空的条件查询多条数据
	 * 
	 * @return
	 */
	public <T> T selectOne(EasyapiBindSQLExecuter executer) {
		List<T> list = select(executer.setLimitStart(1));
		if (list == null || list.size() == 0) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * 依据不为空属性查询，支持聚合与排序
	 * 
	 * @param obj
	 * @param orderByTfd
	 * @param desc
	 * @return
	 */
	public <T> T selectOne(T obj, String orderByTfd, boolean desc) {
		EasyapiBindSQLExecuter executer = new EasyapiBindSQLExecuter(obj);
		if (orderByTfd != null) {
			executer.orderBy(orderByTfd, desc);
		}
		executer.setLimitStart(0);
		executer.setLimitSize(1);
		List<T> list = select(executer);
		if (list == null || list.size() == 0) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * 查询
	 * 
	 * @param executer
	 *            sql执行器
	 * @return
	 */
	public <T> List<T> select(EasyapiBindSQLExecuter executer) {
		if (executer.getBean() == null) {
			throw new RuntimeException("参数对象不能为空");
		}
		SqlStatement st = creatSelectSql(executer);
		List<Map<String, Object>> list = executeQuery(executeSql(st.getSql().toString(), st.getParams()));
		toCommit();
		if (list == null) {
			return new ArrayList<T>();
		}
		Object obj = executer.getBean();
		@SuppressWarnings("unchecked")
		List<T> resulTs = (List<T>) mapToBean(list, obj);
		return resulTs;
	}

	/**
	 * 查询总条数
	 * 
	 * @param obj
	 * @return
	 */
	public <T> Double selectCount(T obj) {
		return selectCount(new EasyapiBindSQLExecuter(obj));
	}

	/**
	 * 查询总条数
	 * 
	 * @return
	 */
	public <T> Double selectCount(EasyapiBindSQLExecuter executer) {
		if (executer.getBean() == null) {
			throw new RuntimeException("参数对象不能为空");
		}
		T obj = executer.getBean();
		String Tne = getTne(obj);
		Map<String, Object> map = getAttributes(obj);
		String rule = executer.getReturnParam();
		if(rule==null || rule.length()==0){
			rule = " COUNT(1) ";
		}
		StringBuffer sql = new StringBuffer("select "+rule+" from " + Tne + " where 1=1 ");
		Set<Entry<String, Object>> entry = map.entrySet();
		List<Object> params = new ArrayList<Object>();
		for (Entry<String, Object> en : entry) {
			sql.append(" and " + en.getKey() + "=? ");
			params.add(en.getValue());
		}
		if (executer.getSql() != null && executer.getSql().length() > 0) {
			sql.append(executer.getSql());
			params.addAll(executer.getParams());
		}
		Double count = executeCount(executeSql(sql.toString(), params));
		toCommit();
		printLogger("==============================");
		printLogger("执行查询语句:" + sql.toString().replace("*", "count(1)"));
		printLogger("执行参数为:" + params);
		printLogger("==============================");
		return count;
	}

	/** 拼装基础查询语句 */
	private SqlStatement creatSelectSql(EasyapiBindSQLExecuter executer) {
		SqlStatement st = new SqlStatement();
		Object obj = executer.getBean();
		String Tne = getTne(obj);
		if (Tne == null) {
			throw new RuntimeException("数据库表名为空，或无@Tne注解");
		}
		Map<String, Object> attributes = getAttributes(obj);
		String returnParam = " * ";
		if (executer.getReturnParam() != null && executer.getReturnParam().length() > 0) {
			returnParam = executer.getReturnParam();
		}
		StringBuffer sql = new StringBuffer("select " + returnParam + " from " + Tne + " where 1=1 ");
		List<Object> params = new ArrayList<Object>();
		for (String key : attributes.keySet()) {
			sql.append("and " + key + "=? ");
			params.add(attributes.get(key));
		}
		if (executer.getSql() != null) {
			sql.append(executer.getSql());
			params.addAll(executer.getParams());
		}
		if (executer.getLimitStart()!=null && executer.getLimitStart()!=0) {
			sql.append(" limit " + executer.getLimitStart());
			if (executer.getLimitSize()!=null && executer.getLimitSize()!=0) {
				sql.append("," + executer.getLimitSize());
			}
		}
		st.setParams(params);
		st.setSql(sql);
		return st;
	}

	/**
	 * 分页查询（依据不为空的条件查询） 查询完毕将page信息回填到参数bean的page属性中
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> EasyApiBindSqlResult<T> listPage(EasyapiBindSQLExecuter executer, Integer currentPageParam, Integer showCountParam) {
		EasyApiBindSqlResult<T> easySqlResult = new EasyApiBindSqlResult<T>();
		if (showCountParam <= 0) {
			throw new RuntimeException("每页显示条数不能小于0");
		}
		SqlStatement st = creatSelectSql(executer);
		// 获取总记录数
		int totalResult = executeCount(
				executeSql("select count(1) from (" + st.getSql().toString() + ") tmp", st.getParams())).intValue();
		if (totalResult == 0) {
			return easySqlResult;
		}
		EasyapiBindPage page = new EasyapiBindPage();
		page.setShowCount(showCountParam);
		page.setCurrentPage(currentPageParam);
		// 设置总记录数
		page.setTotalResult(totalResult);
		// 设置总页数
		page.setTotalPage((totalResult - 1) / page.getShowCount() + 1);
		Integer currentPage = page.getCurrentPage();
		Integer showCount = page.getShowCount();
		String limitSql = getListPageSql(st.getSql().toString(), currentPage, showCount);
		List<T> list = (List<T>) query(limitSql, executer.getBean().getClass(), st.getParams().toArray());
		if (list.size() > 0) {
			easySqlResult.setResultList(list);
			easySqlResult.setPage(page);
		}
		return easySqlResult;
	}

	/**
	 * 分页查询，支持排序
	 * 
	 * @param obj
	 * @param currentPageParam
	 * @param showCountParam
	 * @param orderByTfd
	 * @param desc
	 * @return
	 */
	public <T> EasyApiBindSqlResult<T> listPage(T obj, Integer currentPageParam, Integer showCountParam,
												String orderByTfd, boolean desc) {
		return listPage(new EasyapiBindSQLExecuter(obj).orderBy(orderByTfd, desc), currentPageParam, showCountParam);
	}

	/**
	 * 分页查询，支持聚合
	 * 
	 * @param obj
	 * @param currentPageParam
	 * @param showCountParam
	 * @return
	 */
	public <T> EasyApiBindSqlResult<T> listPage(T obj, Integer currentPageParam, Integer showCountParam) {
		return listPage(new EasyapiBindSQLExecuter(obj), currentPageParam, showCountParam);
	}

	/**
	 * 拼接分页
	 * 
	 * @param sql
	 * @param currentPage
	 * @param showCount
	 * @return
	 */
	private String getListPageSql(String sql, int currentPage, int showCount) {
		StringBuffer pageSql = new StringBuffer();
		if ("ORACLE".equals(driver)) {
			pageSql.append("select * from (select tmp_tb.*,ROWNUM row_id from (");
			pageSql.append(sql);
			pageSql.append(") as tmp_tb where ROWNUM<=");
			pageSql.append((currentPage - 1) * showCount + showCount);
			pageSql.append(") where row_id>");
			pageSql.append((currentPage - 1) * showCount);
		} else if ("MYSQL".equalsIgnoreCase(driver)) {
			pageSql.append(sql);
			pageSql.append(" limit ").append((currentPage - 1) * showCount).append(",").append(showCount);
		}
		return pageSql.toString();
	}

	/**
	 * 一次插入多条
	 * 
	 * @param objs
	 * @return
	 */
	public <T> void insert(Collection<T> objs) {
		if (objs == null || objs.size() == 0) {
			throw new RuntimeException("要插入的数据不能为空");
		}
		Iterator<T> it = objs.iterator();
		T obj = null;
		if (it.hasNext()) {
			obj = it.next();
		}
		String Tne = getTne(obj);
		if (Tne == null) {
			throw new RuntimeException("数据库表名为空，或无@Tne注解");
		}
		Map<String, Object> attributes = getAttributes(obj);
		// 不能添加空数据
		if (attributes == null || attributes.isEmpty()) {
			throw new RuntimeException("数据库表不允许添加空数据");
		}
		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		Set<String> keys = attributes.keySet();
		sql.append("insert into " + Tne + " (");
		for (String key : keys) {
			sql.append("`" + key + "` ,");
		}
		sql.delete(sql.length() - 1, sql.length());
		sql.append(" )values");
		Iterator<T> iterator = objs.iterator();
		// 循环添加
		while (iterator.hasNext()) {
			sql.append("( ");
			for (@SuppressWarnings("unused")
			String key : keys) {
				sql.append(" ?,");
			}
			Map<String, Object> attr = getAttributes(iterator.next());
			for (String key : keys) {
				params.add(attr.get(key));
			}
			sql.delete(sql.length() - 1, sql.length());
			sql.append(" ),");
		}
		sql.delete(sql.length() - 1, sql.length());
		executeInsert(sql.toString(), params);
		
		toCommit();
	}

	/**
	 * 添加数据(自动补填id)
	 * 
	 * @param obj
	 * @return
	 */
	public <T> T insert(T obj) {
		// 自增主键不允许加入
		if (getTidValue(obj) != null) {
			throw new RuntimeException("主键:" + getTidName(obj) + "不允许作为参数");
		}
		String Tne = getTne(obj);
		if (Tne == null) {
			throw new RuntimeException("数据库表名为空，或无@Tne注解");
		}
		if(BaseBean.class.isAssignableFrom(obj.getClass())){
			BaseBean b = (BaseBean) obj;
			b.setCreateTime(System.currentTimeMillis());
			b.setDeleted(0);
		}
		Map<String, Object> attributes = getAttributes(obj);
		// 不能添加空数据
		if (attributes == null || attributes.isEmpty()) {
			throw new RuntimeException("数据库表不允许添加空数据");
		}
		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();
		sql.append("insert into " + Tne + " (");
		for (String key : attributes.keySet()) {
			sql.append("`" + key + "` ,");
			params.add(attributes.get(key));
		}
		sql.delete(sql.length() - 1, sql.length());
		sql.append(" )values( ");
		for (@SuppressWarnings("unused")
		String key : attributes.keySet()) {
			sql.append(" ?,");
		}
		sql.delete(sql.length() - 1, sql.length());
		sql.append(" )");
		Object id = executeInsert(sql.toString(), params);
		
		toCommit();
		String TidName = getTidJavaName(obj);
		try {
			if (TidName == null) {
				printLogger("数据插入成功,但未找到@Tid 注解,无法将主键返回");
				return obj;
			}
			setAttribute(obj, TidName, id);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return obj;
	}

	/**
	 * 给对象的属性赋值
	 * 
	 * @param obj
	 *            对象
	 * @param attrName
	 *            对象的属性名
	 * @param value
	 *            对象的属性值
	 */
	@SuppressWarnings("rawtypes")
	private void setAttribute(Object obj, String attrName, Object value) {
		try {
			Class clazz = obj.getClass();
			while (!clazz.equals(Object.class)) {
				try {
					Field f = clazz.getDeclaredField(attrName);

					if (f == null) {
						continue;
					}
					f.setAccessible(true);
					if (value == null || f.getType().isAssignableFrom(value.getClass())) {
						f.set(obj, value);
					} else {
						f.set(obj, parseToObject(value, f.getType()));
					}
					f.setAccessible(false);

					return;
				} catch (NoSuchFieldException e) {
					clazz = clazz.getSuperclass();
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/***
	 * 转换类型
	 * 
	 * @param value
	 *            字符串的值
	 * @param type
	 *            要转换的类型
	 * @return 转换后的值
	 */
	@SuppressWarnings("unchecked")
	private <T> T parseToObject(Object value, Class<T> type) {
		Object result = null;
		if (value == null || type == String.class) {
			result = value == null ? null : value.toString();
		} else if (type == Character.class || type == char.class) {
			char[] chars = value.toString().toCharArray();
			result = chars.length > 0 ? chars.length > 1 ? chars : chars[0] : Character.MIN_VALUE;
		} else if (type == Boolean.class || type == boolean.class) {
			result = Boolean.parseBoolean(value.toString());
		}
		// 处理boolean值转换
		else if (type == Double.class || type == double.class) {
			result = value.toString().equalsIgnoreCase("true") ? true
					: value.toString().equalsIgnoreCase("false") ? false : value;
		} else if (type == Long.class || type == long.class) {
			result = Long.parseLong(value.toString());
		} else if (type == Integer.class || type == int.class) {
			result = Integer.parseInt(value.toString());
		} else if (type == Double.class || type == double.class) {
			result = Double.parseDouble(value.toString());
		} else if (type == Float.class || type == float.class) {
			result = Float.parseFloat(value.toString());
		} else if (type == Byte.class || type == byte.class) {
			result = Byte.parseByte(value.toString());
		} else if (type == Short.class || type == short.class) {
			result = Short.parseShort(value.toString());
		}
		return (T) result;
	}

	/**
	 * 修改数据（依据id修改）
	 * 
	 * @return
	 */
	public <T> Integer update(EasyapiBindSQLExecuter executer) {
		Object obj = executer.getBean();
		if (obj == null) {
			throw new RuntimeException("参数对象不能为空");
		}
		String Tne = getTne(obj);
		if (Tne == null) {
			throw new RuntimeException("数据库表名为空，或无@Tne注解");
		}
		Object TidValue = null;
		// 参数中的主键
		Object param_id = getTidName(obj);
		if (param_id != null) {
			TidValue = getTidValue(obj);
		}
		// id不能为空
		if (TidValue == null && executer.getSql() == null) {
			throw new RuntimeException("主键值为空，或无@Tid注解");
		}
		Map<String, Object> attributes = getAttributes(obj);
		attributes.remove(param_id);
		if (attributes == null || attributes.isEmpty()) {
			throw new RuntimeException("参数属性为空");
		}
		StringBuffer sql = new StringBuffer();
		sql.append("update " + Tne + " set ");
		for (String key : attributes.keySet()) {
			sql.append("`" + key + "`" + " = ? ,");
		}
		// 添加自增字段
		if (!executer.getIncrMap().isEmpty()) {
			Map<String, Object> incrMap = executer.getIncrMap();
			Set<Entry<String, Object>> set = incrMap.entrySet();
			for (Entry<String, Object> entry : set) {
				sql.append("`" + entry.getKey() + "`" + " = " + "`" + entry.getKey() + "+`" + entry.getValue() + ",");
			}
		}
		//添加null值
		if(!executer.getNullValFields().isEmpty()){
			List<String> nullVal = executer.getNullValFields();
			for (String field : nullVal) {
				sql.append("`" + field + "`" + " = NULL,");
			}
		}
		sql.delete(sql.length() - 1, sql.length());
		List<Object> params = new ArrayList<Object>();
		for (String key : attributes.keySet()) {
			params.add(attributes.get(key));
		}
		if (executer.getSql() != null) {
			sql.append(" where 1=1 ").append(executer.getSql());
			params.addAll(executer.getParams());
			if (param_id != null && TidValue != null) {
				sql.append(" and " + param_id + " = ?");
				params.add(TidValue);
			}
		} else {
			sql.append(" where " + param_id + " = ?");
			params.add(TidValue);
		}
		int num = update(sql.toString(), params.toArray());
		
		toCommit();
		return num;
	}

	public <T> Integer update(T obj) {
		if(obj instanceof BaseBean){
			BaseBean b = (BaseBean) obj;
			b.setUpdateTime(System.currentTimeMillis());
		}
		return update(new EasyapiBindSQLExecuter(obj));
	}

	/**
	 * 删除数据(依据不为空的属性删除,支持聚合)
	 * 
	 * @return 影响行数
	 */
	public <T> Integer delete(EasyapiBindSQLExecuter executer) {
		Object obj = executer.getBean();
		String Tne = getTne(obj);
		if (Tne == null) {
			return null;
		}
		Map<String, Object> attributes = getAttributes(obj);
		if (executer.getSql() == null && (attributes == null || attributes.isEmpty())) {
			throw new RuntimeException("属性为空,暂不允许删除全表数据");
		}
		StringBuffer sql = new StringBuffer();
		sql.append("delete from " + Tne + " where 1=1 ");
		for (String key : attributes.keySet()) {
			sql.append("and " + key + " =? ");
		}
		List<Object> params = new ArrayList<Object>();
		for (String key : attributes.keySet()) {
			params.add(attributes.get(key));
		}
		if (executer.getSql() != null) {
			sql.append(executer.getSql());
			params.addAll(executer.getParams());
		}
		int num = update(sql.toString(), params.toArray());
		
		toCommit();
		return num;
	}

	/**
	 * 依据不为空的属性删除数据
	 * 
	 * @param obj
	 * @return
	 */
	public <T> Integer delete(T obj) {
		return delete(new EasyapiBindSQLExecuter(obj));
	}

	/**
	 * 获取表名
	 * 
	 * @param <T>
	 */
	private <T> String getTne(T t) {
		Class<?> tClass = t.getClass();
		String Tne = "";
		// 获取表名注解
//		Tne annotation = tClass.getDeclaredAnnotation(Tne.class);
		Map<String, cn.easyutil.easyapi.datasource.annotations.Tne> map = getClassAnnotation(tClass, Tne.class);
		Tne annotation = null;
		if(map!=null && !map.isEmpty()){
			annotation = map.get(tClass.getName());
		}
		if (annotation != null) {
			if (!annotation.name().equals("")) {
				Tne = annotation.name();
			}
			if (!annotation.value().equals("")) {
				Tne = annotation.value();
			}
			if (!Tne.equals("")) {
				return Tne;
			}
		}
		return null;
	}

	/**
	 * 获取主键id的名称
	 * 
	 * @param <T>
	 */
	@SuppressWarnings("rawtypes")
	private <T> String getTidName(Object t) {
		String Tid = null;
		for (Class clazz = t.getClass(); !clazz.equals(Object.class); clazz = clazz.getSuperclass()) {
			Field[] fs = clazz.getDeclaredFields();
			for (Field f : fs) {
				// 获取Tid注解
				Tid annotation = f.getDeclaredAnnotation(Tid.class);
				Tfd Tfd = f.getDeclaredAnnotation(Tfd.class);
				if (annotation != null) {
					if (Tfd != null) {
						if (!Tfd.name().equals("")) {
							Tid = Tfd.name();
						} else if (!Tfd.value().equals("")) {
							Tid = Tfd.value();
						}
					} else {
						Tid = f.getName();
					}
				}
				if (Tid != null) {
					return Tid;
				}
			}
		}
		return null;
	}

	/**
	 * 获取主键id的值
	 * 
	 * @param <T>
	 */
	@SuppressWarnings("rawtypes")
	private <T> Object getTidValue(Object t) {
		for (Class clazz = t.getClass(); !clazz.equals(Object.class); clazz = clazz.getSuperclass()) {
			Field[] fs = clazz.getDeclaredFields();
			for (Field f : fs) {
				// 获取Tid注解
				Tid annotation = f.getDeclaredAnnotation(Tid.class);
				if (annotation != null) {
					f.setAccessible(true);
					try {
						return f.get(t);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		return null;
	}

	/**
	 * 获取原类属性id的名字
	 * 
	 * @param <T>
	 */
	@SuppressWarnings("rawtypes")
	private <T> String getTidJavaName(Object t) {
		for (Class clazz = t.getClass(); !clazz.equals(Object.class); clazz = clazz.getSuperclass()) {
			Field[] fs = clazz.getDeclaredFields();
			for (Field f : fs) {
				// 获取Tid注解
				Tid annotation = f.getDeclaredAnnotation(Tid.class);
				if (annotation != null) {
					return f.getName();
				}
			}
		}
		return null;
	}

	/**
	 * 获取对象中的所有属性
	 * 
	 * @param bean
	 *            对象
	 * @return 属性和值(Map[属性名, 属性值])
	 */
	@SuppressWarnings("rawtypes")
	private Map<String, Object> getAttributes(Object bean) {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			// 主键id字段是否被改变
			boolean changeTid = false;
			for (Class clazz = bean.getClass(); !clazz.equals(Object.class); clazz = clazz.getSuperclass()) {
				Field[] fs = clazz.getDeclaredFields();
				for (Field f : fs) {
					f.setAccessible(true);
					if (f.get(bean) == null || (changeTid && f.getName().equals("id"))) {
						f.setAccessible(false);
						continue;
					}
					// 如果该字段被标注忽略，则不往数据库添加此字段
					Tie tie = f.getDeclaredAnnotation(Tie.class);
					if (tie != null) {
						continue;
					}
					// 如果该字段被标注，则不从basebean中取id
					Tid tid = f.getDeclaredAnnotation(Tid.class);
					// 如果当前类里面属性含有自定义注解，则处理属性名
					// if (clazz == bean.getClass()) {
					Tfd tfd = f.getDeclaredAnnotation(Tfd.class);
					if (tid != null && tfd != null && !tfd.name().equals("")) {
						changeTid = true;
					}
					if (tfd != null) {
						String TfdName = "";
						if (!tfd.name().equals("")) {
							TfdName = tfd.name();
						}
						if (!tfd.value().equals("")) {
							TfdName = tfd.value();
						}
						if (TfdName.equals("")) {
							continue;
						}
						f.setAccessible(true);
						map.put(TfdName, f.get(bean));
						f.setAccessible(false);
						continue;
					}
					// }
					// 子类最大，父类值不覆盖子类
					if (map.containsKey(f.getName())) {
						continue;
					}
					f.setAccessible(true);
					Object value = f.get(bean);
					f.setAccessible(false);
					if (value == null) {
						continue;
					}
					map.put(f.getName(), value);
				}
			}
			map.remove("serialVersionUID");
			return map;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 设置是否打印日志
	 * 
	 * @param doLogger
	 */
	public void printLogger(boolean doLogger) {
		this.doLogger = doLogger;
	}

	private void printLogger(String message) {
		if (this.doLogger) {
//			LoggerUtil.info(this.getClass(), message);
		}
	}

	/**
	 * 设置连接类型为oracle
	 */
	public void setOracleDriver() {
		this.driver = "ORACLE";
	}

	/***
	 * 校验是否是九种基础类型(即：非用户定义的类型)
	 * 
	 * @param value
	 *            字符串的值 要校验的值
	 * @return 是否是基础类型(true:已经是基础类型了)
	 */
	private boolean isBaseClass(@SuppressWarnings("rawtypes") Class value) {
		if (value == null) {
			return true;
		} else if (value.equals(Long.class)) {
			return true;
		} else if (value.equals(Integer.class)) {
			return true;
		} else if (value.equals(Double.class)) {
			return true;
		} else if (value.equals(Float.class)) {
			return true;
		} else if (value.equals(Byte.class)) {
			return true;
		} else if (value.equals(Boolean.class)) {
			return true;
		} else if (value.equals(Short.class)) {
			return true;
		} else if (value.equals(Character.class)) {
			return true;
		} else if (value.equals(String.class)) {
			return true;
		}
		return false;
	}

	/**
	 * 将map转成bean
	 * 
	 * @param list
	 * @param obj
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T> List<T> mapToBean(List<Map<String, Object>> list, T obj) {
		// 最终封装后的返回
		List<T> resulTs = new ArrayList<T>();
		try {
			for (Map<String, Object> temp : list) {
				// 每条结果都封装在新obj对象里
				Object t = obj.getClass().newInstance();
				for (Class clazz = t.getClass(); !clazz.equals(Object.class); clazz = clazz.getSuperclass()) {
					Field[] fields = clazz.getDeclaredFields();
					for (Field f : fields) {
						// 如果该类的属性名与表里不一致，则取表字段名
						Tfd Tfd = f.getDeclaredAnnotation(Tfd.class);
						try {
							if (Tfd != null) {
								String TfdName = "";
								if (!Tfd.name().equals("")) {
									TfdName = Tfd.name();
								}
								if (!Tfd.value().equals("")) {
									TfdName = Tfd.value();
								}
								if (TfdName.equals("")) {
									continue;
								}
								Object value = temp.get(TfdName);
								if(value == null){
									continue;
								}
								if(Clob.class.isAssignableFrom(value.getClass())){
									value = ClobUtil.getStr((Clob) value);
								}
								f.setAccessible(true);
								f.set(t, value);
								f.setAccessible(false);
							}
							if (temp.get(f.getName()) != null) {
								f.setAccessible(true);
								f.set(t, temp.get(f.getName()));
								f.setAccessible(false);
							}
						} catch (Exception e) {
							throw new RuntimeException("类属性" + f.getName() + "与表字段类型不符", e);
						}
					}
				}
				resulTs.add((T) t);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return resulTs;
	}
	
	/**
	 * 获取类上指定的注解
	 * @param clazz 类
	 * @return key:类名 val:类上注解实例
	 */
	public static <T> Map<String, T> getClassAnnotation(Class clazz,Class<T> annotation){
		Map<String,T> map = new HashMap<String,T>();
		for (Class cl=clazz;!cl.equals(Object.class);cl=cl.getSuperclass()) {
			Annotation[] ans = cl.getDeclaredAnnotations();
			for(Annotation an : ans){
				if(an.annotationType().equals(annotation)){
					@SuppressWarnings("unchecked")
					T t = (T) an;
					map.put(cl.getName(), t);
				}
			}
		}
		return map;
	}

	/**
	 * 将map转成bean
	 * 
	 * @param list
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> mapToBean(List<Map<String, Object>> list, Class<T> clazz) {
		try {
			if (list.size() == 0) {
				return new ArrayList<>();
			}
			if (isBaseClass(clazz)) {
				if (list.size() > 0 && list.get(0).size() == 1) {
					List<T> result = new ArrayList<T>();
					T value = null;
					String mapKey = null;
					for (String key : list.get(0).keySet()) {
						mapKey = key;
						value = (T) list.get(0).get(key);
					}
					for (Map<String, Object> map : list) {
						value = (T) map.get(mapKey);
						result.add(value);
					}
					return result;
				}
			}
			if (clazz.equals(Map.class)) {
				return (List<T>) list;
			}
			T t = clazz.newInstance();
			return mapToBean(list, t);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	class SqlStatement {
		private StringBuffer sql;
		private List<Object> params;

		public StringBuffer getSql() {
			return sql;
		}

		public void setSql(StringBuffer sql) {
			this.sql = sql;
		}

		public List<Object> getParams() {
			return params;
		}

		public void setParams(List<Object> params) {
			this.params = params;
		}
	}
}
