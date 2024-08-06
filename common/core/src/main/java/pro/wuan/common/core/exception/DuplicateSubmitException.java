package pro.wuan.common.core.exception;

/**
 * 业务表单防止重复提交拦自定义异常
 *
 */
public class DuplicateSubmitException extends RuntimeException{

    public DuplicateSubmitException(String msg) {
        super(msg);
    }

}
