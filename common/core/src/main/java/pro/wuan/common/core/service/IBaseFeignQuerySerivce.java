package pro.wuan.common.core.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pro.wuan.common.core.model.IDModel;

import java.util.List;

/**
 * @author: oldone
 * @date: 2021/9/29 14:50
 */
public interface IBaseFeignQuerySerivce<IdModel extends IDModel> {

    /**
     * 根据ID查询信息，根据实体注解进行转换
     *
     * @param id
     * @return
     */
    @GetMapping("/selectById")
    IdModel selectById(@RequestParam("id") Long id);

    /**
     * 根据实体属性查询实体信息
     *
     * @param ids
     * @return
     */
    @GetMapping("/selectBatchByIds")
    List<IdModel> selectBatchByIds(@RequestParam("ids") List<Long> ids);
}
