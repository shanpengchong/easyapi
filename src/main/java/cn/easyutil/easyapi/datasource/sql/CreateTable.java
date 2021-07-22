package cn.easyutil.easyapi.datasource.sql;


public class CreateTable {

    //全部数据库表
    public static String[] all = new String[]{
            host(),
            userAuth(),
            controller(),
            interfaces(),
            mock(),
            params(),
            project(),
            user(),
            outPackage(),
            userProjects(),
            interfaceParams()
    };

    /**
     * 项目环境表
     * @return
     */
    public static String host(){
        return "CREATE TABLE if not exists `easyapi_project_host` (\n" +
                "  `id` bigint(20) NOT NULL auto_increment,\n" +
                "  `create_time` bigint(20) DEFAULT NULL,\n" +
                "  `update_time` bigint(20) DEFAULT NULL,\n" +
                "  `deleted` int(1) DEFAULT NULL,\n" +
                "  `project_id` bigint(20) DEFAULT NULL COMMENT '项目id',\n" +
                "  `name` varchar(100) DEFAULT NULL COMMENT '环境名称',\n" +
                "  `host` varchar(255) DEFAULT NULL COMMENT '环境地址(address+port)',\n" +
                "  `port` int(5) DEFAULT NULL COMMENT '环境端口',\n" +
                "  `address` varchar(255) DEFAULT NULL COMMENT '环境ip或域名',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ;";
    }

