package pro.wuan.common.db.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 添加默认的sql条件
 * @author: libra
 */
@Component
@ConfigurationProperties("add-sql-defualt-additional")
@Data
@RefreshScope
public class SqlDefualtAdditionalConfiguration {
    /**
     * 是否开启
     */
    private Boolean isEnable=false;

    /**
     * 是否开启读取缓存单位模式
     */
    private Boolean isEnableOrganDatabaseModel = true;

    /**
     * 跳过的表
     */
    private List<String> skipTable = new ArrayList<>();
}