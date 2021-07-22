package cn.easyutil.easyapi.configuration;

import cn.easyutil.easyapi.entity.auth.AuthMoudle;
import cn.easyutil.easyapi.entity.doc.MockOutPackageBean;
import cn.easyutil.easyapi.util.FileUtil;
import cn.easyutil.easyapi.util.JsonUtil;
import cn.easyutil.easyapi.util.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 接口文档的路径
 */
public class PathContext {

    /**
     * 项目根目录
     */
    public static String projectBasePath = "";
//
//    /**
//     * 接口文档存放项目根目录名称
//     */
//    public static String dataFolder = "";
//
//    /**
//     * 接口文档说明的文件目录
//     */
//    public static String apiInfoPath = "";
//
//    /**
//     * 接口文档说明的文件目录
//     */
//    public static String apiOutPackagePath = "";
//
//    /**
//     * 接口文档的控制器列表目录
//     */
//    public static String apiControllerPath = "";
//
//    /**
//     * 接口文档接口列表目录
//     */
//    public static String apiInterfacePath = "";
//
//    /**
//     * 接口文档请求参数列表目录
//     */
//    public static String apiRequestParamPath = "";
//
//    /**
//     * 接口文档响应参数列表目录
//     */
//    public static String apiResponseParamPath = "";
//
//    /**
//     * 接口文档响应参数mock数据目录
//     */
//    public static String apiResponseMockPath = "";
//
//    /**
//     * 接口文档使用到的bean列表目录
//     */
//    public static String apiBeansPath = "";
//
//    /**
//     * 空参对象
//     */
//    public static String apiBeanNullPath = "";
//
//    /**
//     * 用户列表目录
//     */
//    public static String authUserPath = "";
//
//    /**
//     * 权限功能模块列表
//     */
//    public static String authMoudlesPath = "";
//
//
    public static void init(EasyapiConfiguration configuration){
        if(configuration == null){
            return ;
        }
        // 当前项目下路径
        File file = new File("");
        String filePath = null;
        // 初始化文件
        try {
            filePath = file.getCanonicalPath();
        } catch (Exception e) {
            throw new RuntimeException("获取当前项目路径出错", e);
        }
        PathContext.projectBasePath = filePath + File.separator + "src" + File.separator + "main" + File.separator + "java"
                + File.separator;
//        //设置项目名称
//        String projectName = configuration.getApiFileName();
//        if (StringUtil.isEmpty(projectName)) {
//            projectName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
//            configuration.setApiFileName(projectName);
//        }
//        PathContext.dataFolder = projectName + "-apidoc";
//        // 如果使用者指定了目录，则使用指定的目录
//        if (!StringUtil.isEmpty(configuration.getApiFilePath())) {
//            PathContext.dataFolder = configuration.getApiFilePath() + File.separator + projectName + "-apidoc";
//        }
//        //是否删除用户
//        if (configuration.isDelOldUsers()) {
//            FileUtil.del(new File(PathContext.dataFolder));
//        }
//        PathContext.apiInfoPath = PathContext.dataFolder + File.separator + "api-info.json";
//        PathContext.apiOutPackagePath = PathContext.dataFolder + File.separator + "api-outPackage.json";
//        PathContext.apiControllerPath = PathContext.dataFolder + File.separator + "api-controllers.json";
//        PathContext.apiRequestParamPath = PathContext.dataFolder + File.separator + "api-request-param" + File.separator;
//        PathContext.apiResponseParamPath = PathContext.dataFolder + File.separator + "api-response-param" + File.separator;
//        PathContext.apiResponseMockPath = PathContext.dataFolder + File.separator + "api-response-mock" + File.separator;
//        PathContext.apiBeansPath = PathContext.dataFolder + File.separator + "api-beans" + File.separator;
//        PathContext.apiInterfacePath = PathContext.dataFolder + File.separator + "api-interface" + File.separator;
//        PathContext.authUserPath = PathContext.dataFolder + File.separator + "authUsers.json";
//        PathContext.authMoudlesPath = PathContext.dataFolder + File.separator + "authMoudles.json";
//        //初始化null的文件
//        PathContext.apiBeanNullPath = PathContext.apiBeansPath + File.separator + "null.json";
//        //是否删除旧的接口文档
//        if (configuration.isDelOldApi()) {
//            // 先删除旧的文件
////			FileUtil.del(new File(dataFolder));
//            FileUtil.del(new File(PathContext.apiBeansPath));
//            FileUtil.del(new File(PathContext.apiInterfacePath));
//            FileUtil.del(new File(PathContext.apiRequestParamPath));
//            FileUtil.del(new File(PathContext.apiResponseMockPath));
//            FileUtil.del(new File(PathContext.apiResponseParamPath));
//            FileUtil.del(new File(PathContext.apiControllerPath));
//            FileUtil.del(new File(PathContext.apiOutPackagePath));
//        }
//        //以下初始化各文件夹和文件
//        FileUtil.createNewFolder(PathContext.dataFolder);
//        FileUtil.createNewFile(PathContext.apiInfoPath);
//        FileUtil.createNewFile(PathContext.apiOutPackagePath);
//        FileUtil.createNewFile(PathContext.apiControllerPath);
//        FileUtil.createNewFolder(PathContext.apiRequestParamPath);
//        FileUtil.createNewFolder(PathContext.apiResponseParamPath);
//        FileUtil.createNewFolder(PathContext.apiResponseMockPath);
//        FileUtil.createNewFolder(PathContext.apiBeansPath);
//        FileUtil.createNewFolder(PathContext.apiInterfacePath);
//        FileUtil.createNewFile(PathContext.authUserPath);
//        FileUtil.write(new File(PathContext.authMoudlesPath), JsonUtil.beanToJson(AuthMoudle.parseBeans()));
//        FileUtil.write(new File(PathContext.apiBeanNullPath), JsonUtil.beanToJson(new String[]{}));
    }

