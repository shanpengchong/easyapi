package cn.easyutil.easyapi.interview.controller;

import cn.easyutil.easyapi.datasource.EasyapiBindSqlExecution;
import cn.easyutil.easyapi.datasource.bean.EasyapiBindSQLExecuter;
import cn.easyutil.easyapi.entity.auth.AuthMoudle;
import cn.easyutil.easyapi.entity.auth.User;
import cn.easyutil.easyapi.entity.auth.UserAuth;
import cn.easyutil.easyapi.entity.auth.UserProject;
import cn.easyutil.easyapi.entity.common.ApidocComment;
import cn.easyutil.easyapi.entity.doc.InfoBean;
import cn.easyutil.easyapi.interview.dto.UpdateUserDto;
import cn.easyutil.easyapi.interview.entity.ResponseBody;
import cn.easyutil.easyapi.configuration.EasyapiConfiguration;
import cn.easyutil.easyapi.configuration.PathContext;
import cn.easyutil.easyapi.interview.entity.SessionUser;
import cn.easyutil.easyapi.util.FileUtil;
import cn.easyutil.easyapi.util.JsonUtil;
import cn.easyutil.easyapi.util.RequestPool;
import cn.easyutil.easyapi.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限模块
 */
@ApidocComment(ignore = true)
@RestController
@RequestMapping("/easyapi/doc/auth")
public class AuthController {

    @Autowired
    private EasyapiConfiguration configuration;

    @Autowired
    private EasyapiBindSqlExecution execution;

    /**
     * 用户登陆
     * @param user
     * @return
     */
    @RequestMapping("/login")
    public ResponseBody login(User user){
        String username = user.getUsername();
        String password = user.getPassword();
        if(StringUtil.isEmpty(username) || StringUtil.isEmpty(password)){
            return ResponseBody.error("未填写用户名密码");
        }
        password = StringUtil.toMD5(password);
        User query = new User();
        query.setUsername(username);
        query.setPassword(password);
        query = execution.selectOne(query);
        if(query == null){
            return ResponseBody.error("用户名或密码错误");
        }
        List<UserAuth> auths =execution.select(EasyapiBindSQLExecuter.build(new UserAuth()).eq(UserAuth::getUserId,query.getId()));
        List<UserProject> projects =execution.select(EasyapiBindSQLExecuter.build(new UserProject()).eq(UserProject::getUserId,query.getId()));
        SessionUser suser = new SessionUser();
        suser.setUser(query);
        suser.setAuths(auths);
        if(!StringUtil.isEmpty(auths)){
            query.setAuths(auths.stream().map(au -> au.getAuthCode()).collect(Collectors.toList()));
        }
        if(query.getSuperAdmin() == 1){
            List<Integer> allAuthCodes = AuthMoudle.getAllAuthCodes();
            query.setAuths(allAuthCodes);
            List<UserAuth> userAuths = new ArrayList<>();
            for (Integer code : allAuthCodes) {
                UserAuth au = new UserAuth();
                au.setUserId(query.getId());
                au.setAuthCode(code);
                userAuths.add(au);
            }
            suser.setAuths(userAuths);
        }
        suser.setProjects(projects);
        RequestPool.setSessionUser(suser);
//        RequestPool.login(query);
        return ResponseBody.successObj(query);
    }

    /**
     * 批量导入用户
     * @return
     */
    @RequestMapping("/importUsers")
    public ResponseBody importUsers(String json){
        List<User> newUsers = null;
        try {
            newUsers = JsonUtil.jsonToList(json, User.class);
        }catch (Exception e){
            return ResponseBody.error("导入数据格式不正确");
        }
        if(StringUtil.isEmpty(newUsers)){
            return ResponseBody.successObj();
        }
//        //整合现有用户
//        List<User> users = getUsers();
//        Iterator<User> iterator = newUsers.iterator();
//        while(iterator.hasNext()){
//            User newUser = iterator.next();
//            String username = newUser.getUsername();
//            //如果用户名或密码不存在,则删除
//            if(StringUtil.isEmpty(username) || StringUtil.isEmpty(newUser.getPassword())){
//                iterator.remove();
//                continue;
//            }
//            //对比现有用户，如果存在则使用现在的
//            for (User user : users) {
//                if(username.equals(user.getUsername())){
//                    iterator.remove();
//                    break;
//                }
//            }
//            if(StringUtil.isEmpty(newUser.getAuths())){
//                newUser.setAuths(AuthMoudle.getDefaultAuthCodes());
//            }
//        }
//        //整合新旧数据
//        if(!StringUtil.isEmpty(newUsers)){
//            users.addAll(newUsers);
//            FileUtil.write(new File(PathContext.authUserPath), JsonUtil.beanToJson(users));
//        }
        Iterator<User> iterator = newUsers.iterator();
        while (iterator.hasNext()){
            User user = iterator.next();
            if(queryUserExsit(user.getUsername())){
                iterator.remove();
            }
            user.setSuperAdmin(0);
            user.setCreateTime(System.currentTimeMillis());
        }
        execution.insert(newUsers);
        return ResponseBody.successObj();
    }

