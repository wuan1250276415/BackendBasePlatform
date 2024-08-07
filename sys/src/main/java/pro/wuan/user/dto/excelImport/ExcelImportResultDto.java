package pro.wuan.user.dto.excelImport;

import lombok.Data;

/**
 * 导入excel结果
 */
@Data
public class ExcelImportResultDto<T> {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 结果对象
     */
    private T result;

    /**
     * 构造方法
     *
     * @param success
     * @param message
     */
    public ExcelImportResultDto(boolean success, String message, T result) {
        this.success = success;
        this.message = message;
        this.result = result;
    }

}
