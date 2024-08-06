package pro.wuan.common.web.annotation;

import org.springframework.context.annotation.Import;
import pro.wuan.common.web.config.ListColumnFilterConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 激活列表显示列数据权限
 *
 * @program: tellhowcloud
 * @author: HawkWang
 * @create: 2022-01-05 13:37
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({ListColumnFilterConfiguration.class})
public @interface EnableListColumnFilter {
}
