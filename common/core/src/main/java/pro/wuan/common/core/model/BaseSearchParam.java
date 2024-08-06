package pro.wuan.common.core.model;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Data
public class BaseSearchParam {


    /**
     * 前端数据查询区域
     */
    private Map<String,BaseQueryValue> queryPrams;


    /**
     * 排序字段
     */
    private String sortField;


    /**
     * 排序规则
     */
    private String sortOrder;

    public Map<String, BaseQueryValue> getQueryPrams() {
        if(queryPrams == null){
            queryPrams = new HashMap<>();
        }
        return queryPrams;
    }


    /**
     * 私有化wrapper
     * @ignore
     */
    private QueryWrapper wrapper;


    // 提供注入查询条件的可能
    public boolean addQueryParams(String key,BaseQueryValue baseQueryValue){
        //step1 参数校验
        if(StringUtils.isBlank(key) || baseQueryValue == null){
            return false;
        }
        Object[] values = baseQueryValue.getValues();
        if(values == null || values.length == 0){
            return false;
        }
        // 已经存在的不允许重复添加
        if(getQueryPrams().containsKey(key)){
            return false;
        }
        //执行添加操作
        getQueryPrams().put(key,baseQueryValue);
        return true;
    }

    /**
     * 根据key值查找map中的指定元素，该元素继续保留在map中
     * @author ivan
     * @blame
     * @since 2020/7/16
     */
    public BaseQueryValue peek(String key){
        BaseQueryValue baseQueryValue = null;
        if(!StringUtils.isBlank(key)){
            baseQueryValue = getQueryPrams().get(key);
        }
        return baseQueryValue;
    }

    /**
     * 根据key值查找map中的指定元素，并将该元素移出map结构
     * @author ivan
     * @blame
     * @since 2020/7/16
     */
    public BaseQueryValue pop(String key){
        BaseQueryValue baseQueryValue = peek(key);
        //取消此项错位通用查询条件
        getQueryPrams().remove(key);
        return baseQueryValue;
    }

}
