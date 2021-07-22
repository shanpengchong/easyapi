package cn.easyutil.easyapi.interview.controller;

import cn.easyutil.easyapi.datasource.EasyapiBindSqlExecution;
import cn.easyutil.easyapi.datasource.bean.EasyapiBindSQLExecuter;
import cn.easyutil.easyapi.entity.auth.AuthMoudle;
import cn.easyutil.easyapi.entity.auth.User;
import cn.easyutil.easyapi.entity.auth.UserProject;
import cn.easyutil.easyapi.entity.common.ApidocComment;
import cn.easyutil.easyapi.entity.doc.*;
import cn.easyutil.easyapi.filter.ReadBeanApiFilter;
import cn.easyutil.easyapi.interview.dto.SyncRemoteAll;
import cn.easyutil.easyapi.interview.dto.SyncRemoteInterface;
import cn.easyutil.easyapi.interview.dto.UpdateReqOrResParamDto;
import cn.easyutil.easyapi.interview.entity.HttpEntity;
import cn.easyutil.easyapi.interview.entity.ResponseBody;
import cn.easyutil.easyapi.interview.entity.SyncFileBean;
import cn.easyutil.easyapi.javadoc.html.CreateHtml;
import cn.easyutil.easyapi.logic.EasyapiRun;
import cn.easyutil.easyapi.logic.MockExecuter;
import cn.easyutil.easyapi.configuration.EasyapiConfiguration;
import cn.easyutil.easyapi.util.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 接口文档接口
 *
 * @author spc
 *
 */
@ApidocComment(ignore = true)
@RestController
@RequestMapping("/easyapi/doc")
public class ApiDocController {

	@Autowired
	private EasyapiConfiguration configuration;

	@Autowired
	private EasyapiBindSqlExecution execution;

	private Map<Long,byte[]> docJs = new HashMap<>();

	@RequestMapping("/downJS")
	public void downHtml(HttpServletResponse response,Long projectId) throws Exception {
		if (docJs.get(projectId) == null) {
			CreateHtml create = new CreateHtml(execution);
			docJs.put(projectId, create.createJs(projectId).getJs().getBytes());
		}
		byte[] jsBytes = docJs.get(projectId);
		// 清空response
		response.reset();
		// 设置response的Header
		response.addHeader("Content-Disposition", "attachment;filename=apidoc.js");
		response.addHeader("Content-Length", "" + jsBytes.length);
		response.getOutputStream().write(jsBytes);
		response.getOutputStream().flush();
	}

	/**
	 * 同步单个接口
	 * @authCode 10001
	 * @param controllerName    控制器名称
	 * @param interfaceName 接口名称
	 * @param address   同步地址
	 * @return
	 */
	@PostMapping("/manualSync")
	public ResponseBody manualSync(String controllerName,String interfaceName,String address) {
//		if(!hasAuth(AuthMoudle.manualSync)){
//			return ResponseBody.error("您未拥有此权限");
//		}
//		if (StringUtil.isEmpty(controllerName) || StringUtil.isEmpty(interfaceName) || StringUtil.isEmpty(address)) {
//			return ResponseBody.error();
//		}
//		if (!address.startsWith("http")) {
//			address = "http://" + address;
//		}
//		address = address +"/easyapi/doc/receiveSyncFile";
//		HttpUtil util = new HttpUtil(address);
//		util.setRequestMethod("POST");
//		try {
//			util.doUrl();
//		} catch (Exception e) {
//			return ResponseBody.error(address + "连接目标机器失败，请确认ip或端口是否正确," + e.getMessage());
//		}
//		//先同步接口到接口列表
//		String fileContent = FileUtil.getFileContent(new File(PathContext.apiInterfacePath + controllerName + ".json"));
//		if(StringUtil.isEmpty(fileContent)){
//			return ResponseBody.error("未找到相关接口");
//		}
//		List<InterfaceBean> list = JsonUtil.jsonToList(fileContent, InterfaceBean.class);
//		List<InterfaceBean> syncInteface = new ArrayList<>();
//		for (InterfaceBean apiInterfaceBean: list) {
//			if(apiInterfaceBean.getJavaName().equals(interfaceName)){
//				syncInteface.add(apiInterfaceBean);
//			}
//		}
//		if(syncInteface.isEmpty()){
//			return ResponseBody.error("未找到相关接口");
//		}
//		SyncFileBean bean = new SyncFileBean();
//		bean.setAddress(address);
//		//同步接口
//		bean.setType(0);
//		bean.setJson(JsonUtil.beanToJson(syncInteface));
//		bean.setFileName(controllerName+".json");
//		sycnFile(bean);
//		//同步入参
//		bean.setType(1);
//		String fileName = controllerName+"."+interfaceName+".json";
//		String req = PathContext.apiRequestParamPath+fileName;
//		bean.setJson(FileUtil.getFileContent(new File(req)));
//		bean.setFileName(controllerName+"."+interfaceName+".json");
//		sycnFile(bean);
//		//同步返回
//		bean.setType(2);
//		String resp = PathContext.apiResponseParamPath+fileName;
//		bean.setFileName(fileName);
//		bean.setJson(FileUtil.getFileContent(new File(resp)));
//		//同步mock
//		bean.setType(3);
//		String mock = PathContext.apiResponseMockPath+fileName;
//		bean.setFileName(fileName);
//		bean.setJson(FileUtil.getFileContent(new File(mock)));
//		sycnFile(bean);
		return ResponseBody.successObj();
	}

	/**
	 * 手动同步到目标服务器
	 * @authCode 10002
	 * @param address  目标服务器
	 * @param username 账户名
	 * @param password 密码
	 */
	@PostMapping("/manualSyncAll")
	public ResponseBody manualSyncAll(String address, String username, String password) {
		if(!hasAuth(AuthMoudle.manualSyncAll)){
			return ResponseBody.error("您未拥有此权限");
		}
		if (StringUtil.isEmpty(address) || StringUtil.isEmpty(username) || StringUtil.isEmpty(password)) {
			return ResponseBody.error();
		}
		if (!address.startsWith("http")) {
			address = "http://" + address;
		}
		address = address + "/easyapi/doc/receiveSyncAll";
		HttpUtil util = new HttpUtil(address);
		util.setRequestMethod("POST");
		try {
			util.doUrl();
		} catch (Exception e) {
			return ResponseBody.error(address + "连接目标机器失败，请确认ip或端口是否正确," + e.getMessage());
		}
		//先同步基本信息
		SyncRemoteAll remoteAll = new SyncRemoteAll();
		remoteAll.setUsername(username);
		remoteAll.setPassword(password);
		//当前主项目信息
		InfoBean initProject = EasyapiRun.initProject;
		//查询当前controller
		List<ControllerBean> controllerBeans = execution.select(EasyapiBindSQLExecuter.build(new ControllerBean()).eq(ControllerBean::getProject_id, initProject.getId()));
		remoteAll.setControllerBeans(controllerBeans);
		//查询当前项目信息
		remoteAll.setProject(initProject);
		//查询当前返回外包装
		List<MockOutPackageBean> outPackageBeans = execution.select(EasyapiBindSQLExecuter.build(new MockOutPackageBean()).eq(MockOutPackageBean::getProjectId, initProject.getId()));
		remoteAll.setOutPackageBeans(outPackageBeans);
		//同步到远程


		//再逐个同步接口信息


		return ResponseBody.successObj();
	}

