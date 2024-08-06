package pro.wuan.common.web.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.wuan.common.web.entity.SearchCondition;
import pro.wuan.common.web.mapper.SearchConditionMapper;
import pro.wuan.common.web.service.ISearchConditionService;

/**
 * 搜索条件控制层
 *
 * @author: oldone
 * @date: 2021/9/6 13:59
 */
@RestController
@RequestMapping("/search")
public class SearchController extends CURDController<ISearchConditionService, SearchConditionMapper, SearchCondition> {


}
