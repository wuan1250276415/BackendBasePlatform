package pro.wuan.feignapi.userapi.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 单位人员数量
 *
 * @author: oldone
 * @date: 2022/3/30 15:02
 */
@Data
public class UnitUserNumVo implements Serializable {

    //单位id
    private Long unitId;
    //用户数量
    private Integer userNum;
}
