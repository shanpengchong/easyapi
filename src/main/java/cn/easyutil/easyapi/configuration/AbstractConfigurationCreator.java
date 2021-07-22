package cn.easyutil.easyapi.configuration;

public abstract class AbstractConfigurationCreator implements ConfigurationCreator{

    /**
     * 补充和完善配置
     * @param configuration 从spring上下文获取的配置
     * @return  补充完成后的配置
     */
    abstract public EasyapiConfiguration replenish(EasyapiConfiguration configuration);

    @Override
    public EasyapiConfiguration getConfiguration() {
        return process();
    }

    abstract protected EasyapiConfiguration process();
}
