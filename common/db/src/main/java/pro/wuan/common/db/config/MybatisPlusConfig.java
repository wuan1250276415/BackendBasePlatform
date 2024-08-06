package pro.wuan.common.db.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.wuan.common.db.dynamictable.MyDynamicTableNameInterceptor;
import pro.wuan.common.db.dynamictable.MyTableNameHandler;
import pro.wuan.common.db.dynamictable.RIdModTableNameParser;
import pro.wuan.common.db.injector.MybatisSqlInjector;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@MapperScan("pro.wuan.**.**.*.mapper")
public class MybatisPlusConfig {
    @Autowired
    private SqlDefualtAdditionalConfiguration additionalConfiguration;

    /**
     * 分页插件
     *
     * @return
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        log.info("tellhowcloud-db");
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        // 取消MyBatis Plus的最大分页500条的限制
        paginationInnerInterceptor.setMaxLimit(100000L);
        MyDynamicTableNameInterceptor myDynamicTableNameInterceptor = new MyDynamicTableNameInterceptor(additionalConfiguration);

        Map<String, MyTableNameHandler> map = new HashMap<>();
        map.put("msg_re_info_user", new RIdModTableNameParser());
        myDynamicTableNameInterceptor.setTableNameHandlerMap(map);
        interceptor.addInnerInterceptor(myDynamicTableNameInterceptor);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean
    public MybatisSqlInjector getMySqlInjector() {
        return new MybatisSqlInjector();
    }
}
