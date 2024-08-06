package pro.wuan.common.db.injector;

import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.session.Configuration;
import pro.wuan.common.db.injector.method.BatchInsertNoCascade;
import pro.wuan.common.db.injector.method.UpdateAllById;

import java.util.List;

/**
 * @program: tellhowcloud
 * @description: 自定义SQL注入
 * @author: HawkWang
 * @create: 2021-08-25 16:25
 **/
public class MybatisSqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Configuration configuration, Class<?> mapperClass, TableInfo tableInfo) {

        List<AbstractMethod> methodList = super.getMethodList(configuration,mapperClass,tableInfo);
        //增加自定义方法
        methodList.add(new BatchInsertNoCascade(field -> field.getFieldFill() != FieldFill.UPDATE));
        methodList.add(new UpdateAllById(field -> !ArrayUtil.containsAny(new String[]{
                "create_time", "create_user_id", "delete_flag", "update_user_id", "update_time"
        }, field.getColumn())));
        return methodList;

    }

}