	/**
	 * 接收外来的同步文件请求
	 *
	 * @param remoteAll
	 */
	@PostMapping("/receiveSyncAll")
	public void receiveSyncAll(SyncRemoteAll remoteAll) {
		InfoBean project = remoteAll.getProject();
		getUserProject(remoteAll.getUsername(),remoteAll.getPassword(),project);
		List<ControllerBean> controllerBeans = remoteAll.getControllerBeans();
		if(!StringUtil.isEmpty(controllerBeans)){
			//先删除旧的文档，再添加新的
			execution.delete(EasyapiBindSQLExecuter.build(new ControllerBean()).eq(ControllerBean::getProject_id, project.getId()));
			controllerBeans.stream().forEach(c->c.setProject_id(project.getId()));
			execution.insert(controllerBeans);
		}

		List<MockOutPackageBean> outPackageBeans = remoteAll.getOutPackageBeans();
		if(!StringUtil.isEmpty(outPackageBeans)){
			//先删除旧的文档，再添加新的
			execution.delete(EasyapiBindSQLExecuter.build(new MockOutPackageBean()).eq(MockOutPackageBean::getProjectId, project.getId()));
			outPackageBeans.stream().forEach(c->c.setProjectId(project.getId()));
			execution.insert(outPackageBeans);
		}
	}

	@PostMapping("/receiveSyncInterface")
	public void receiveSyncInterface(SyncRemoteInterface remoteInterface){
		InfoBean project = new InfoBean();
		project.setName(remoteInterface.getProjectName());
		project = getUserProject(remoteInterface.getUsername(),remoteInterface.getPassword(),project);
		String controllerName = remoteInterface.getControllerName();
		InterfaceBean interfaceBean = remoteInterface.getInterfaceBean();
		ParamBean requestParam = remoteInterface.getRequestParam();
		MockBean responseMock = remoteInterface.getResponseMock();
		ParamBean responseParam = remoteInterface.getResponseParam();

		EasyapiBindSQLExecuter eq = EasyapiBindSQLExecuter.build(new InterfaceBean())
				.eq(InterfaceBean::getProject_id, project.getId())
				.eq(InterfaceBean::getController_name, controllerName)
				.eq(InterfaceBean::getJavaName, interfaceBean.getJavaName());
		execution.delete(eq);

		interfaceBean.setProject_id(project.getId());
		execution.insert(interfaceBean);


	}

	private InfoBean getUserProject(String username,String password,InfoBean project){
		if(StringUtil.isEmpty(username) || StringUtil.isEmpty(password)){
			ResponseBody.error("用户名或密码不正确");
		}
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user = execution.selectOne(user);
		if(user == null){
			ResponseBody.error("用户名或密码不正确");
		}
		UserProject up = new UserProject();
		up.setUserId(user.getId());
		List<UserProject> ups = execution.select(up);
		if(ups==null || ups.size()==0){
			if(project.getId() == null){
				return null;
			}
			project.setId(null);
			execution.insert(project);
			up.setProjectId(project.getId());
			execution.insert(up);
			return project;
		}
		List<Long> collect = ups.stream().map(u -> u.getProjectId()).collect(Collectors.toList());
		List<InfoBean> infos = execution.select(EasyapiBindSQLExecuter.build(new InfoBean()).in("id",collect ));
		if(infos==null || infos.size()==0){
			if(project.getId() == null){
				return null;
			}
			execution.insert(project);
			up.setProjectId(project.getId());
			execution.insert(up);
			return project;
		}
		InfoBean finalProject = project;
		InfoBean query = infos.stream().filter(i -> i.getName().equals(finalProject.getName())).findFirst().get();
		if(query == null){
			if(project.getId() == null){
				return null;
			}
			execution.insert(project);
			up.setProjectId(project.getId());
			execution.insert(up);
		}else{
			project = query;
		}
		return project;
	}


	/**
	 * 获取接口文档说明
	 * @authCode 20001
	 * @return
	 */
	@PostMapping("/getInfo")
	public ResponseBody getInfo(Long projectId) {
		if(!hasAuth(AuthMoudle.getInfo)){
			return ResponseBody.error("您未拥有此权限");
		}
//		File file = new File(PathContext.apiInfoPath);
//		if (!file.exists() || StringUtil.isEmpty(FileUtil.getFileContent(file))) {
//			initInfoFile(request.getLocalPort());
//			file = new File(PathContext.apiInfoPath);
//		}
//		String context = FileUtil.getFileContent(file);
//		if (!JsonUtil.isJson(context)) {
//			return ResponseBody.successObj();
//		}
		InfoBean infoBean = new InfoBean();
		if(projectId == null){
			infoBean.setInitProject(1);
		}
		infoBean.setId(projectId);
		infoBean = execution.selectOne(infoBean);
		List<HostBean> hosts = execution.select(EasyapiBindSQLExecuter.build(new HostBean()).eq(HostBean::getProjectId, infoBean.getId()));
		infoBean.setRequestHost(hosts);
		return ResponseBody.successObj(infoBean);
	}

	/**
	 * 获取返回数据外包装
	 * @authCode 20002
	 * @return
	 */
	@PostMapping("/getOutPackage")
	public ResponseBody getOutPackage(Long projectId) {
		if(!hasAuth(AuthMoudle.getOutPackage)){
			return ResponseBody.error("您未拥有此权限");
		}
		//		File file = new File(PathContext.apiOutPackagePath);
//		if (!file.exists() || StringUtil.isEmpty(FileUtil.getFileContent(file))) {
//			initOutPackageFile();
//			file = new File(PathContext.apiOutPackagePath);
//		}
//		String context = FileUtil.getFileContent(file);
//		if (!JsonUtil.isJson(context)) {
//			return ResponseBody.successObj();
//		}
//		return ResponseBody.successList(JsonUtil.jsonToList(context, MockOutPackageBean.class));
		projectId = getProjectId(projectId);
		List<MockOutPackageBean> select = execution.select(EasyapiBindSQLExecuter.build(new MockOutPackageBean()).eq(MockOutPackageBean::getProjectId, projectId));
		return ResponseBody.successList(select);
	}

	/**
	 * 修改返回数据外包装
	 * @authCode 20201
	 * @return
	 */
	@PostMapping("/updateOutPackage")
	public ResponseBody updateOutPackage(String json,Long projectId) {
		if(!hasAuth(AuthMoudle.updateOutPackage)){
			return ResponseBody.error("您未拥有此权限");
		}
		if (StringUtil.isEmpty(json)) {
			return ResponseBody.error("参数不能为空");
		}
		projectId = getProjectId(projectId);
		try {
			List<MockOutPackageBean> list = JsonUtil.jsonToList(json, MockOutPackageBean.class);
			boolean hasData = false;
			for (MockOutPackageBean bean : list) {
				bean.setProjectId(projectId);
				Integer dataStatus = bean.getDataStatus();
				if (dataStatus == null) {
					dataStatus = 0;
					bean.setDataStatus(0);
				}
				if (dataStatus == 1 && hasData) {
					return ResponseBody.error("只能存在一个实体外包装");
				}
				if (dataStatus == 1) {
					hasData = true;
				}
			}
			if (!hasData) {
				return ResponseBody.error("必须存在一个实体外包装");
			}
//			FileUtil.write(new File(PathContext.apiOutPackagePath), json);
			execution.delete(EasyapiBindSQLExecuter.build(new MockOutPackageBean()).eq(MockOutPackageBean::getProjectId,projectId));
			execution.insert(list);
			return ResponseBody.successObj();
		} catch (Exception e) {
			return ResponseBody.error("数据结构错误");
		}
	}

