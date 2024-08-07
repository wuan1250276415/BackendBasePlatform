package pro.wuan.post.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.element.service.IOrgElementService;
import pro.wuan.feignapi.userapi.entity.*;
import pro.wuan.organ.service.IOrgOrganService;
import pro.wuan.post.mapper.OrgPostMapper;
import pro.wuan.post.service.IOrgPostService;
import pro.wuan.user.mapper.OrgUserMapper;
import pro.wuan.user.mapper.OrgUserPostMapper;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 岗位
 *
 * @author: liumin
 * @date: 2021-08-30 11:19:43
 */
@Validated
@Service("orgPostService")
public class OrgPostServiceImpl extends BaseServiceImpl<OrgPostMapper, OrgPostModel> implements IOrgPostService {

    @Autowired
    private IOrgOrganService organService;

    @Resource
    private IOrgElementService elementService;

    @Resource
    private OrgUserPostMapper userPostMapper;

    @Resource
    private OrgUserMapper userMapper;

    /**
     * 保存岗位
     *
     * @param postModel 岗位实体
     * @return 岗位实体
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<OrgPostModel> savePost(OrgPostModel postModel) {
        OrgOrganModel department = organService.getOrganById(postModel.getDeptId());
        if (department == null) {
            return Result.failure("所属部门不存在");
        }
        OrgOrganModel unit = organService.getDirectParentUnit(postModel.getDeptId());
        if (unit != null) {
            postModel.setUnitId(unit.getId());
        }
        if (this.insert(postModel) <= 0) {
            return Result.failure(postModel);
        }

        // 保存元素
        OrgElementModel elementModel = new OrgElementModel();
        elementModel.setId(postModel.getId());
        elementModel.setName(postModel.getName());
        elementModel.setType(CommonConstant.ELEMENT_TYPE.POST.getValue());
        elementModel.setSort(postModel.getSort());
        elementModel.setParentId(postModel.getDeptId());
        elementModel.setTreeIds(department.getTreeIds() + CommonConstant.SEPARATOR_COMMA + postModel.getId());
        elementModel.setStatus(CommonConstant.USE_STATUS.USE.getValue());
        elementService.insert(elementModel);
        return Result.success(postModel);
    }

    /**
     * 更新岗位
     *
     * @param postModel 岗位实体
     * @return 岗位实体
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<OrgPostModel> updatePost(OrgPostModel postModel) {
        OrgPostModel post = this.selectById(postModel.getId());
        if (post == null) {
            return Result.failure("岗位信息不存在");
        }
        OrgOrganModel department = organService.getOrganById(postModel.getDeptId());
        if (department == null) {
            return Result.failure("所属部门不存在");
        }
        OrgOrganModel unit = organService.getDirectParentUnit(postModel.getDeptId());
        if (unit != null) {
            postModel.setUnitId(unit.getId());
        }
        // 更新元素
        OrgElementModel elementModel = new OrgElementModel();
        elementModel.setId(postModel.getId());
        elementModel.setName(postModel.getName());
        elementModel.setType(CommonConstant.ELEMENT_TYPE.POST.getValue());
        elementModel.setSort(postModel.getSort());
        elementModel.setParentId(postModel.getDeptId());
        elementModel.setTreeIds(department.getTreeIds() + CommonConstant.SEPARATOR_COMMA + postModel.getId());
        elementService.updateById(elementModel);

        if (this.updateById(postModel) == 1) {
            return Result.success(postModel);
        } else {
            return Result.failure(postModel);
        }
    }

    /**
     * 删除岗位
     *
     * @param id 岗位id
     * @return 删除成功/失败
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deletePost(@NotNull(message = "id不能为空") Long id) {
        OrgPostModel post = this.selectById(id);
        if (post == null) {
            return Result.failure("岗位信息不存在");
        }
        LambdaQueryWrapper<OrgUserPostModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgUserPostModel::getPostId, id);
        List<OrgUserPostModel> userPostList = userPostMapper.selectList(wrapper);
        if (CollectionUtil.isNotEmpty(userPostList)) {
            List<Long> userIdList = userPostList.stream().map(OrgUserPostModel::getUserId).collect(Collectors.toList());
            LambdaQueryWrapper<OrgUserModel> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.in(OrgUserModel::getId, userIdList);
            List<OrgUserModel> userList = userMapper.selectList(userWrapper);
            if (CollectionUtil.isNotEmpty(userList)) {
                String userInfo = userList.stream().map(OrgUserModel::getLoginName).collect(Collectors.joining(CommonConstant.SEPARATOR_COMMA));
                return Result.failure("岗位存在关联的用户，用户登录名：" + userInfo);
            }
        }
        // 删除元素
        elementService.deleteElement(id);

        if (this.deleteById(id) > 0) {
            return Result.success("删除成功！");
        } else {
            return Result.failure("删除失败！");
        }
    }

    /**
     * 获取组织机构下的岗位列表
     *
     * @param organId 组织机构id
     * @return 岗位实体列表
     */
    public Result<List<OrgPostModel>> getPostListByOrgan(@NotNull(message = "organId不能为空") Long organId) {
        return Result.success(this.getPostListByOrganId(organId));
    }

    /**
     * 获取组织机构下的岗位列表
     *
     * @param organId 组织机构id
     * @return 岗位实体列表
     */
    public List<OrgPostModel> getPostListByOrganId(@NotNull(message = "organId不能为空") Long organId) {
        LambdaQueryWrapper<OrgPostModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrgPostModel::getDeptId, organId);
        List<OrgPostModel> list = dao.selectList(wrapper);
        return list;
    }

    /**
     * 根据用户id获取关联的岗位列表
     *
     * @param userId 用户id
     * @return 岗位列表
     */
    public List<OrgPostModel> getPostListByUserId(@NotNull(message = "userId不能为空") Long userId) {
        return dao.getPostListByUserId(userId);
    }

}
