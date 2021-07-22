package cn.easyutil.easyapi.configuration;

/**
 * 默认的配置文件处理
 */
public class DefaultConfigurationCreator extends AbstractConfigurationCreator{

    //补充后的配置
    private EasyapiConfiguration replenishConfiguration;

    private EasyapiConfiguration configuration;

    public DefaultConfigurationCreator(EasyapiConfiguration configuration){
        this.configuration = configuration;
    }

    @Override
    public EasyapiConfiguration replenish(EasyapiConfiguration configuration) {
        return configuration;
    }

    @Override
    protected EasyapiConfiguration process() {
        if(replenishConfiguration != null){
            return replenishConfiguration;
        }
        replenishConfiguration = replenish(configuration);
        if(replenishConfiguration != null){
            return replenishConfiguration;
        }
        return configuration;
    }
}