	/**
	 * 获取控制器列表
	 * @authCode 30001
	 * @return
	 */
	@PostMapping("/getControllers")
	public ResponseBody getControllers(Long projectId) {
		if(!hasAuth(AuthMoudle.getControllers)){
			return ResponseBody.error("您未拥有此权限");
		}
		projectId = getProjectId(projectId);
//		File file = new File(PathContext.apiControllerPath);
//		if (!file.exists()) {
//			return ResponseBody.successList();
//		}
//		String context = FileUtil.getFileContent(file);
//		if (!JsonUtil.isJson(context)) {
//			return ResponseBody.successList();
//		}
//		List<ControllerBean> list = JsonUtil.jsonToList(context, ControllerBean.class);
//		if (StringUtil.isEmpty(list)) {
//			return ResponseBody.successList();
//		}
//		list.sort(Comparator.comparing(ControllerBean::getSort));
		List<ControllerBean> list = execution.select(EasyapiBindSQLExecuter.build(new ControllerBean()).eq(ControllerBean::getProject_id, projectId).orderBy("sort"));
		return ResponseBody.successList(list);
	}

	/**
	 * @authCode 40001
	 * @param text
	 * @return
	 */
	@PostMapping("/findInterfaces")
	public ResponseBody findInterfaces(String text,Long projectId) {
		if(!hasAuth(AuthMoudle.findInterfaces)){
			return ResponseBody.error("您未拥有此权限");
		}
		if (text == null) {
			text = "";
		}
		//		List<File> interfaceFiles = new ArrayList<File>();
//		FileUtil.fetchFileList(new File(PathContext.apiInterfacePath), interfaceFiles);
//		for (File file : interfaceFiles) {
//			String content = FileUtil.getFileContent(file);
//			if(StringUtil.isEmpty(content)){
//				continue;
//			}
//			List<InterfaceBean> list = JsonUtil.jsonToList(content, InterfaceBean.class);
//			for (InterfaceBean bean : list) {
//				// 本接口是否包含关键字
//				boolean inner = false;
//				// 如果接口名称包含关键字
//				if (bean.getTitle().contains(text) || StringUtil.toPinYin(bean.getTitle()).toUpperCase().contains(text.toUpperCase())) {
//					inner = true;
//				} else if (bean.getRequest_url().contains(text) || StringUtil.toPinYin(bean.getRequest_url()).toUpperCase().contains(text.toUpperCase())) {
//					// 如果接口地址包含关键字
//					inner = true;
//				}
//				if (inner) {
//					for (ControllerBean con : controllers) {
//						if (con.getController_java_name().equals(bean.getController_name())) {
//							con.setChildren(bean);
//						}
//					}
//				}
//			}
//		}
		projectId = getProjectId(projectId);
		List<ControllerBean> controllers = (List<ControllerBean>) getControllers(projectId).getData();
		List<InterfaceBean> list = execution.select(EasyapiBindSQLExecuter.build(new InterfaceBean()).eq(InterfaceBean::getProject_id, projectId));
		for (InterfaceBean bean : list) {
			// 本接口是否包含关键字
			boolean inner = false;
			// 如果接口名称包含关键字
			if (bean.getTitle().contains(text) || StringUtil.toPinYin(bean.getTitle()).toUpperCase().contains(text.toUpperCase())) {
				inner = true;
			} else if (bean.getRequest_url().contains(text) || StringUtil.toPinYin(bean.getRequest_url()).toUpperCase().contains(text.toUpperCase())) {
				// 如果接口地址包含关键字
				inner = true;
			}
			if (inner) {
				for (ControllerBean con : controllers) {
					if (con.getController_java_name().equals(bean.getController_name())) {
						con.setChildren(bean);
					}
				}
			}
		}
		Iterator<ControllerBean> iterator = controllers.iterator();
		while (iterator.hasNext()) {
			ControllerBean next = iterator.next();
			if (next.getChildren() == null || next.getChildren().size() == 0) {
				iterator.remove();
			}
		}
		return ResponseBody.successList(controllers);
	}

