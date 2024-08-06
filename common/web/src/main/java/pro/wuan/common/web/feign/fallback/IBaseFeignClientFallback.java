package pro.wuan.common.web.feign.fallback;

import com.baomidou.mybatisplus.core.metadata.IPage;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.core.model.PageSearchParam;

import java.util.List;

/**
 * @program: tellhowcloud
 * @description: 基础的服务降级
 * @author: HawkWang
 * @create: 2021-08-27 14:28
 **/
public interface IBaseFeignClientFallback<IdModel extends IDModel> {
    Class<IdModel> getModelClass();

    IdModel selectById(Long id);

    List<IdModel> selectByProperty(IdModel idModel);

    IPage<IdModel> selectPage(PageSearchParam pageSearchParam);
}
