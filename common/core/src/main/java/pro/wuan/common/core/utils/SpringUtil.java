package pro.wuan.common.core.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * spring获取bean工具类
 */
@Component
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    public static <T> T getBean(Class<T> cla) {
        return applicationContext.getBean(cla);
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public static <T> T getBean(String name, Class<T> cal) {
        return applicationContext.getBean(name, cal);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> type) {
        return applicationContext.getBeansOfType(type);
    }

    public static String getProperty(String key) {
        return applicationContext.getBean(Environment.class).getProperty(key);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringUtil.applicationContext = applicationContext;
    }
}
