package pro.wuan.common.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IBaseMapper<T> extends BaseMapper<T> {

    // 自定义基础类方法

    /**
     * 全量修改所有字段
     *
     * @param entity 实体
     * @return 修改数量
     */
    int updateAllById(@Param(Constants.ENTITY) T entity);

    /**
     * 批量插入所有字段
     * <p>
     * 只测试过MySQL！只测试过MySQL！只测试过MySQL！
     *
     * @param entityList 实体集合
     * @return 插入数量
     */
    int batchInsertNoCascade(List<T> entityList);
}
