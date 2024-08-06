package pro.wuan.common.web.sysenum;

import cn.hutool.core.util.ClassUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import pro.wuan.common.core.annotation.sysenum.EnumType;

/**
 * 系统启动后，加载com.tellhow当中的枚举类型
 *
 * @program: tellhowcloud
 * @author: HawkWang
 * @create: 2021-09-18 09:58
 **/
@Slf4j
@Configuration
public class SysEnumListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private SysEnumCache sysEnumCache;

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ClassUtil.scanPackage("com.tellhow").stream().filter(clazz -> {
            if (clazz.isEnum() && clazz.isAnnotationPresent(EnumType.class)) {
                return true;
            }
            return false;
        }).forEach(item -> {
            EnumType isEnum = item.getAnnotation(EnumType.class);
            String key = isEnum.name();
            Class hasSame = sysEnumCache.getSysEnum(key);
            if (null != hasSame) {
                log.error("存在相同key[{}]的枚举类{}", key, hasSame.getName());
                throw new RuntimeException("存在相同key[" + key + "]的枚举");
            }
            sysEnumCache.setSysEnum(key, item);
        });
    }
}