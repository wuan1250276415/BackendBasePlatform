package pro.wuan.common.mq.exception;

/**
 * @description:
 * @author: oldone
 * @date: 2020/9/9 10:09
 */
public class RocketMQException extends RuntimeException {

    private static final long serialVersionUID = -3051475272115841569L;

    public RocketMQException(String message) {
        super(message);
    }
}