	/**
	 * 获取响应的mock数据 例：http://localhost:8089/yifei-apidoc/mock?path=apis/s-10001
	 * @authCode 50001
	 * @param path
	 * @return
	 */
	@RequestMapping("/mock")
	public Map<String, Object> mockResponse(String path,Long projectId,Long interfaceId) {
		if(!hasAuth(AuthMoudle.mock)){
			//mock接口先暂时放行
//			return Collections.emptyMap();
		}
//		InterfaceBean bean = null;
//		// 查詢接口
//		List<File> interfaceFiles = new ArrayList<File>();
//		FileUtil.fetchFileList(new File(PathContext.apiInterfacePath), interfaceFiles);
//		for (File file : interfaceFiles) {
//			String content = FileUtil.getFileContent(file);
//			if(StringUtil.isEmpty(content)){
//				continue;
//			}
//			List<InterfaceBean> list = JsonUtil.jsonToList(content, InterfaceBean.class);
//			for (InterfaceBean beans : list) {
//				if (beans.getRequest_url().equals(path)) {
//					bean = beans;
//				}
//			}
//		}
//		Map<String, Object> result = new HashMap<String, Object>();
//		String fileContent = FileUtil.getFileContent(new File(PathContext.apiOutPackagePath));
//		if (StringUtil.isEmpty(fileContent)) {
//			initOutPackageFile();
//			fileContent = FileUtil.getFileContent(new File(PathContext.apiOutPackagePath));
//		}
//		// 添加返回外包装
//		List<MockOutPackageBean> list = JsonUtil.jsonToList(fileContent, MockOutPackageBean.class);
//		String outPackageKey = null;
//		for (MockOutPackageBean op : list) {
//			Integer dataStatus = op.getDataStatus();
//			String key = op.getKey();
//			if (dataStatus != null && dataStatus == 1) {
//				outPackageKey = key;
//				continue;
//			}
//			Object val = op.getVal();
//			try {
//				result.put(key, JsonUtil.jsonToMap(val.toString()));
//			} catch (Exception e) {
//				try {
//					result.put(key, JsonUtil.jsonToList(val.toString(), Map.class));
//				} catch (Exception e2) {
//					try {
//						result.put(key, JsonUtil.jsonToList(val.toString(), String.class));
//					} catch (Exception e3) {
//						result.put(key, val);
//					}
//				}
//			}
//		}
//		if (bean == null) {
//			result.put(outPackageKey, "未知返回");
//		}
//		String response_param_name = bean.getResponse_param_name();
//		String text = FileUtil.getFileContent(new File(PathContext.apiResponseMockPath + response_param_name));
//		try {
//			result.put(outPackageKey, JsonUtil.jsonToList(text, Map.class));
//			return result;
//		} catch (Exception e) {
//			try {
//				result.put(outPackageKey, JsonUtil.jsonToList(text, List.class));
//				return result;
//			}catch (Exception e1){
//				try {
//					result.put(outPackageKey, JsonUtil.jsonToMap(text));
//					return result;
//				} catch (Exception e2) {
//					result.put(outPackageKey, text);
//					return result;
//				}
//			}
//		}
		Map<String, Object> result = new HashMap<>();
		projectId = getProjectId(projectId);
		EasyapiBindSQLExecuter executer = EasyapiBindSQLExecuter.build(new InterfaceBean())
				.eq(InterfaceBean::getProject_id, projectId)
				.eq(!StringUtil.isEmpty(path),InterfaceBean::getRequest_url,path)
				.eq(!StringUtil.isEmpty(interfaceId),InterfaceBean::getId,interfaceId);
		InterfaceBean in = execution.selectOne(executer);
		if(in == null){
			return result;
		}
		//获取接口返回mock
		MockBean mock = execution.selectOne(EasyapiBindSQLExecuter.build(new MockBean())
				.eq(MockBean::getProjectId,projectId)
				.eq(MockBean::getInterfaceId,in.getId()));

		//获取全部外包装
		List<MockOutPackageBean> outPackages = execution.select(EasyapiBindSQLExecuter.build(new MockOutPackageBean()).eq(MockOutPackageBean::getProjectId, projectId));
		if(outPackages == null){
			return result;
		}

		//转换mock真实类型
		String mockJson = mock.getMock();
		Object mockVal = getStrRealVal(mockJson);
//		try {
//			mockVal = JsonUtil.jsonToList(mockJson, Map.class);
//		} catch (Exception e) {
//			try {
//				mockVal = JsonUtil.jsonToList(mockJson, List.class);
//			}catch (Exception e1){
//				try {
//					mockVal = JsonUtil.jsonToMap(mockJson);
//				} catch (Exception e2) {}
//			}
//		}

		for (MockOutPackageBean out : outPackages) {
			if(out.getDataStatus() == 1){
				result.put(out.getKey(),mockVal);
				continue;
			}
			result.put(out.getKey(),getStrRealVal(out.getOldVal()));
		}
		return result;

	}

	//获取json的真实类型
	private Object getStrRealVal(String val){
		if(StringUtil.isEmpty(val) || !JsonUtil.isJson(val)){
			return val;
		}
		Object realVal = val;
		try {
			realVal = JsonUtil.jsonToList(val, Map.class);
		} catch (Exception e) {
			try {
				realVal = JsonUtil.jsonToList(val, List.class);
			}catch (Exception e1){
				try {
					realVal = JsonUtil.jsonToMap(val);
				} catch (Exception e2) {}
			}
		}
		return realVal;
	}

	/**
	 * 获取mock数据
	 *
	 * @param responseParamName
	 * @return
	 */
	@PostMapping("/getMock")
	public ResponseBody getMockData(String responseParamName,Long projectId,Long interfaceId) {
		if (StringUtil.isEmpty(responseParamName)) {
			return ResponseBody.error();
		}
//		File mockFile = new File(PathContext.apiResponseMockPath + responseParamName);
//		String content = FileUtil.getFileContent(mockFile);
		projectId = getProjectId(projectId);
		EasyapiBindSQLExecuter executer = EasyapiBindSQLExecuter.build(new MockBean())
				.eq(MockBean::getProjectId, projectId);

		if(interfaceId != null){
			executer.eq(MockBean::getInterfaceId,interfaceId);
		}
		MockBean bean = execution.selectOne(executer);
		String content = bean.getMock();
		try {
			return ResponseBody.successList(JSONArray.parseArray(content));
		} catch (Exception e) {
			try {
				return ResponseBody.successObj(JSONObject.parse(content));
			}catch (Exception e1){
				return ResponseBody.successObj(content);
			}
		}
	}

	/**
	 * 修改mock数据
	 * @authCode 50201
	 * @param responseParamName 返回值名称
	 * @param json              更新的数据
	 * @return
	 */
	@PostMapping("/updateMock")
	public ResponseBody updateMock(String responseParamName, String json,Long interfaceId,Long projectId) {
		if(!hasAuth(AuthMoudle.updateMock)){
			return ResponseBody.error("您未拥有此权限");
		}
		if (StringUtil.isEmpty(responseParamName) || StringUtil.isEmpty(json)) {
			return ResponseBody.error();
		}
		projectId = getProjectId(projectId);
		EasyapiBindSQLExecuter executer = EasyapiBindSQLExecuter.build(new InterfaceBean())
				.eq(InterfaceBean::getProject_id, projectId)
				.eq(!StringUtil.isEmpty(interfaceId), InterfaceBean::getId, interfaceId)
				.eq(!StringUtil.isEmpty(responseParamName), InterfaceBean::getResponse_param_name, responseParamName);
		InterfaceBean in = execution.selectOne(executer);
		if(in == null){
			return ResponseBody.error("接口不存在");
		}
		MockBean mockBean = new MockBean();
		mockBean.setProjectId(projectId);
		mockBean.setInterfaceId(in.getId());
		//先删除旧的mock
		execution.delete(mockBean);
		mockBean.setMock(json);
		execution.insert(mockBean);

//		File mockFile = new File(PathContext.apiResponseMockPath + responseParamName);
//		FileUtil.write(mockFile, json);
		return ResponseBody.successObj();
	}

	/**
	 * 获取接口列表
	 * @authCode 40002
	 * @param controllerName 控制器名称
	 * @param controllerId   控制器id
	 * @return
	 */
	@PostMapping("/getInterfaces")
	public ResponseBody getInterfaces(String controllerName, Long controllerId,Long projectId) {
		if(!hasAuth(AuthMoudle.getInterfaces)){
			return ResponseBody.error("您未拥有此权限");
		}
//		if (controllerName.endsWith(".json")) {
//			controllerName = controllerName.substring(0, controllerName.indexOf(".json"));
//		}
//		File file = new File(PathContext.apiInterfacePath + controllerName + ".json");
//		if (!file.exists()) {
//			return ResponseBody.successList();
//		}
//		String context = FileUtil.getFileContent(file);
//		if (!JsonUtil.isJson(context)) {
//			return ResponseBody.successList();
//		}
//		List<InterfaceBean> list = JsonUtil.jsonToList(context, InterfaceBean.class);
//		if (StringUtil.isEmpty(list)) {
//			return ResponseBody.successList();
//		}
//		list.sort(Comparator.comparing(InterfaceBean::getSort));
		projectId = getProjectId(projectId);
		EasyapiBindSQLExecuter executer = EasyapiBindSQLExecuter.build(new InterfaceBean())
				.eq(InterfaceBean::getProject_id, projectId)
				.eq(InterfaceBean::getController_name, controllerName);
		if(controllerId != null){
			executer.eq(InterfaceBean::getController_id,controllerId);
		}
		List<InterfaceBean> list = execution.select(executer);
		return ResponseBody.successList(list);
	}


