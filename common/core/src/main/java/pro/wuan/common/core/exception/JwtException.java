package pro.wuan.common.core.exception;

/**
 * @description: 自定义jwt认证异常
 * @author: oldone
 * @date: 2020/7/31 17:08
 */
public class JwtException extends RuntimeException {
    public JwtException() {
    }

    public JwtException(String message) {
        super(message);
    }

    public JwtException(Throwable cause) {
        super(cause);
    }

    public JwtException(String message, Throwable cause) {
        super(message, cause);
    }
}