    public static String javaMockRemark(){
        StringBuffer sb = new StringBuffer("java mock字段表达式");
        sb.append(newLine());
        sb.append("${char}:默认4个随机数字+字母" + newLine());
        sb.append("${char(name)}:随机一个中文昵称" + newLine());
        sb.append("${char(text)}:随机一个中文文本内容" + newLine());
        sb.append("${char(ho)}:随机一个公司名称" + newLine());
        sb.append("${char(address)}:随机一个中文地址" + newLine());
        sb.append("${char(mobile)}:随机一个手机号码" + newLine());
        sb.append("${char(md5)}:随机一个md5串" + newLine());
        sb.append("${char(aes)}:随机一个aes串" + newLine());
        sb.append("${int}:随机取0或1" + newLine());
        sb.append("${int(10)}:随机取0-10之间的数字,范围可自定义" + newLine());
        sb.append("${int(1,10)}:随机取1-10之间的数字,范围可自定义" + newLine());
        sb.append("${double}:随机取0到1之间的小数，保留2位" + newLine());
        sb.append("${double(10)}:随机取0到10之间的小数，保留2位,范围可自定义" + newLine());
        sb.append("${double(1,10)}:随机取1到10之间的小数，保留2位,范围可自定义" + newLine());
        sb.append("${time}:取当前时间戳，13位" + newLine());
        sb.append("${time()}:当前时间转换字符串，默认yyyy-MM-dd HH:mm:ss");
        sb.append("${time(yyyy-MM-dd HH:mm:ss)}:当前时间转换字符串，表达式可自定义" + newLine());
        sb.append("${url}:随机一个图片网络地址" + newLine());
        sb.append("${url(pic)}:随机一个图片网络地址" + newLine());
        sb.append("${url(vod)}:随机一个视频网络地址" + newLine());
        return sb.toString();
    }

    private static String newLine() {
        return "</br>";
    }

    public static List<MockOutPackageBean> initOutPackageFile() {
        // 创建返回外包装
        List<MockOutPackageBean> list = new ArrayList<MockOutPackageBean>();
        MockOutPackageBean mp1 = new MockOutPackageBean();
        mp1.setKey("code");
        mp1.setVal(200);
        mp1.setDataStatus(0);
        mp1.setType(0);
        list.add(mp1);
        MockOutPackageBean mp2 = new MockOutPackageBean();
        mp2.setKey("data");
        mp2.setDataStatus(1);
        list.add(mp2);
        MockOutPackageBean mp3 = new MockOutPackageBean();
        mp3.setKey("errCode");
        mp3.setDataStatus(0);
        list.add(mp3);
        MockOutPackageBean mp4 = new MockOutPackageBean();
        mp4.setKey("errMsg");
        mp4.setDataStatus(0);
        list.add(mp4);
        MockOutPackageBean mp5 = new MockOutPackageBean();
        mp5.setKey("page");
        mp5.setDataStatus(0);
        Map<String, Integer> page = new HashMap<String, Integer>();
        page.put("currentPage", 1);
        page.put("showCount", 2);
        page.put("totalPage", 1);
        page.put("totalResult", 2);
        mp5.setVal(JsonUtil.beanToJson(page));
        list.add(mp5);
        return list;
    }
}
