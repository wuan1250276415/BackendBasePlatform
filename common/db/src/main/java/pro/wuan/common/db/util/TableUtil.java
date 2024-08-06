package pro.wuan.common.db.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.stat.TableStat;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @program: tellhowcloud
 * @description: 数据库表帮助类
 * @author: HawkWang
 * @create: 2021-08-26 10:35
 **/
public class TableUtil {
    private static AntPathMatcher antPathMatcher = new AntPathMatcher();
    public static String property2column(String property, Class clazz) {
        Map<String, ColumnCache> columnCacheMap = LambdaUtils.getColumnMap(clazz);
        ColumnCache columnCache = columnCacheMap.get(LambdaUtils.formatKey(property));
        return columnCache == null ? null : columnCache.getColumn();
    }

    /**
     * 提出sql字符串中所有表名--重新封装sql加入模式查询
     *
     */
    public static String addModelSql(String sql,String model,List<String> skipTable) {
        if (StrUtil.isBlank(model)){
            return sql;
        }
        SQLStatementParser parser = new PGSQLStatementParser(sql);
        // 使用Parser解析生成AST，这里SQLStatement就是AST
        SQLStatement sqlStatement = parser.parseStatement();
        PGSchemaStatVisitor visitor = new PGSchemaStatVisitor();
        sqlStatement.accept(visitor);
        Map<TableStat.Name, TableStat> tables = visitor.getTables();
        List<String> allTableName = new ArrayList<>();
        for (TableStat.Name t : tables.keySet()) {
            allTableName.add(t.getName());
            boolean flag =skipTable.stream().anyMatch(pattern -> antPathMatcher.match(pattern, t.getName()));
            if(!flag){
                sql = sql.replace(t.getName()+" ", " "+model+"."+t.getName()+" ");
            }

        }
//        System.out.println(sql);
        return sql;
    }


}
