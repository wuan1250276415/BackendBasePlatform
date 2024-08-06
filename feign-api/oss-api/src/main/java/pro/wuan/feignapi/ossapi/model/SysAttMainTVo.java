package pro.wuan.feignapi.ossapi.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class SysAttMainTVo implements Serializable {

    // 页面附件ID列表
    private Long[] sysAttMainIds;

    // 页面标识符号，任意字段内容均可
    private String pageMark;

    private String moduleKey;
}
