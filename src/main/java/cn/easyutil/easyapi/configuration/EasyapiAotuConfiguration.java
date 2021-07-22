package cn.easyutil.easyapi.configuration;

import cn.easyutil.easyapi.datasource.EasyapiBindSqlExecution;
import cn.easyutil.easyapi.interview.controller.ApiDocController;
import cn.easyutil.easyapi.interview.controller.AuthController;
import cn.easyutil.easyapi.logic.EasyapiRun;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(EasyapiConfiguration.class)
@ConditionalOnProperty(prefix = "easyapi",name = "enable",havingValue = "true")
public class EasyapiAotuConfiguration implements InitializingBean {

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private ConfigurationCreator configurationCreator;

    @Autowired
    private EasyapiBindSqlExecution easyapiBindSqlExecution;

    @Override
    public void afterPropertiesSet() throws Exception {
        //生成接口文档
        new EasyapiRun().run(appContext,configurationCreator,easyapiBindSqlExecution);
    }

    @Bean
    @ConditionalOnMissingBean
    public ConfigurationCreator configurationCreator(EasyapiConfiguration configuration){
        return new DefaultConfigurationCreator(configuration);
    }

    @Bean
    public ApiDocController apiDocController(){
        return new ApiDocController();
    }

    @Bean
    public AuthController authController(){
        return new AuthController();
    }

    @Bean
    public EasyapiBindSqlExecution easyapiBindSqlExecution(){
        return new EasyapiBindSqlExecution();
    }

}
