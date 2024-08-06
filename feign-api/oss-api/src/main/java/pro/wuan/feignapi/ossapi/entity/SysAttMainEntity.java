package pro.wuan.feignapi.ossapi.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pro.wuan.common.core.model.BaseDeleteModel;


@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_att_main")
@Data
public class SysAttMainEntity extends BaseDeleteModel {


    /**
     * 附件流
     *
     * @ignore
     */
    private byte[] content;


    /**
     * 附加名称
     *
     * @ignore
     */
    private String fileName;


    /**
     * 文件缺省路径
     *
     * @ignore
     */
    private String filePath;


    /**
     * 文件大小
     *
     * @ignore
     */
    private Long fileSize;


    /**
     * 文件格式
     *
     * @ignore
     */
    @TableField(value = "file_type")
    private String fileFormat;


    /**
     * 模块ClassName
     */
    private String moduleBean;


    /**
     * 模块id
     */
    private Long moduleId;


    /**
     * 附件对应的key
     */
    @NotBlank(message = "附件key不能为空")
    private String moduleKey;


    /**
     * 原始头像的图片
     *
     * @ignore
     */
    private String originalPath;


    /**
     * 文件存储路径，''1''代表DB存储，  ‘2’或者不填代表文件系统存储
     */
    private String saveType;


    /**
     * 业务实例ID
     *
     * @ignore
     */
    private Long serviceInstanceId;


    /**
     * 状态  1可用  0不可用
     *
     * @ignore
     */
    private String status;

    /**
     * base64图片
     */
    @TableField(exist = false)
    private String baseStr;


}
