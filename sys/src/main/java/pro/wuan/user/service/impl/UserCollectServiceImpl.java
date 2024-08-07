package pro.wuan.user.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.feignapi.userapi.entity.UserCollect;
import pro.wuan.feignapi.userapi.model.UserContext;
import pro.wuan.user.mapper.UserCollectMapper;
import pro.wuan.user.service.IUserCollectService;

import java.util.List;

@Service
public class UserCollectServiceImpl extends BaseServiceImpl<UserCollectMapper, UserCollect> implements IUserCollectService {



    /**
     * 批量修改
     * @param collects
     * @return
     */
    @Override
    public Result batchUpdate(List<UserCollect> collects) {
        //获取当前用户所有收藏
        QueryWrapper<UserCollect> queryWrapper=new QueryWrapper();
        queryWrapper.eq("create_user_id", UserContext.getCurrentUserId());
        List<UserCollect> list=this.selectList(queryWrapper);
        if(list!=null&&list.size()>0){
            //删除collects中没有,list中有的
            for(int i=0;i<list.size();i++){
                boolean  flag=true;
                for(int j=0;j<collects.size();j++){
                    if(list.get(i).getId()==collects.get(j).getId()){
                        flag=false;//匹配到了就不需要删除
                    }
                }
                //删除
                if(flag){
                    this.deleteById(list.get(i).getId());
                }
            }
        }
        //判断哪些新增,哪些更新
        if(collects!=null&&collects.size()>0){
            //collects中在list中有的则需要更新,没有的则需要新增
            for(int i=0;i<collects.size();i++){
                boolean  flag=true;
                for(int j=0;j<list.size();j++){
                    if(collects.get(i).getId()==list.get(j).getId()){
                        flag=false;//需要更新
                        this.updateById(collects.get(i));
                    }
                }
                //新增
                if(flag){
                    this.insert(collects.get(i));
                }
            }
        }
        return Result.success();
    }








}