    /**
     * 用户与权限对应表
     * @return
     */
    public static String userAuth(){
        return "CREATE TABLE if not exists `easyapi_user_auth` (\n" +
                "  `id` bigint(20) NOT NULL auto_increment,\n" +
                "  `create_time` bigint(20) DEFAULT NULL,\n" +
                "  `update_time` bigint(20) DEFAULT NULL,\n" +
                "  `deleted` int(1) DEFAULT NULL,\n" +
                "  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',\n" +
                "  `project_id` bigint(20) DEFAULT NULL COMMENT '项目id',\n" +
                "  `auth_code` int(10) DEFAULT '0' COMMENT '用户拥有的权限码',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ")";
    }

    public static String userProjects(){
        return "CREATE TABLE if not exists `easyapi_user_projects` (\n" +
                "  `id` bigint(20) NOT NULL auto_increment,\n" +
                "  `create_time` bigint(20) DEFAULT NULL,\n" +
                "  `update_time` bigint(20) DEFAULT NULL,\n" +
                "  `deleted` int(1) DEFAULT NULL,\n" +
                "  `project_id` bigint(20) DEFAULT NULL COMMENT '项目id',\n" +
                "  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ";
    }

    /**
     * 控制器表
     * @return
     */
    public static String controller(){
        return "CREATE TABLE if not exists `easyapi_controller` (\n" +
                "  `id` bigint(20) NOT NULL auto_increment,\n" +
                "  `create_time` bigint(20) DEFAULT NULL,\n" +
                "  `update_time` bigint(20) DEFAULT NULL,\n" +
                "  `deleted` int(1) DEFAULT NULL,\n" +
                "  `project_id` bigint(20) DEFAULT NULL COMMENT '项目id',\n" +
                "  `java_name` varchar(100) DEFAULT NULL COMMENT '控制器包名+类名',\n" +
                "  `comment` varchar(50) DEFAULT NULL COMMENT '控制器名称',\n" +
                "  `comment_pinyin` varchar(255) DEFAULT NULL COMMENT '控制器名称拼音',\n" +
                "  `api_path` varchar(100) DEFAULT NULL COMMENT '统一请求的父路径',\n" +
                "  `sort` int(3) DEFAULT NULL COMMENT '排序值',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") \n";
    }

    /**
     * 接口表
     * @return
     */
    public static String interfaces(){
        return "CREATE TABLE if not exists `easyapi_interface` (\n" +
                "  `id` bigint(20) NOT NULL auto_increment,\n" +
                "  `create_time` bigint(20) DEFAULT NULL,\n" +
                "  `update_time` bigint(20) DEFAULT NULL,\n" +
                "  `deleted` int(1) DEFAULT NULL,\n" +
                "  `project_id` bigint(20) DEFAULT NULL COMMENT '项目id',\n" +
                "  `controller_id` bigint(20) DEFAULT NULL COMMENT '控制器id',\n" +
                "  `java_name` varchar(100) DEFAULT NULL COMMENT '接口方法名',\n" +
                "  `CONTROLLER_NAME` varchar(100) DEFAULT NULL COMMENT '控制器名称',\n" +
                "  `title` varchar(255) DEFAULT NULL COMMENT '接口标题',\n" +
                "  `remark` varchar(255) DEFAULT NULL COMMENT '接口说明',\n" +
                "  `request_param_name` varchar(255) DEFAULT NULL COMMENT '',\n" +
                "  `response_param_name` varchar(255) DEFAULT NULL COMMENT '',\n" +
                "  `request_param_remark` varchar(255) DEFAULT NULL COMMENT '请求参数说明',\n" +
                "  `response_param_remark` varchar(255) DEFAULT NULL COMMENT '响应参数说明',\n" +
                "  `request_url` varchar(100) DEFAULT NULL COMMENT '请求地址',\n" +
                "  `request_method` int(1) DEFAULT '0' COMMENT '请求方式 0-全部  1-get  2-post',\n" +
                "  `request_type` int(1) DEFAULT NULL COMMENT '请求体类型 0-form  1-json体  2-文件上传 3-XML 4-RAW',\n" +
                "  `sort` int(3) DEFAULT '0' COMMENT '排序值',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") \n";
    }

    public static String interfaceParams(){
        return "CREATE TABLE if not exists `easyapi_interface_param` (\n" +
                "  `id` bigint(20) NOT NULL auto_increment,\n" +
                "  `create_time` bigint(20) DEFAULT NULL,\n" +
                "  `update_time` bigint(20) DEFAULT NULL,\n" +
                "  `deleted` int(1) DEFAULT NULL,\n" +
                "  `project_id` bigint(20) DEFAULT NULL COMMENT '项目id',\n" +
                "  `interface_id` bigint(20) DEFAULT NULL COMMENT '接口id',\n" +
                "  `params_json` clob DEFAULT NULL COMMENT '存储的参数json',\n" +
                "  `type` int(1) DEFAULT '0' COMMENT '0-请求参数 1-返回参数',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") \n";
    }

    /**
     * 接口返回mock表
     * @return
     */
    public static String mock(){
        return "CREATE TABLE if not exists `easyapi_interface_mock` (\n" +
                "  `id` bigint(20) NOT NULL auto_increment,\n" +
                "  `create_time` bigint(20) DEFAULT NULL,\n" +
                "  `update_time` bigint(20) DEFAULT NULL,\n" +
                "  `deleted` int(1) DEFAULT NULL,\n" +
                "  `project_id` bigint(20) DEFAULT NULL COMMENT '项目id',\n" +
                "  `interface_id` bigint(20) DEFAULT NULL COMMENT '接口id',\n" +
                "  `mock` CLOB COMMENT 'mock结果',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ";
    }

    /**
     * 接口参数表
     * @return
     */
    public static String params(){
        return "CREATE TABLE if not exists `easyapi_param` (\n" +
                "  `id` bigint(20) NOT NULL auto_increment,\n" +
                "  `create_time` bigint(20) DEFAULT NULL,\n" +
                "  `update_time` bigint(20) DEFAULT NULL,\n" +
                "  `deleted` int(1) DEFAULT NULL,\n" +
                "  `project_id` bigint(20) DEFAULT NULL COMMENT '项目id',\n" +
                "  `interface_id` bigint(20) DEFAULT NULL COMMENT '接口id',\n" +
                "  `interface_param_type` int(1) DEFAULT '0' COMMENT '接口参数类型:0-请求参数  1-响应参数',\n" +
                "  `java_name` varchar(100) DEFAULT NULL COMMENT '包名+类名',\n" +
                "  `name` varchar(100) DEFAULT NULL COMMENT '参数名称',\n" +
                "  `remark` varchar(255) DEFAULT NULL COMMENT '参数说明',\n" +
                "  `type` int(2) DEFAULT '1' COMMENT '参数类型 1-整数 2-数字 3-布尔 4-字符 5-基本类型数组 6-对象 7-文件 8-map 9-对象数组',\n" +
                "  `mock_value` varchar(100) DEFAULT NULL COMMENT '默认值',\n" +
                "  `old_mock_value` varchar(100) DEFAULT NULL COMMENT '原始未被替换的mockvalue的值',\n" +
                "  `required` int(1) DEFAULT '0' COMMENT '是否必填 0-不必填  1-必填',\n" +
                "  `show` int(1) DEFAULT '1' COMMENT '是否显示  0-不显示  1-显示',\n" +
                "  `sort` int(3) DEFAULT '0' COMMENT '排序值',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ";
    }

    /**
     * 项目表
     * @return
     */
    public static String project(){
        return "CREATE TABLE if not exists `easyapi_project` (\n" +
                "  `id` bigint(20) NOT NULL auto_increment,\n" +
                "  `create_time` bigint(20) DEFAULT NULL,\n" +
                "  `update_time` bigint(20) DEFAULT NULL,\n" +
                "  `deleted` int(1) DEFAULT NULL,\n" +
                "  `title` varchar(100) DEFAULT NULL COMMENT '项目标题',\n" +
                "  `name` varchar(100) DEFAULT NULL COMMENT '项目名称',\n" +
                "  `remark` CLOB COMMENT '项目介绍',\n" +
                "  `img` CLOB COMMENT '项目缩略图',\n" +
                "  `INIT_PROJECT` int(1) COMMENT '是否初始项目 0-不是  1-是',\n" +
                "  `disable` int(1) DEFAULT '0' COMMENT '0-非关闭  1-关闭',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ";
    }

    /**
     * 用户表
     * @return
     */
    public static String user(){
        return "CREATE TABLE if not exists `easyapi_user` (\n" +
                "  `id` bigint(20) NOT NULL auto_increment,\n" +
                "  `create_time` bigint(20) DEFAULT NULL,\n" +
                "  `update_time` bigint(20) DEFAULT NULL,\n" +
                "  `deleted` int(1) DEFAULT NULL,\n" +
                "  `username` varchar(50) DEFAULT NULL COMMENT '登陆用户名',\n" +
                "  `password` varchar(50) DEFAULT NULL COMMENT '登陆密码',\n" +
                "  `remark` varchar(100) DEFAULT NULL COMMENT '用户说明',\n" +
                "  `last_login_time` bigint(20) DEFAULT NULL COMMENT '上次登陆时间',\n" +
                "  `super_admin` int(1) DEFAULT '0' COMMENT '0-普通用户  1-超级管理员',\n" +
                "  `disable` int(1) DEFAULT '0' COMMENT '0-正常  1-禁用',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") \n";
    }

    /**
     * 参数外包装
     * @return
     */
    public static String outPackage(){
        return "CREATE TABLE if not exists `easyapi_project_out_package` (\n" +
                "  `id` bigint(20) NOT NULL auto_increment,\n" +
                "  `create_time` bigint(20) DEFAULT NULL,\n" +
                "  `update_time` bigint(20) DEFAULT NULL,\n" +
                "  `deleted` int(1) DEFAULT NULL,\n" +
                "  `project_id` bigint(20) DEFAULT NULL COMMENT '项目id',\n" +
                "  `key` varchar(50) DEFAULT NULL COMMENT '字段名',\n" +
                "  `val` text COMMENT '字段示例值',\n" +
                "  `sort` int(1) DEFAULT '0' COMMENT '排序值',\n"+
                "  `type` int(1) DEFAULT '0' COMMENT '0-普通字段  1-返回体字段',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") \n";
    }

    public static String clearTables(Long projectId){
//        return "TRUNCATE TABLE `easyapi_project_out_package` ;" +
//                "TRUNCATE TABLE `easyapi_user` ;" +
//                "TRUNCATE TABLE `easyapi_project` ;" +
//                "TRUNCATE TABLE `easyapi_param` ;" +
//                "TRUNCATE TABLE `easyapi_interface` ;" +
//                "TRUNCATE TABLE `easyapi_controller` ;" +
//                "TRUNCATE TABLE `easyapi_user_auth` ;" +
//                "TRUNCATE TABLE `easyapi_user_projects` ;" +
//                "TRUNCATE TABLE `easyapi_interface_param` ;" +
//                "TRUNCATE TABLE `easyapi_interface_mock` ;" +
//                "TRUNCATE TABLE `easyapi_project_host` ;" ;
        String sql =  "TRUNCATE TABLE `easyapi_project_out_package` ;" +
                "DELETE from `easyapi_user` where project_id=${projectId};" +
                "DELETE from `easyapi_project` where id=${projectId};" +
                "DELETE from `easyapi_param` where project_id=${projectId};" +
                "DELETE from `easyapi_interface` where project_id=${projectId};" +
                "DELETE from `easyapi_controller` where project_id=${projectId};" +
                "DELETE from `easyapi_user_auth` where project_id=${projectId};" +
                "DELETE from `easyapi_user_projects` where project_id=${projectId};" +
                "DELETE from `easyapi_interface_param` where project_id=${projectId};" +
                "DELETE from `easyapi_interface_mock` where project_id=${projectId};" +
                "DELETE from `easyapi_project_host` where project_id=${projectId};" ;
        return sql.replace("${projectId}", projectId.toString());
    }

    public static String clearTableNotUser(Long projectId){
//        return "TRUNCATE TABLE `easyapi_project_out_package` ;" +
//                "TRUNCATE TABLE `easyapi_project` ;" +
//                "TRUNCATE TABLE `easyapi_param` ;" +
//                "TRUNCATE TABLE `easyapi_interface` ;" +
//                "TRUNCATE TABLE `easyapi_controller` ;" +
//                "TRUNCATE TABLE `easyapi_interface_param` ;" +
//                "TRUNCATE TABLE `easyapi_interface_mock` ;" +
//                "TRUNCATE TABLE `easyapi_project_host` ;" ;
        String sql =  "DELETE from `easyapi_project_out_package` where project_id=${projectId};" +
                "DELETE from `easyapi_project`  where id=${projectId};" +
                "DELETE from `easyapi_param`  where project_id=${projectId};" +
                "DELETE from `easyapi_interface`  where project_id=${projectId};" +
                "DELETE from `easyapi_controller`  where project_id=${projectId};" +
                "DELETE from `easyapi_interface_param`  where project_id=${projectId};" +
                "DELETE from `easyapi_interface_mock`  where project_id=${projectId};" +
                "DELETE from `easyapi_project_host`  where project_id=${projectId};" ;
        return sql.replace("${projectId}", projectId.toString());
    }

    public static String clearUsers(Long projectId){
//        return "TRUNCATE TABLE `easyapi_user` ;" +
//                "TRUNCATE TABLE `easyapi_user_auth` ;" +
//                "TRUNCATE TABLE `easyapi_user_projects` ;";
        String sql =  "DELETE from `easyapi_user` where project_id=${projectId};" +
                "DELETE from `easyapi_user_auth` where project_id=${projectId};" +
                "DELETE from `easyapi_user_projects` where project_id=${projectId};";
        return sql.replace("${projectId}", projectId.toString());
    }

}