    /**
     * 添加用户
     * @param user
     * @return
     */
    @RequestMapping("/addUser")
    public ResponseBody addUser(User user){
        if(!RequestPool.isAdmin()){
            ResponseBody.error("暂无权限");
        }
        String username = user.getUsername();
        String password = user.getPassword();
        if(StringUtil.isEmpty(username) || StringUtil.isEmpty(password)){
            return ResponseBody.error("用户名和密码不能为空");
        }
        user.setPassword(StringUtil.toMD5(password));
        user.setCreateTime(System.currentTimeMillis());
//        List<User> authUsers = getUsers();
//        for (User authUser : authUsers) {
//            if(authUser.getUsername().equals(username)){
//                return ResponseBody.error("用户名已存在");
//            }
//        }
//        if(user.getAuths()==null || user.getAuths().isEmpty()){
//            user.setAuths(AuthMoudle.getDefaultAuthCodes());
//        }
//        authUsers.add(user);
//        FileUtil.write(new File(PathContext.authUserPath), JsonUtil.beanToJson(authUsers));
        if(queryUserExsit(username)){
            ResponseBody.error("用户名已存在");
        }
        user.setSuperAdmin(0);
        execution.insert(user);
        return ResponseBody.successObj();
    }

    /**
     * 修改密码
     * @param password
     * @return
     */
    @RequestMapping("/updatePassword")
    public ResponseBody updatePassword(String password){
//        String userName = RequestPool.getUser().getUsername();
//        if(StringUtil.isEmpty(userName)){
//            return ResponseBody.error("请先进行登陆");
//        }
//        List<User> users = getUsers();
//        for (User user : users) {
//            if(user.getUsername().equals(userName)){
//                user.setPassword(StringUtil.toMD5(password));
//            }
//        }
//        FileUtil.write(new File(PathContext.authUserPath), JsonUtil.beanToJson(users));
        if(!RequestPool.isLogin()){
            return ResponseBody.error("请先进行登陆");
        }
        Long id = RequestPool.getUser().getId();
        User user = new User();
        user.setId(id);
        user.setPassword(StringUtil.toMD5(password));
        execution.update(user);
        return ResponseBody.successObj();

    }
    /**
     * 修改用户信息
     * @param user
     * @return
     */
    @RequestMapping("/updateUser")
    public ResponseBody updateUser(UpdateUserDto user){
        if(!RequestPool.isAdmin()){
            ResponseBody.error("暂无权限");
        }
        if(user.getProjectId() == null){
            InfoBean project = execution.selectOne(EasyapiBindSQLExecuter.build(new InfoBean()).eq(InfoBean::getInitProject,1));
            user.setProjectId(project.getId());
        }
        String username = user.getUsername();
        String password = user.getPassword();
        if(StringUtil.isEmpty(username)){
            return ResponseBody.error("用户名不能为空");
        }
//        String loginUserName = configuration.getLoginUserName();
//        if(username.equals(loginUserName) && !RequestPool.getUser().getUsername().equals(loginUserName)){
//            return ResponseBody.error("不可修改超级管理员信息");
//        }
//        List<User> users = getUsers();
//        boolean find = false;
//        for (User us : users) {
//            if(us.getUsername().equals(username)){
//                find = true;
//                if(!StringUtil.isEmpty(password)){
//                    us.setPassword(StringUtil.toMD5(password));
//                }
//                if(!StringUtil.isEmpty(user.getRemark())){
//                    us.setRemark(user.getRemark());
//                }
//                if(!StringUtil.isEmpty(user.getAuths())){
//                    us.setAuths(user.getAuths());
//                }
//            }
//        }
//        if(find){
//            FileUtil.write(new File(PathContext.authUserPath), JsonUtil.beanToJson(users));
//            return ResponseBody.successObj();
//        }
//        return ResponseBody.error("未找到相关用户");
        User query = new User();
        query.setUsername(username);
        query = execution.selectOne(query);
        if(query == null){
            return ResponseBody.error("未找到相关用户");
        }
        if(!query.getPassword().equals(StringUtil.toMD5(password))){
            query.setPassword(StringUtil.toMD5(password));
        }
        query.setDisable(user.getDisable());
        query.setRemark(user.getRemark());
        query.setUpdateTime(System.currentTimeMillis());
        if(user.getAuths() != null){
            //先删除旧的权限
            execution.delete(EasyapiBindSQLExecuter.build(new UserAuth()).eq(UserAuth::getUserId,query.getId()));
            List<UserAuth> userAuths = new ArrayList<>();
            for (Integer auth : user.getAuths()) {
                UserAuth at = new UserAuth();
                at.setUserId(query.getId());
                at.setProjectId(user.getProjectId());
                at.setAuthCode(auth);
                at.setCreateTime(System.currentTimeMillis());
                userAuths.add(at);
            }
            execution.insert(userAuths);
        }
        return ResponseBody.successObj();
    }

