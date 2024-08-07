package pro.wuan.user.dto;

import lombok.Data;

/**
 * 获取人脸识别准确度参数
 */
@Data
public class LoadRlsbVo {

    private BoxRlsbVo box;

    private String subject;

    private String similarity;
}
