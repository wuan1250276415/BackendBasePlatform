package pro.wuan.common.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.springframework.stereotype.Service;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.common.web.entity.SearchCondition;
import pro.wuan.common.web.mapper.SearchConditionMapper;
import pro.wuan.common.web.service.ISearchConditionService;

/**
 * 搜索条件接口实现
 *
 * @author: oldone
 * @date: 2021/9/6 13:43
 */
@Service
public class SearchConditionServiceImpl extends BaseServiceImpl<SearchConditionMapper, SearchCondition> implements ISearchConditionService {

    /**
     * 是否已存在
     * @param param
     * @return
     */
    @Override
    public Boolean isExists(String param) {
        QueryWrapper<SearchCondition> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("search_name", param);
        return CollectionUtils.isNotEmpty(this.selectList(queryWrapper));
    }
}
