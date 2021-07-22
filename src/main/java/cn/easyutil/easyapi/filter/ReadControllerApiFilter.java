package cn.easyutil.easyapi.filter;

import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * 读取controller相关api
 */
public interface ReadControllerApiFilter {

    /**
     * 读取需要展示的controller
     * @param springContext spring容器
     * @return
     */
    Set<Class> readControllers(ApplicationContext springContext);

    /**
     * 读取controller是否忽略
     * @param controller
     * @return  true：忽略  false：非忽略
     */
    boolean readControllerIgnore(Class controller);

    /**
     * 读取controller名称
     * @param controller    controller类
     * @return  controller名称
     */
    String readControllerAliasName(Class controller);

    /**
     * 读取controller上的父路径
     * @param controller
     * @return  父路径
     */
    String readControllerRequestPath(Class controller);
}
