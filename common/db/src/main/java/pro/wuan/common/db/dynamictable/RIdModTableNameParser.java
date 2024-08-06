package pro.wuan.common.db.dynamictable;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.model.GlobalContext;

import java.util.Map;

/**
 * 根据消息接受人id取模
 *
 * @author: oldone
 * @date: 2021/10/9 15:48
 */
@Slf4j
public class RIdModTableNameParser implements MyTableNameHandler {

    private final String RECEIVE_ID = "userId";

    /**
     * 生成动态表名后的sql
     *
     * @param sql       原始sql
     * @param tableName 新表表名
     * @param parameter 待操作数据
     * @return 替换表名后的sql语句
     */
    @Override
    public String dynamicTableName(String sql, String tableName, Object parameter) {
        if (parameter == null) {
            return this.getTableName(tableName);
        }

        try {
            Map map = JSONObject.parseObject(JSONObject.toJSONString(parameter), Map.class);
            if (map == null || !map.containsKey(RECEIVE_ID)) {
                return this.getTableName(tableName);
            }
            Object ridObj = map.get(RECEIVE_ID);
            String rid = String.valueOf(ridObj);
            long num = Long.valueOf(rid) % 5;
            return tableName + "_" + num;
        } catch (Exception e) {
            log.error("dynamicTableName error:", e);
        }
        return this.getTableName(tableName);
    }

    private String getTableName(String tableName) {
        Long userId = GlobalContext.getCurrentUserId();
        if (CommonConstant.DEFAULT_PARENT_ID.equals(userId)) {
            return tableName;
        }
        long num = userId % 5;
        return tableName + "_" + num;
    }
}
