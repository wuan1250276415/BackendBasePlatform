package pro.wuan.post.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.feignapi.userapi.entity.OrgPostModel;

import java.util.List;

/**
 * 岗位
 *
 * @author: liumin
 * @date: 2021-08-30 11:19:43
 */
@Mapper
public interface OrgPostMapper extends IBaseMapper<OrgPostModel> {

    /**
     * 根据用户id获取关联的岗位列表
     *
     * @param userId 用户id
     * @return 岗位列表
     */
    public List<OrgPostModel> getPostListByUserId(@Param("userId") Long userId);
	
}