	/**
	 * 添加一个接口
	 * <p>
	 * 控制器名称
	 * @authCode 40101
	 * @param json 接口信息
	 * @return
	 */
	@PostMapping("/addInterfaces")
	public ResponseBody addInterfaces(String json,Long projectId) {
		if(!hasAuth(AuthMoudle.addInterfaces)){
			return ResponseBody.error("您未拥有此权限");
		}
		if (!JsonUtil.isJson(json)) {
			return ResponseBody.error("提交的数据非json结构");
		}
//        ImportInterfaceDto dto = JsonUtil.jsonToBean(json, ImportInterfaceDto.class);
//        json = dto.getJson();
		InterfaceBean bean = JsonUtil.jsonToBean(json, InterfaceBean.class);
//		if (bean == null || StringUtil.isEmpty(bean.getController_name())) {
//			return ResponseBody.error("未找到相关控制器");
//		}
//		List<File> fileList = new ArrayList<File>();
//		FileUtil.fetchFileList(new File(PathContext.apiInterfacePath), fileList);
//		if (fileList.isEmpty()) {
//			return ResponseBody.error("未找到相关控制器");
//		}
//		File find = null;
//		for (File file : fileList) {
//			if (file.getName().equals(bean.getController_name() + ".json")) {
//				find = file;
//				break;
//			}
//		}
//		if (find == null) {
//			return ResponseBody.error("未找到相关控制器");
//		}
//		List<InterfaceBean> list = JsonUtil.jsonToList(FileUtil.getFileContent(find),
//				InterfaceBean.class);
//		for (InterfaceBean yb : list) {
//			if (!StringUtil.isEmpty(yb.getRequest_url()) && !StringUtil.isEmpty(bean.getRequest_url())
//					&& yb.getRequest_url().equals(bean.getRequest_url())) {
//				return ResponseBody.error("已存在相同请求路径的接口-" + yb.getRequest_url() + "-" + yb.getTitle());
//			}
//		}
//		list.add(bean);
//		FileUtil.write(find, JsonUtil.beanToJson(list));
		projectId = getProjectId(projectId);
		InterfaceBean query = new InterfaceBean();
		query.setProject_id(projectId);
		query.setRequest_url(bean.getRequest_url());
		query = execution.selectOne(query);
		if(query != null){
			ResponseBody.error("已存在相同请求路径的接口-" + query.getRequest_url() + "-" + query.getTitle());
		}
		execution.insert(bean);
		return ResponseBody.successObj();
	}

	/**
	 * 删除接口
	 * @authCode 40301
	 * @param controllerName 控制器名字
	 * @param requestUrl     接口路径
	 * @return
	 */
	@PostMapping("/delInterfaces")
	public ResponseBody delInterfaces(String controllerName, String requestUrl,Long projectId,Long interfaceId) {
		if(!hasAuth(AuthMoudle.delInterfaces)){
			return ResponseBody.error("您未拥有此权限");
		}
//		if (StringUtil.isEmpty(controllerName) || StringUtil.isEmpty(requestUrl)) {
//			return ResponseBody.error("参数缺失");
//		}
//		List<File> fileList = new ArrayList<File>();
//		FileUtil.fetchFileList(new File(PathContext.apiInterfacePath), fileList);
//		File find = null;
//		for (File file : fileList) {
//			if (file.getName().equals(controllerName + ".json")) {
//				find = file;
//				break;
//			}
//		}
//		if (find == null) {
//			return ResponseBody.error("未找到相关接口");
//		}
//		List<InterfaceBean> list = JsonUtil.jsonToList(FileUtil.getFileContent(find),
//				InterfaceBean.class);
//		Iterator<InterfaceBean> iterator = list.iterator();
//		while (iterator.hasNext()) {
//			InterfaceBean bean = iterator.next();
//			if (bean.getRequest_url().equals(requestUrl)) {
//				iterator.remove();
//			}
//		}
//		FileUtil.write(find, JsonUtil.beanToJson(list));
		projectId = getProjectId(projectId);
		execution.delete(EasyapiBindSQLExecuter.build(new InterfaceBean())
				.eq(InterfaceBean::getProject_id,projectId)
				.eq(!StringUtil.isEmpty(requestUrl),InterfaceBean::getRequest_url,requestUrl)
				.eq(!StringUtil.isEmpty(interfaceId),InterfaceBean::getId,interfaceId));
		return ResponseBody.successObj();
	}

	/**
	 * 获取请求参数
	 * @authCode 60001
	 * @return
	 */
	@PostMapping("/getRequestParam")
	public ResponseBody getRequestParam(String requestParamName, Long interfaceId,Long projectId) {
		if(!hasAuth(AuthMoudle.getRequestParam)){
			return ResponseBody.error("您未拥有此权限");
		}
//		if (requestParamName.endsWith(".json")) {
//			requestParamName = requestParamName.substring(0, requestParamName.indexOf(".json"));
//		}
//		File file = new File(PathContext.apiRequestParamPath + File.separator + requestParamName + ".json");
//		if (!file.exists()) {
//			return ResponseBody.successObj();
//		}
//		String context = FileUtil.getFileContent(file);
//		if (!JsonUtil.isJson(context)) {
//			return ResponseBody.successObj();
//		}
//		Pattern pvalue = Pattern.compile("\"uuid\":\"(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{0,}\"");
//		Matcher matcher = pvalue.matcher(context);
//		while (matcher.find()) {
//			String group = matcher.group();
//			while (context.contains(group)) {
//				context = context.replaceFirst(group,
//						"\"uuid\":\"" + UUID.randomUUID().toString().replace("-", "") + "\"");
//			}
//		}
		projectId = getProjectId(projectId);
		EasyapiBindSQLExecuter executer = EasyapiBindSQLExecuter.build(new InterfaceBean())
				.eq(!StringUtil.isEmpty(interfaceId), InterfaceBean::getId, interfaceId)
				.eq(!StringUtil.isEmpty(requestParamName),InterfaceBean::getRequest_param_name,requestParamName)
				.eq(InterfaceBean::getProject_id, projectId);
		InterfaceBean in = execution.selectOne(executer);
		if(in == null){
			return ResponseBody.error("未找到相关接口");
		}
		InterfaceParamBean param = execution.selectOne(EasyapiBindSQLExecuter.build(new InterfaceParamBean())
				.eq(InterfaceParamBean::getInterfaceId,in.getId()).eq(InterfaceParamBean::getType,0));
		String context = param.getParamsJson();
		Pattern pvalue = Pattern.compile("\"uuid\":\"(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{0,}\"");
		Matcher matcher = pvalue.matcher(context);
		while (matcher.find()) {
			String group = matcher.group();
			while (context.contains(group)) {
				context = context.replaceFirst(group,
						"\"uuid\":\"" + UUID.randomUUID().toString().replace("-", "") + "\"");
			}
		}
		return ResponseBody.successObj(JsonUtil.jsonToMap(context));
	}

