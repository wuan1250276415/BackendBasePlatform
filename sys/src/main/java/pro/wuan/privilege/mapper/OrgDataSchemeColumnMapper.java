package pro.wuan.privilege.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pro.wuan.common.core.mapper.IBaseMapper;
import pro.wuan.common.core.model.PageSearchParam;
import pro.wuan.feignapi.userapi.entity.OrgDataSchemeColumnModel;
import pro.wuan.privilege.vo.DataSchemeColumnAndSymbolIdVO;
import pro.wuan.privilege.vo.DataSchemeColumnAndSymbolVO;

import java.util.List;

/**
 * 数据方案字段
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
@Mapper
public interface OrgDataSchemeColumnMapper extends IBaseMapper<OrgDataSchemeColumnModel> {

    /**
     * 分页查询数据方案字段
     *
     * @param mppage
     * @param pageSearchParam
     * @param menuResourceId
     * @return
     */
    IPage<OrgDataSchemeColumnModel> selectPage(Page<OrgDataSchemeColumnModel> mppage, @Param("param") PageSearchParam pageSearchParam, @Param("menuId") Long menuResourceId);

    /**
     * 根据菜单资源id获取数据方案字段及条件符号关联表VO列表
     *
     * @param menuResourceId
     * @return
     */
    public List<DataSchemeColumnAndSymbolVO> getDataSchemeColumnAndSymbolVOList(@Param("menuResourceId") Long menuResourceId);

    /**
     * 根据菜单资源id获取数据方案字段及条件符号关联表idVO列表
     *
     * @param menuResourceId
     * @return
     */
    public List<DataSchemeColumnAndSymbolIdVO> getDataSchemeColumnAndSymbolIdVOList(@Param("menuResourceId") Long menuResourceId);

}
