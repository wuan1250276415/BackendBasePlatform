package pro.wuan.common.web.sysenum;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统枚举缓存
 *
 * @program: tellhowcloud
 * @author: HawkWang
 * @create: 2021-09-18 10:05
 **/
@Component
public class SysEnumCache {
    private static Map<String, Class> enums = new HashMap<String, Class>();

    public Class getSysEnum(String key) {
        return enums.get(key);
    }

    public void setSysEnum(String key, Class clazz) {
        enums.put(key, clazz);
    }
}