	/**
	 * 获取响应参数
	 * @authCode 70001
	 * @return
	 */
	@PostMapping("/getResponseParam")
	public ResponseBody getResponseParam(String responseParamName, Long interfaceId,Long projectId) {
		if(!hasAuth(AuthMoudle.getResponseParam)){
			return ResponseBody.error("您未拥有此权限");
		}
		if (responseParamName.endsWith(".json")) {
			responseParamName = responseParamName.substring(0, responseParamName.indexOf(".json"));
		}
//		File file = new File(PathContext.apiResponseParamPath + File.separator + responseParamName + ".json");
//		if (!file.exists()) {
//			return ResponseBody.successObj();
//		}
//		String context = FileUtil.getFileContent(file);
//		if (!JsonUtil.isJson(context)) {
//			return ResponseBody.successObj();
//		}
//		Pattern pvalue = Pattern.compile("\"uuid\":\"(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{0,}\"");
//		Matcher matcher = pvalue.matcher(context);
//		while (matcher.find()) {
//			String group = matcher.group();
//			while (context.contains(group)) {
//				context = context.replaceFirst(group,
//						"\"uuid\":\"" + UUID.randomUUID().toString().replace("-", "") + "\"");
//			}
//		}
//		return ResponseBody.successObj(JsonUtil.jsonToBean(context, Object.class));

		projectId = getProjectId(projectId);
		EasyapiBindSQLExecuter executer = EasyapiBindSQLExecuter.build(new InterfaceBean())
				.eq(!StringUtil.isEmpty(interfaceId), InterfaceBean::getId, interfaceId)
				.eq(!StringUtil.isEmpty(responseParamName),InterfaceBean::getResponse_param_name,responseParamName)
				.eq(InterfaceBean::getProject_id, projectId);
		InterfaceBean in = execution.selectOne(executer);
		if(in == null){
			return ResponseBody.error("未找到相关接口");
		}
		InterfaceParamBean param = execution.selectOne(EasyapiBindSQLExecuter.build(new InterfaceParamBean())
				.eq(InterfaceParamBean::getInterfaceId,in.getId()).eq(InterfaceParamBean::getType,1));
		String context = param.getParamsJson();
		Pattern pvalue = Pattern.compile("\"uuid\":\"(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{0,}\"");
		Matcher matcher = pvalue.matcher(context);
		while (matcher.find()) {
			String group = matcher.group();
			while (context.contains(group)) {
				context = context.replaceFirst(group,
						"\"uuid\":\"" + UUID.randomUUID().toString().replace("-", "") + "\"");
			}
		}
		return ResponseBody.successObj(JsonUtil.jsonToMap(context));
	}

