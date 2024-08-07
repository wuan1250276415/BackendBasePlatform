package pro.wuan.user.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.feignapi.userapi.entity.UserSet;
import pro.wuan.layout.mapper.LayoutMapper;
import pro.wuan.skin.mapper.SysSkinMapper;
import pro.wuan.user.mapper.UserSetMapper;
import pro.wuan.user.service.IUserSetService;

import javax.annotation.Resource;

@Service
public class UserSetServiceImpl extends BaseServiceImpl<UserSetMapper, UserSet> implements IUserSetService {


    @Resource
    private SysSkinMapper sysSkinMapper;
    @Resource
    private LayoutMapper layoutMapper;


    //添加用户设置
    @Override
    public Result save(UserSet userSet) {
        //首先判断该用户时候已有系统设置信息,有就修改,没有就新增
        QueryWrapper<UserSet> queryWrapper=new QueryWrapper();
        queryWrapper.eq("create_user_id",userSet.getCreateUserId());
        UserSet  existUserSet=this.selectOne(queryWrapper);
        if(existUserSet!=null){
            if(this.update(userSet,queryWrapper)>0){
                return  Result.success();
            }else{
                return  Result.failure();
            }
        }else{
            if(this.insert(userSet)>0){
                return  Result.success();
            }else{
                return  Result.failure();
            }
        }
    }



    //根据用户id获取用户设置
    @Override
    public UserSet getByUserId(Long userId) {
        QueryWrapper<UserSet> queryWrapper=new QueryWrapper();
        queryWrapper.eq("create_user_id",userId);
        UserSet  userSet=this.selectOne(queryWrapper);
        if(userSet!=null){
            //获取其他设置信息
            //皮肤信息
            if(userSet.getSkinId()!=null||userSet.getSkinId()!=0L){
                userSet.setSysSkin(sysSkinMapper.selectById(userSet.getSkinId()));
            }
            //布局信息
            if(userSet.getLayoutId()!=null||userSet.getLayoutId()!=0L){
                userSet.setLayout(layoutMapper.selectById(userSet.getLayoutId()));
            }
            return userSet;
        }
        return null;

    }









}
