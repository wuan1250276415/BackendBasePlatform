package pro.wuan.gateway;

import java.lang.annotation.*;


@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NacosPropertySource {

    String dataId();

    boolean autoRefreshed();

    String groupId();
}
