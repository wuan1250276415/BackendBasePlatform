package pro.wuan.common.db.component;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import pro.wuan.common.core.model.GlobalContext;

import java.util.Date;

/**
 * mybatis-plus 默认字段填充规则设置
 * @author ivan
 * @blame
 * @since 2020/7/16
 */
@Component
public class MybatisMetaObjectHandler implements MetaObjectHandler {
	private final Snowflake snowflake = new Snowflake(1,1);
    @Override
    public void insertFill(MetaObject metaObject) {
        // 生成ID
        setDefaultValue("id", snowflake.nextId(), metaObject);
//        if (UserContext.getCurrentUser() != null && UserContext.getCurrentUser().getUser() != null) {
//            setDefaultValue("createUserId", UserContext.getCurrentUser().getUser().getId(), metaObject);
//            setDefaultValue("updateUserId", UserContext.getCurrentUser().getUser().getId(), metaObject);
//            setDefaultValue("tenantId", UserContext.getCurrentUserTenantId(), metaObject);
//        }
        setDefaultValue("createUserId", GlobalContext.getCurrentUserId(), metaObject);
        setDefaultValue("updateUserId", GlobalContext.getCurrentUserId(), metaObject);
        setDefaultValue("tenantId", GlobalContext.getTenantId(), metaObject);

        setDefaultValue("createTime", new Date(), metaObject);
        setDefaultValue("updateTime", new Date(), metaObject);

    }

    @Override
    public void updateFill(MetaObject metaObject) {
//        if (UserContext.getCurrentUser() != null && UserContext.getCurrentUser().getUser() != null) {
//            setDefaultValue("updateUserId", UserContext.getCurrentUser().getUser().getId(), metaObject);
//        }

        setDefaultValue("updateUserId", GlobalContext.getCurrentUserId(), metaObject);

        setDefaultValue("updateTime", new Date(), metaObject);
    }

    /**
     * 方法判断参数是否存在
     * @author ivan
     * @blame
     * @since 2020/7/16
     */
    public void setDefaultValue(String property,Object value, MetaObject metaObject){
        Object propertyValue = getFieldValByName(property, metaObject);
        if(propertyValue == null){
            setFieldValByName(property, value, metaObject);
        }
    }
}
