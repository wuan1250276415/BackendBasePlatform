package pro.wuan.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.feignapi.userapi.entity.OrgUserPostModel;
import pro.wuan.user.mapper.OrgUserPostMapper;
import pro.wuan.user.service.IOrgUserPostService;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户岗位关联表
 *
 * @author: liumin
 * @date: 2021-08-30 10:40:59
 */
@Service("orgUserPostService")
public class OrgUserPostServiceImpl extends BaseServiceImpl<OrgUserPostMapper, OrgUserPostModel> implements IOrgUserPostService {

    /**
     * 批量保存用户岗位关联表
     *
     * @param userId
     * @param postIdList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveUserPost(@NotNull(message = "userId不能为空") Long userId, List<Long> postIdList) {
        if (CollectionUtil.isNotEmpty(postIdList)) {
            List<OrgUserPostModel> userPostList = new ArrayList<>();
            for (Long postId : postIdList) {
                OrgUserPostModel userPostModel = new OrgUserPostModel();
                userPostModel.setUserId(userId);
                userPostModel.setPostId(postId);
                userPostList.add(userPostModel);
            }
            this.batchInsertNoCascade(userPostList);
        }
    }

    /**
     * 批量删除用户岗位关联表
     *
     * @param userId
     * @param postIdList
     * @return
     */
    public int batchDeleteUserPost(@NotNull(message = "userId不能为空") Long userId, List<Long> postIdList) {
        LambdaQueryWrapper<OrgUserPostModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgUserPostModel::getUserId, userId);
        wrapper.in(OrgUserPostModel::getPostId, postIdList);
        return this.delete(wrapper);
    }

    /**
     * 批量删除用户岗位关联表
     *
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteUserPost(@NotNull(message = "userId不能为空") Long userId) {
        LambdaQueryWrapper<OrgUserPostModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgUserPostModel::getUserId, userId);
        return this.delete(wrapper);
    }

    /**
     * 根据岗位id列表查询用户岗位关联实体信息
     *
     * @param postIdList
     * @return
     */
    public List<OrgUserPostModel> getUserPostListByPostIdList(List<Long> postIdList) {
        if (CollectionUtil.isEmpty(postIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<OrgUserPostModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(OrgUserPostModel::getPostId, postIdList);
        return this.selectList(queryWrapper);
    }

}
