package pro.wuan.common.db.dynamictable;

/**
 * 动态表名获取接口
 *
 * @author: oldone
 * @date: 2021/9/29 11:27
 */
public interface MyTableNameHandler {
    /**
     * 生成动态表名后的sql
     *
     * @param sql        原始sql
     * @param tableName  新表表名
     * @param parameter 参数
     * @return 替换表名后的sql语句
     */
    String dynamicTableName(String sql, String tableName, Object parameter);
}
