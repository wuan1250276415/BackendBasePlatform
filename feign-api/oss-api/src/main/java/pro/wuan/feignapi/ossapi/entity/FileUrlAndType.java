package pro.wuan.feignapi.ossapi.entity;

import lombok.Data;

/**
 * @description: 文件相关实体
 * @author: oldone
 * @date: 2020/9/23 20:33
 */
@Data
public class FileUrlAndType {
    /**
     * 文件地址
     */
    private String url;
    /**
     * 文件业务类型
     */
    private String type;
}

