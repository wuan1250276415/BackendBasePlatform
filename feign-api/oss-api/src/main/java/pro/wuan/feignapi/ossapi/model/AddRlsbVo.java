package pro.wuan.feignapi.ossapi.model;

import lombok.Data;

/**
 * @author Ace丶小磊
 * @Version V1.0
 * @ClassName AddRlsbVo
 * @Description 人脸识别录入返回结果Vo
 * @date 2021年12月21日 14:42
 */
@Data
public class AddRlsbVo {

    /**
     * 照片标识ID
     */
    private String image_id;
    /**
     * 照片主题
     */
    private String subject;
}
