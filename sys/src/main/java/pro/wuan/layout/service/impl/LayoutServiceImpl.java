package pro.wuan.layout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.feignapi.userapi.entity.Layout;
import pro.wuan.layout.mapper.LayoutMapper;
import pro.wuan.layout.service.ILayoutService;

@Service
public class LayoutServiceImpl extends BaseServiceImpl<LayoutMapper, Layout> implements ILayoutService {



    //添加系统布局
    @Override
    public Result save(Layout layout) {
        //首先判断该布局是否已经添加
        QueryWrapper<Layout> queryWrapper=new QueryWrapper();
        queryWrapper.eq("layout_key",layout.getLayoutKey());
        Layout existLayout=this.selectOne(queryWrapper);
        if(existLayout!=null){
            return  Result.failure("该布局已经添加");
        }
        int i=this.insert(layout);
        if(i!=1){
            return  Result.failure("系统异常，添加失败！");
        }
        return Result.success();
    }











}
