package pro.wuan.common.core.utils;

import cn.hutool.core.lang.Snowflake;

/**
 * @program: tellhowcloud
 * @description: ID生成器
 * @author: HawkWang
 * @create: 2021-08-26 10:19
 **/
public class IDUtil {
    private static final Snowflake snowflake = new Snowflake(1, 1);

    public static long nextId() {
        return snowflake.nextId();
    }

}
