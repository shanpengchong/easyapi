package cn.easyutil.easyapi.logic;

import cn.easyutil.easyapi.configuration.ConfigurationCreator;
import cn.easyutil.easyapi.configuration.EasyapiConfiguration;
import cn.easyutil.easyapi.datasource.EasyapiBindSqlExecution;
import cn.easyutil.easyapi.datasource.sql.CreateTable;
import cn.easyutil.easyapi.entity.doc.InfoBean;
import cn.easyutil.easyapi.util.StringUtil;
import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;
import java.io.File;


public class EasyapiRun {

    public static String path = "./apidoc-db/";

    private static String sourceFile = "easyapi";

    //当前主项目的信息
    public static InfoBean initProject;


    public void run(ApplicationContext appContext,ConfigurationCreator configurationCreator,EasyapiBindSqlExecution easyapiBindSqlExecution){
        EasyapiConfiguration configuration = configurationCreator.getConfiguration();
        if(!configuration.isEnable()){
            System.out.println("------------未开启easyapi,若要开启,请在配置文件中设置easyapi.enable=true------------");
            return ;
        }
        //数据库创建路径
        if(!StringUtil.isEmpty(configuration.getH2DataFolder())){
            String folder = configuration.getH2DataFolder();
            if(!folder.endsWith(File.separator)){
                folder += File.separator;
            }
            path = folder;
        }
        if(configuration.getDataSource() != null){
            easyapiBindSqlExecution.setDataSource(configuration.getDataSource());
        }else{
            easyapiBindSqlExecution.setDataSource(dataSource(configuration.getProjectName()));
        }
        //先初始化数据库表，如果不存在则创建
        for (String s : CreateTable.all) {
            easyapiBindSqlExecution.update(s);
        }

        Starter instance = new Starter(configuration,appContext,easyapiBindSqlExecution);
        instance.start();
    }

    private DataSource dataSource(String projectName){
        // 连接数据库
        String URL = "jdbc:h2:file:"+path+sourceFile+"-"+projectName;
        String USER = "easyapi";
        String PASSWORD = "123456";

        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(URL);
        druidDataSource.setUsername(USER);
        druidDataSource.setPassword(PASSWORD);
        druidDataSource.setDriverClassName("org.h2.Driver");
        druidDataSource.setInitialSize(0);
        druidDataSource.setMaxActive(100);
        druidDataSource.setMaxWait(10000);
        druidDataSource.setMinIdle(20);
        druidDataSource.setValidationQuery("Select  'x' from DUAL");
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setTestOnReturn(false);
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
        druidDataSource.setMinEvictableIdleTimeMillis(25200000);
        druidDataSource.setRemoveAbandoned(true);
        druidDataSource.setRemoveAbandonedTimeout(1800);
        druidDataSource.setLogAbandoned(true);
        return druidDataSource;
    }
}