	/**
	 * 网络请求
	 *
	 * @authCode 80001
	 * @param enty
	 * @return
	 */
	@PostMapping("/doUrl")
	public ResponseBody doUrl(HttpServletRequest request, HttpEntity enty) {
//		if(!hasAuth(AuthMoudle.doUrl)){
//			return ResponseBody.error("您未拥有此权限");
//		}
		if (StringUtil.isEmpty(enty.getUrl()) || StringUtil.isEmpty(enty.getMethod())
				|| StringUtil.isEmpty(enty.getType())) {
			return ResponseBody.error();
		}
		if (enty.getMethod().equalsIgnoreCase("all")) {
			enty.setMethod("POST");
		}
		Map<String, Object> headersMap = JsonUtil.jsonToMap(enty.getJsonHeaders());
		String sessionId = "";
		Map<String, String> headers = new HashMap<String, String>();
		if (headersMap != null && !headersMap.isEmpty()) {
			Set<Entry<String, Object>> entrySet = headersMap.entrySet();
			for (Entry<String, Object> entry : entrySet) {
				headers.put(entry.getKey(), entry.getValue().toString());
			}
		}
		if (request.getSession().getAttribute("yifei-doc-session-sessionId") != null) {
			sessionId = (String) request.getSession().getAttribute("yifei-doc-session-sessionId");
		}
		Integer type = enty.getType();
		String base64Result = StringUtil.base64Encode("{}");
		if (type == 0) {
			// form表单
			HttpUtil util = new HttpUtil(enty.getUrl());
			util.setRequestMethod(enty.getMethod());
			util.setHeaders(headers);
			if (!StringUtil.isEmpty(sessionId)) {
				util.addSession(sessionId);
			}
			String formParam = HttpEntity.jsonToForm(enty.getJsonParams());
			if(enty.getMethod().equalsIgnoreCase("get")){
				enty.setUrl(enty.getUrl()+"?"+formParam);
			}else{
				util.setRequestParam(formParam);
			}
			base64Result = StringUtil.base64Encode(IOUtil.inputToByte(util.doInputStreamUrl()));
			if (!StringUtil.isEmpty(util.getResponseSessionId())) {
				sessionId = util.getResponseSessionId();
			}
		} else if (type == 1) {
			// json体
			HttpUtil util = new HttpUtil(enty.getUrl());
			util.setRequestMethod("POST");
			util.setHeaders(headers);
			if (!StringUtil.isEmpty(sessionId)) {
				util.addSession(sessionId);
			}
			util.setJsonRequestParam(JsonUtil.jsonToMap(enty.getJsonParams()));
			base64Result = StringUtil.base64Encode(IOUtil.inputToByte(util.doInputStreamUrl()));
			if (!StringUtil.isEmpty(util.getResponseSessionId())) {
				sessionId = util.getResponseSessionId();
			}
		} else if (type == 2) {
			MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest) request;
			MultipartFile file = multiReq.getFile("file");
			// 文件上传
			if (file == null) {
				return ResponseBody.error("文件未提交");
			}
			HttpUtil util = new HttpUtil(StringUtil.UrlDecode(enty.getUrl()));
			util.setRequestMethod("POST");
			try {
				util.setHeaders(headers);
				if (!StringUtil.isEmpty(sessionId)) {
					util.addSession(sessionId);
				}
				String jsonToForm = HttpEntity.jsonToForm(enty.getJsonParams());
				Map<String, String> map = HttpEntity.formToMap(jsonToForm);
				if (map == null) {
					map = new HashMap<String, String>();
				}
				base64Result = util.doFileUrl(StringUtil.UrlDecode(enty.getFileParamName()), file.getInputStream(),
						file.getOriginalFilename(), map);
				if (!StringUtil.isEmpty(util.getResponseSessionId())) {
					sessionId = util.getResponseSessionId();
				}
				base64Result = StringUtil.base64Encode(base64Result);
			} catch (Exception e) {
				return ResponseBody.error("文件上传失败：" + e.getMessage());
			}
		} else if (type == 3) {
			// XML
			HttpUtil util = new HttpUtil(enty.getUrl());
			util.setRequestMethod(enty.getMethod());
			util.setHeaders(headers);
			if (!StringUtil.isEmpty(sessionId)) {
				util.addSession(sessionId);
			}
			util.setHeaders("Content-Type", "application/xml");
			util.setRequestParam(enty.getJsonParams());
			base64Result = StringUtil.base64Encode(IOUtil.inputToByte(util.doInputStreamUrl()));
			if (!StringUtil.isEmpty(util.getResponseSessionId())) {
				sessionId = util.getResponseSessionId();
			}
		} else if (type == 4) {
			// RAW 纯字符串
			HttpUtil util = new HttpUtil(enty.getUrl());
			util.setRequestMethod(enty.getMethod());
			util.setHeaders(headers);
			if (!StringUtil.isEmpty(sessionId)) {
				util.addSession(sessionId);
			}
			util.setHeaders("Content-Type", "text/plain");
			util.setRequestParam(enty.getJsonParams());
			base64Result = StringUtil.base64Encode(IOUtil.inputToByte(util.doInputStreamUrl()));
			if (!StringUtil.isEmpty(util.getResponseSessionId())) {
				sessionId = util.getResponseSessionId();
			}
		}
		request.getSession().setAttribute("yifei-doc-session-sessionId", sessionId);
		return ResponseBody.successObj(base64Result);
	}

	/**
	 * 修改项目介绍
	 * @authCode 20201
	 * @param info
	 */
	@PostMapping("/updateInfo")
	public ResponseBody updateInfo(InfoBean info,Long projectId) {
		if(!hasAuth(AuthMoudle.updateInfo)){
			return ResponseBody.error("您未拥有此权限");
		}
//		File file = new File(PathContext.apiInfoPath);
//		if (!file.exists()) {
//			return ResponseBody.successObj();
//		}
//		FileUtil.write(file, info.getJson());
		if(!StringUtil.isEmpty(info.getJson())){
			info = JsonUtil.jsonToBean(info.getJson(),InfoBean.class);
		}
		projectId = getProjectId(projectId);
		List<HostBean> requestHost = info.getRequestHost();
		if(requestHost != null){
			//先删除旧的
			execution.delete(EasyapiBindSQLExecuter.build(new HostBean()).eq(HostBean::getProjectId,projectId));
			for (HostBean host : requestHost) {
				host.setProjectId(projectId);
				host.setCreateTime(System.currentTimeMillis());
			}
			execution.insert(requestHost);
		}
		info.setId(projectId);
		execution.update(info);
		return ResponseBody.successObj();
	}

	/**
	 * 修改入参
	 * @authCode 60201
	 * @param dto
	 */
	@PostMapping("/updateRequestParams")
	public ResponseBody updateRequestParams(UpdateReqOrResParamDto dto) {
		if(!hasAuth(AuthMoudle.updateRequestParams)){
			return ResponseBody.error("您未拥有此权限");
		}
		if (StringUtil.isEmpty(dto.getRequestParamName()) || StringUtil.isEmpty(dto.getJson())) {
			return ResponseBody.error();
		}
		// if (!YiFeiAuthProcess.checkAuth(FeaturesType.update_param)) {
		// return ResponseBody.error("未获得权限");
		// }
//		File file = new File(PathContext.apiRequestParamPath + File.separator + dto.getRequestParamName());
//		if (!file.exists()) {
//			file = FileUtil.createNewFile(PathContext.apiRequestParamPath + File.separator + dto.getRequestParamName());
//		}
		dto.setProjectId(getProjectId(dto.getProjectId()));
		InterfaceBean in = new InterfaceBean();
		in.setRequest_param_name(dto.getRequestParamName());
		in = execution.selectOne(in);
		if(in == null){
			ResponseBody.error("未找到相关接口");
		}
		InterfaceParamBean paramBean = execution.selectOne(EasyapiBindSQLExecuter.build(new InterfaceParamBean())
				.eq(InterfaceParamBean::getInterfaceId,in.getId())
				.eq(InterfaceParamBean::getType,0)
				.eq(InterfaceParamBean::getProjectId,dto.getProjectId()));

		if (!JsonUtil.isJson(dto.getJson())) {
			return ResponseBody.error("json格式有误");
		}
		JSONObject map = JSONObject.parseObject(dto.getJson());
		Collection<Object> values = map.values();
		for (Object object : values) {
			if (!Map.class.isAssignableFrom(object.getClass())) {
				continue;
			}
			Map<String, Object> m = (Map<String, Object>) object;
			if (m != null && !m.isEmpty() && m.containsKey("show") && m.get("show").equals(0)) {
				updateShow(m);
			}
		}
//		FileUtil.write(file, map.toJSONString());
		paramBean.setParamsJson(map.toJSONString());
		execution.update(paramBean);
		return ResponseBody.successObj();
	}

	/**
	 * 修改返回值
	 * @authCode 70201
	 * @param dto
	 */
	@PostMapping("/updateResponseParams")
	public ResponseBody updateResponseParams(UpdateReqOrResParamDto dto) {
		if(!hasAuth(AuthMoudle.updateResponseParams)){
			return ResponseBody.error("您未拥有此权限");
		}
		if (StringUtil.isEmpty(dto.getResponseParamName()) || StringUtil.isEmpty(dto.getJson())) {
			return ResponseBody.error();
		}
//		File file = new File(PathContext.apiResponseParamPath + File.separator + dto.getResponseParamName());
//		if (!file.exists()) {
//			file = FileUtil.createNewFile(PathContext.apiResponseParamPath + File.separator + dto.getResponseParamName());
//		}
		dto.setProjectId(getProjectId(dto.getProjectId()));
		InterfaceBean in = new InterfaceBean();
		in.setResponse_param_name(dto.getResponseParamName());
		in = execution.selectOne(in);
		if(in == null){
			ResponseBody.error("未找到相关接口");
		}
		InterfaceParamBean paramBean = execution.selectOne(EasyapiBindSQLExecuter.build(new InterfaceParamBean())
				.eq(InterfaceParamBean::getInterfaceId,in.getId())
				.eq(InterfaceParamBean::getType,1)
				.eq(InterfaceParamBean::getProjectId,dto.getProjectId()));

		if (!JsonUtil.isJson(dto.getJson())) {
			return ResponseBody.error("json格式有误");
		}
		Object result = JsonUtil.jsonToBean(dto.getJson(), Object.class);
		Object resultWrite = result;
		// 如果父类隐藏，则子类一并隐藏
		if (List.class.isAssignableFrom(result.getClass())) {
			List<Object> list = (List<Object>) result;
			List<Object> listWrite = new ArrayList<Object>();
			for (Object object : list) {
				if (!Map.class.isAssignableFrom(object.getClass())) {
					listWrite.add(object);
					continue;
				}
				Map<String, Object> beanToMap = JsonUtil.beanToMap(object);
				Set<String> keySet = beanToMap.keySet();
				for (String key : keySet) {
					Object obj = beanToMap.get(key);
					if (!ParamBean.class.isAssignableFrom(obj.getClass())
							&& !Map.class.isAssignableFrom(obj.getClass())) {
						continue;
					}
					Map<String, Object> m = JsonUtil.beanToMap(obj);
					if (m != null && !m.isEmpty() && m.containsKey("show") && m.get("show").equals(0)) {
						updateShow(m);
						beanToMap.put(key, m);
					}
				}
				listWrite.add(beanToMap);
			}
			resultWrite = listWrite;
		} else if (Map.class.isAssignableFrom(result.getClass())) {
			Map<String, Object> beanToMap = JsonUtil.beanToMap(result);
			Set<String> keySet = beanToMap.keySet();
			for (String key : keySet) {
				Object object = beanToMap.get(key);
				if (!ParamBean.class.isAssignableFrom(object.getClass())
						&& !Map.class.isAssignableFrom(object.getClass())) {
					continue;
				}
				Map<String, Object> m = JsonUtil.beanToMap(object);
				if (m != null && !m.isEmpty() && m.containsKey("show") && m.get("show").equals(0)) {
					updateShow(m);
					beanToMap.put(key, m);
				}
			}
			resultWrite = beanToMap;
		}
//		FileUtil.write(file, JsonUtil.beanToJson(resultWrite));
		paramBean.setParamsJson(JsonUtil.beanToJson(resultWrite));
		execution.update(paramBean);

		//同步修改mock数据
		ReadBeanApiFilter filter = configuration .getBeanApiFilter();
		MockExecuter executer = new MockExecuter(filter);
		executer.setMockHidden(configuration.getResponseParamApiFilter().isMockHiddenParam(true));
		executer.setMockShow(configuration.getResponseParamApiFilter().isMockHiddenParam(false));
		executer.setMockRequired(configuration.getResponseParamApiFilter().isMockRequiredParam(true));
		executer.setMockUnRequired(configuration.getResponseParamApiFilter().isMockRequiredParam(false));
		Object mockValue = executer.mockByApiBean(JsonUtil.beanToJson(resultWrite));

		MockBean mockBean = execution.selectOne(EasyapiBindSQLExecuter.build(new MockBean())
				.eq(MockBean::getInterfaceId,in.getId())
				.eq(MockBean::getProjectId,dto.getProjectId()));
		mockBean.setMock(JsonUtil.beanToJson(mockValue));

//		FileUtil.write(new File(PathContext.apiResponseMockPath + dto.getResponseParamName()),
//				JsonUtil.beanToJson(mockValue));
		return ResponseBody.successObj();
	}

	/**
	 * 修改接口详情
	 * @authCode 40201
	 * @param controllerName 控制器名称
	 * @param json           接口详情json
	 * @return
	 */
	@PostMapping("/updateInteface")
	public ResponseBody updateInteface(String controllerName, String javaName, String json,Long projectId,Long interfaceId) {
		if(!hasAuth(AuthMoudle.updateInteface)){
			return ResponseBody.error("您未拥有此权限");
		}
		if (StringUtil.isEmpty(controllerName) || StringUtil.isEmpty(javaName) || StringUtil.isEmpty(json)) {
			return ResponseBody.error();
		}
//		File file = new File(PathContext.apiInterfacePath + File.separator + controllerName + ".json");
//		if (!file.exists()) {
//			return ResponseBody.error("controllerName不存在");
//		}
//		String context = FileUtil.getFileContent(file);
//		if (!JsonUtil.isJson(context)) {
//			return ResponseBody.error("controllerName不存在");
//		}
//		List<InterfaceBean> list = JsonUtil.jsonToList(context, InterfaceBean.class);
//		Iterator<InterfaceBean> iterator = list.iterator();
//		while (iterator.hasNext()) {
//			InterfaceBean next = iterator.next();
//			if (next.getJavaName().equals(javaName)) {
//				iterator.remove();
//			}
//		}
//		list.add(JsonUtil.jsonToBean(json, InterfaceBean.class));
//		FileUtil.write(file, JsonUtil.beanToJson(list));
		projectId = getProjectId(projectId);
		ControllerBean controllerBean = execution.selectOne(EasyapiBindSQLExecuter.build(new ControllerBean())
				.eq(ControllerBean::getController_java_name,controllerName)
				.eq(ControllerBean::getProject_id,projectId));
		if(controllerBean == null){
			ResponseBody.error("未找到相关接口");
		}
		InterfaceBean interfaceBean = execution.selectOne(EasyapiBindSQLExecuter.build(new InterfaceBean())
				.eq(InterfaceBean::getController_id,controllerBean.getId())
				.eq(InterfaceBean::getJavaName,javaName)
				.eq(InterfaceBean::getProject_id,projectId));

		InterfaceBean update = JsonUtil.jsonToBean(json, InterfaceBean.class);
		update.setId(interfaceBean.getId());
		update.setController_id(controllerBean.getId());
		update.setProject_id(projectId);
		execution.update(update);

		return ResponseBody.successObj();
	}

	/**
	 * 导入请求参数
	 */
	@PostMapping("/importRequestParam")
	public ResponseBody importRequestParam() {
		return ResponseBody.successObj();
	}

	/**
	 * 导入返回参数
	 */
	@PostMapping("/importResponseParam")
	public ResponseBody importResponseParam() {
		return ResponseBody.successObj();
	}

	/**
	 * 导出测试用例
	 */
	@PostMapping("/exportTestExcel")
	public ResponseBody exportTestExcel() {
		return ResponseBody.successObj();
	}

	/**
	 * 导出接口文档到excel
	 */
	@PostMapping("/exportApiExcel")
	public ResponseBody exportApiExcel() {
		return ResponseBody.successObj();
	}


	/**
	 * 修改接口参数时如果父类设置隐藏，则子类一并隐藏
	 *
	 * @param m
	 */
	public void updateChildrenShow(Map m) {
		Set<String> keySet = m.keySet();
		for (String key : keySet) {
			if (!Map.class.isAssignableFrom(m.get(key).getClass())) {
				continue;
			}
			Map<String, Object> valMap = (Map<String, Object>) m.get(key);
			updateShow(valMap);
		}
	}

	/**
	 * 修改接口参数时如果父类设置隐藏，则子类一并隐藏
	 *
	 * @param map
	 */
	public void updateShow(Map map) {
		if (map == null || map.isEmpty()) {
			return;
		}
		if (map.containsKey("show")) {
			map.put("show", 0);
		}
		if (!map.containsKey("children")) {
			return;
		}
		Object object = map.get("children");
		if(Collection.class.isAssignableFrom(object.getClass()) || object.getClass().isArray()){
			List<Map> jsonToList = JsonUtil.jsonToList(JsonUtil.beanToJson(object), Map.class);
			for (Map map2 : jsonToList) {
				updateChildrenShow(map2);
			}
			map.put("children", jsonToList);
		}else{
			Map<String, Object> m = JsonUtil.jsonToMap(JsonUtil.beanToJson(object));
			updateChildrenShow(m);
			map.put("children", m);
		}
	}

	/**
	 * 将文件内容发送给指定服务器
	 */
	private void sycnFile(SyncFileBean bean) {
		HttpUtil util = new HttpUtil(bean.getAddress());
		util.setRequestMethod("POST");
		util.setFormRequestParam(bean);
		try {
			util.doUrl();
		} catch (Exception e) {

		}
	}

	private boolean hasAuth(AuthMoudle authMoudle){
		if(!RequestPool.isAdmin() && !AuthMoudle.hasAuth(authMoudle.getAuthCode(), RequestPool.getUserAuths())){
			return false;
		}
		return true;
	}

	private Long getProjectId(Long projectId){
		if(projectId == null){
			InfoBean info = execution.selectOne(EasyapiBindSQLExecuter.build(new InfoBean()).eq(InfoBean::getInitProject,1));
			projectId = info.getId();
		}
		return projectId;
	}
}
