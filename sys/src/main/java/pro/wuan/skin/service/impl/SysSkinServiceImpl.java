package pro.wuan.skin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.feignapi.userapi.entity.SysSkin;
import pro.wuan.skin.mapper.SysSkinMapper;
import pro.wuan.skin.service.ISysSkinService;

@Service
public class SysSkinServiceImpl extends BaseServiceImpl<SysSkinMapper, SysSkin> implements ISysSkinService {



    //添加皮肤
    @Override
    public Result save(SysSkin sysSkin) {
        //首先判断该皮肤的主皮肤是否已经添加
        QueryWrapper<SysSkin> queryWrapper=new QueryWrapper();
        queryWrapper.eq("main_skin",sysSkin.getMainSkin());
        SysSkin existSkin=this.selectOne(queryWrapper);
        if(existSkin!=null){
            return  Result.failure("该主皮肤已经添加");
        }
        int i=this.insert(sysSkin);
        if(i!=1){
            return  Result.failure("系统异常，添加失败！");
        }
        return Result.success();
    }











}
