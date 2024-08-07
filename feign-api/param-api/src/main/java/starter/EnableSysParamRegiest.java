package starter;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动加载当前应用中的配置
 *
 * @program: tellhowcloud
 * @author: HawkWang
 * @create: 2021-09-13 14:13
 **/
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({SysParamConfig.class})
public @interface EnableSysParamRegiest {
}
