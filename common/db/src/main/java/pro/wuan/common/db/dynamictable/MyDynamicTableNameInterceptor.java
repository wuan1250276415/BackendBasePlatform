package pro.wuan.common.db.dynamictable;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.TableNameParser;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import pro.wuan.common.core.model.GlobalContext;
import pro.wuan.common.db.config.SqlDefualtAdditionalConfiguration;
import pro.wuan.common.db.util.TableUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * 动态表名生成拦截器
 *
 * @author: oldone
 * @date: 2021/9/29 11:27
 */
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MyDynamicTableNameInterceptor implements InnerInterceptor {
    private Map<String, MyTableNameHandler> tableNameHandlerMap = new LinkedHashMap<>();
    private boolean useUnityRule = false;
    private final String LIKE_KEY = " like ";
    private final String QUESTION_KEY = "?";
    private SqlDefualtAdditionalConfiguration sqlDefualtAdditionalConfiguration;
    public MyDynamicTableNameInterceptor(SqlDefualtAdditionalConfiguration additionalConfiguration) {
        log.info("注入sql拦截模式配置：init sqlDefualtAdditionalConfiguration：{}",additionalConfiguration);
        this.sqlDefualtAdditionalConfiguration = additionalConfiguration;
    }
    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        this.modifyLikeSql(boundSql.getSql(), boundSql.getParameterObject(), boundSql);
        PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
        if (InterceptorIgnoreHelper.willIgnoreDynamicTableName(ms.getId())) {
            return;
        }
//        mpBs.sql(this.changeTable(mpBs.sql(), parameter));
        mpBs.sql(this.modifyModeSql(this.changeTable(mpBs.sql(), parameter)));
    }

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpSh.mappedStatement();
        SqlCommandType sct = ms.getSqlCommandType();
        if (sct == SqlCommandType.INSERT || sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE) {
            if (InterceptorIgnoreHelper.willIgnoreDynamicTableName(ms.getId())) {
                return;
            }
            PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
            mpBs.sql(this.modifyModeSql(this.changeTable(mpBs.sql(), sh.getBoundSql().getParameterObject())));
        }
    }


    protected String changeTable(String sql, Object parameter) {
        TableNameParser parser = new TableNameParser(sql);
        List<TableNameParser.SqlToken> names = new ArrayList<>();
        parser.accept(names::add);
        StringBuilder builder = new StringBuilder();
        int last = 0;
        for (TableNameParser.SqlToken name : names) {
            int start = name.getStart();
            if (start != last) {
                builder.append(sql, last, start);
                String value = name.getValue();
                MyTableNameHandler handler = null;
                if (useUnityRule) {
                    handler = tableNameHandlerMap.get("*");
                } else {
                    handler = tableNameHandlerMap.get(value);
                }
                if (handler != null) {
                    // 处理
                    builder.append(handler.dynamicTableName(sql, value, getEntity(parameter)));
                } else {
                    builder.append(value);
                }
            }
            last = name.getEnd();
        }
        if (last != sql.length()) {
            builder.append(sql.substring(last));
        }
        return builder.toString();
    }

    private Object getEntity(Object parameter) {
        if (parameter == null) {
            return null;
        }
        if (parameter instanceof MapperMethod.ParamMap) {
            MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) parameter;
            if (paramMap.containsKey("ew")) {
                Object ew = paramMap.get("ew");

                if (ew instanceof QueryWrapper) {
                    QueryWrapper queryWrapper = (QueryWrapper) ew;
                    return queryWrapper.getEntity();
                }
                if (ew instanceof UpdateWrapper) {
                    UpdateWrapper updateWrapper = (UpdateWrapper) ew;
                    return updateWrapper.getEntity();
                }
            }
            if (paramMap.containsKey("et")) {
                Object et = paramMap.get("et");
                if (et != null) {
                    return et;
                }
            }
        }
        return parameter;
    }

    private String modifyLikeSql(String sql, Object parameterObject, BoundSql boundSql) {
        if (parameterObject instanceof HashMap) {
        } else {
            return sql;
        }
        if (!sql.toLowerCase().contains(" like ") || !sql.toLowerCase().contains("?")) {
            return sql;
        }
        // 获取关键字的个数（去重）
        String[] strList = sql.split("\\?");
        Set<String> keyNames = new HashSet<>();
        for (int i = 0; i < strList.length; i++) {
            if (strList[i].toLowerCase().contains(" like ")) {
                String keyName = boundSql.getParameterMappings().get(i).getProperty();
                keyNames.add(keyName);
            }
        }
        // 对关键字进行特殊字符“清洗”，如果有特殊字符的，在特殊字符前添加转义字符（\）
        for (String keyName : keyNames) {
            HashMap parameter = (HashMap) parameterObject;
            if (keyName.contains("ew.paramNameValuePairs.") && sql.toLowerCase().contains(" like ?")) {
                // 第一种情况：在业务层进行条件构造产生的模糊查询关键字
                AbstractWrapper wrapper = (AbstractWrapper) parameter.get("ew");
                parameter = (HashMap) wrapper.getParamNameValuePairs();

                String[] keyList = keyName.split("\\.");
                // ew.paramNameValuePairs.MPGENVAL1，截取字符串之后，获取第三个，即为参数名
                Object a = parameter.get(keyList[2]);
                if (a instanceof String && (a.toString().contains("_") || a.toString().contains("\\") || a.toString()
                        .contains("%"))) {
                    parameter.put(keyList[2],
                            "%" + this.escapeStr(a.toString().substring(1, a.toString().length() - 1)) + "%");
                }
            } else if (!keyName.contains("ew.paramNameValuePairs.") && sql.toLowerCase().contains(" like ?")) {
                // 第二种情况：未使用条件构造器，但是在service层进行了查询关键字与模糊查询符`%`手动拼接
                Object a = parameter.get(keyName);
                if (a instanceof String && (a.toString().contains("_") || a.toString().contains("\\") || a.toString()
                        .contains("%"))) {
                    parameter.put(keyName,
                            "%" + this.escapeStr(a.toString().substring(1, a.toString().length() - 1)) + "%");
                }
            } else {
                try{
                    if(parameter.containsKey(keyName)){
                        // 第三种情况：在Mapper类的注解SQL中进行了模糊查询的拼接
                        Object a = parameter.get(keyName);
                        if (a instanceof String && (a.toString().contains("_") || a.toString().contains("\\") || a.toString()
                                .contains("%"))) {
                            parameter.put(keyName, this.escapeStr(a.toString()));
                        }
                    }else{
                        log.info("无法找到当前key{}", keyName);
                    }

                }catch (Exception ex){
                    log.error("发现数据解析异常", ex);
                }
            }
        }
        return sql;
    }

    public static String escapeStr(String str) {
        if (StringUtils.isNotEmpty(str)) {
            str = str.replaceAll("\\\\", "\\\\\\\\");
            str = str.replaceAll("_", "\\\\_");
            str = str.replaceAll("%", "\\\\%");
        }
        return str;
    }

    /**
     * 添加条件--指定模式
     *
     * @return
     */
    private String modifyModeSql(String sql) {
        //开启或者不为超管
        if(sqlDefualtAdditionalConfiguration.getIsEnable()){
            sql = TableUtil.addModelSql(sql, GlobalContext.getDatabaseModl(),sqlDefualtAdditionalConfiguration.getSkipTable());
        }
        return sql;
    }

}