    /**
     * 删除用户
     * @param username
     * @return
     */
    @RequestMapping("/delUser")
    public ResponseBody delUser(String username){
        if(!RequestPool.isAdmin()){
            ResponseBody.error("暂无权限");
        }
        if(StringUtil.isEmpty(username)){
            return ResponseBody.error("用户名不能为空");
        }
//        if(username.equals("admin")){
//            return ResponseBody.error("超级管理员不允许被删除");
//        }
//        List<User> users = getUsers();
//        Iterator<User> iterator = users.iterator();
//        while (iterator.hasNext()){
//            User user = iterator.next();
//            if(user.getUsername().equals(username)){
//                iterator.remove();
//            }
//        }
//        FileUtil.write(new File(PathContext.authUserPath), JsonUtil.beanToJson(users));
        User user = queryUser(username);
        if(user == null){
            return ResponseBody.error("用户不存在");
        }
        if(user.getSuperAdmin() == 1){
            return ResponseBody.error("超级管理员不允许被删除");
        }
        execution.delete(user);
        return ResponseBody.successObj();
    }

    /**
     * 搜索用户
     * @param username
     * @return
     */
    @RequestMapping("/findUser")
    public ResponseBody findUser(String username){
        if(!RequestPool.isAdmin()){
            ResponseBody.error("暂无权限");
        }
//        List<User> users = getUsers();
//        if(StringUtil.isEmpty(username)){
//            return ResponseBody.successList(users);
//        }
//        List<User> result = new ArrayList<>();
//        for (User user : users) {
//            if(user.getUsername().contains(username)){
//                result.add(user);
//            }
//        }
        User query = new User();
        EasyapiBindSQLExecuter executer = EasyapiBindSQLExecuter.build(query);
        if(StringUtil.isEmpty(username)){
            executer.like(User::getUsername,username);
        }
        List<User> users = execution.select(executer);
        for (User user : users) {
            if(user.getSuperAdmin() == 1){
                user.setAuths(AuthMoudle.getAllAuthCodes());
                continue;
            }
            List<UserAuth> select = execution.select(EasyapiBindSQLExecuter.build(new UserAuth()).eq(UserAuth::getUserId, user.getId()));
            user.setAuths(select.stream().map(ua -> ua.getAuthCode()).collect(Collectors.toList()));
        }
        return ResponseBody.successList(users);
    }

    /**
     * 给用户添加权限
     * @param user
     * @return
     */
    @RequestMapping("/addAuth")
    public ResponseBody addAuth(User user){
        UpdateUserDto dto = new UpdateUserDto();
        BeanUtils.copyProperties(user,dto);
        return updateUser(dto);
    }

    /**
     * 获取系统所有权限列表
     * @return
     */
    @RequestMapping("/getAuths")
    public ResponseBody getAuths(){
        return ResponseBody.successList(AuthMoudle.parseBeans());
    }

//    private List<User> getUsers(){
//        String authUserPath = PathContext.authUserPath;
//        String fileContent = FileUtil.getFileContent(new File(authUserPath));
//        return JsonUtil.jsonToList(fileContent, User.class);
//    }

    private boolean queryUserExsit(String username){

        return queryUser(username) != null;
    }

    private User queryUser(String username){
        User user = new User();
        user.setUsername(username);
        return execution.selectOne(user);
    }
}
