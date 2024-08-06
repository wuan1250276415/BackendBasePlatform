package pro.wuan.common.web.feign.client.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.core.model.PageSearchParam;

import java.util.List;

/**
 * @program: tellhowcloud
 * @description: 查询的Feign接口
 * @author: HawkWang
 * @create: 2021-08-27 11:34
 **/
public interface QueryFeignClient<IdModel extends IDModel> {
    /**
     * 根据ID查询信息，根据实体注解进行转换
     *
     * @param id
     * @return
     */
    @GetMapping("/selectById/{id}")
    IdModel selectById(@PathVariable Long id);

    /**
     * 根据实体属性查询实体信息
     *
     * @param idModel 实体
     * @return
     */
    @PostMapping("/selectByProperty")
    List<IdModel> selectByProperty(@RequestParam(value = "idModel") IdModel idModel);

    /**
     * 根据查询条件返回所有符合查询结果的数据，并转换
     *
     * @param pageSearchParam
     * @return
     */
    @PostMapping(value = "/selectPage")
    List<IPage<IdModel>> selectPage(@RequestParam(value = "pageSearchParam") PageSearchParam pageSearchParam);
}
