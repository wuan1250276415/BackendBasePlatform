package pro.wuan.common.core.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: posiding
 * @create:2019-04-16 20:36
 **/
@Component
public class SpringBeanUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(SpringBeanUtil.applicationContext == null) {
            SpringBeanUtil.applicationContext = applicationContext;
        }
    }

    /**
     * @description 获取spring bean容器
     * @return
     */
    public static ApplicationContext getApplicationContext(){
        return applicationContext;
    }


    public static <T> T getBean(Class<T> classes){
        return applicationContext.getBean(classes);
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public static <T> T getBeanIgnoreEx(Class<T> classes){
        try{
            return applicationContext.getBean(classes);
        }catch (Exception ex){

        }
        return null;
    }
}
