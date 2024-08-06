package pro.wuan.common.core.validator;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import pro.wuan.common.core.annotation.ServiceType;
import pro.wuan.common.core.annotation.constraints.annotation.UniqueMany;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.common.core.utils.SpringBeanUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * 唯一性验证器
 *
 * @author: oldone
 * @date: 2021/9/6 17:36
 */
@Slf4j
public class UniqueManyValidator implements ConstraintValidator<UniqueMany, Object> {
    private UniqueMany parameters;

    @Override
    public void initialize(UniqueMany parameters) {
        this.parameters = parameters;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        String[] eqs = parameters.equalPropertys();
        String[] nes = parameters.notEqualPropertys();
        Class idModel = value.getClass();
        QueryWrapper wrapper = new QueryWrapper<>();
        wrapper.setEntityClass(idModel);
        for (String eq : eqs) {
            try {
                Object propertyVal = BeanUtils.getProperty(value, eq);
                wrapper.eq(getClolumn(eq, idModel), propertyVal);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

        }

        for (String ne : nes) {
            try {
                Object propertyVal = BeanUtils.getProperty(value, ne);
                wrapper.ne(getClolumn(ne, idModel), propertyVal);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

        }
        IBaseService iBaseService = null;
        ServiceType serviceType = (ServiceType) idModel.getAnnotation(ServiceType.class);
        String serverName = serviceType.serviceName();
        if (StringUtils.isNotBlank(serverName)) {
            iBaseService = (IBaseService) SpringBeanUtil.getBean(serverName);
        } else {
            iBaseService = SpringBeanUtil.getBean(serviceType.service());
        }
        Long count = iBaseService.selectCount(wrapper);
        return null == count || count <= 0;
    }

    private String getClolumn(String property, Class clazz) {
        Map<String, ColumnCache> columnCacheMap = LambdaUtils.getColumnMap(clazz);
        ColumnCache columnCache = columnCacheMap.get(LambdaUtils.formatKey(property));
        return columnCache == null ? null : columnCache.getColumn();
    }
}
