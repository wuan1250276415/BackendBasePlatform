package starter;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

/**
 * 自动加载系统参数配置
 *
 * @program: tellhowcloud
 * @author: HawkWang
 * @create: 2021-09-13 14:11
 **/
public class SysParamConfig {

    @Bean
    public ApplicationListener loadSysParam() {
        return new SysParamListener();
    }
}
