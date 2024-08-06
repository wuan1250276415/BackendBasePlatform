package pro.wuan.common.web.handler;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pro.wuan.common.core.constant.HttpStatus;
import pro.wuan.common.core.exception.CommonException;
import pro.wuan.common.core.exception.DuplicateSubmitException;
import pro.wuan.common.core.exception.JwtException;
import pro.wuan.common.core.model.Result;
import pro.wuan.common.core.utils.ReflectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * controller层统一异常处理，包括自定义异常
 *
 * @author: oldone
 * @date: 2021/9/6 15:25
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 自定义通用异常处理
     */
    @ExceptionHandler(CommonException.class)
    public Result commonException(CommonException e) {
        return Result.failure(e.getMessage());
    }

    /**
     * 处理所有不可知的异常
     */
    @ExceptionHandler(Throwable.class)
    public Result handleException(Throwable e) {
        // 打印堆栈信息
        log.error("error:" + ExceptionUtils.getStackTrace(e));
        return Result.failure("系统异常，请联系管理员！");
    }

    @ExceptionHandler(DuplicateSubmitException.class)
    public Result customException(DuplicateSubmitException e) {
        log.error("error:" + ExceptionUtils.getStackTrace(e));
        return Result.DuplicateSubmitError("表单不可重复提交！");
    }

    @ExceptionHandler(JwtException.class)
    public Result JwtException(JwtException e) {
        log.error("error:" + ExceptionUtils.getStackTrace(e));
        return Result.failure(HttpStatus.UNAUTHORIZED.value(),e.getMessage(),null);
    }

    /**
     * 增加列表的数据校验全局异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result handleConstraintViolationException(ConstraintViolationException e) {
        log.error("数据校验出现问题{}，异常类型：{}", e.getMessage(), e.getClass());
        Map<String, String> errorMap = new HashMap<>();
        e.getConstraintViolations().forEach(constraintViolationError -> {
            Object obj = constraintViolationError.getLeafBean();
            if (null != ReflectionUtils.getDeclaredField(obj, "id")) {
                Object id = ReflectionUtils.getFieldValue(obj, "id");
                errorMap.put(String.valueOf(id), constraintViolationError.getMessage());
            } else {
                errorMap.put(String.valueOf(constraintViolationError.getInvalidValue()), constraintViolationError.getMessage());
            }
        });
        String msg = "";
        for (Map.Entry<String, String> entry : errorMap.entrySet()) {
            msg += entry.getValue() + ";";
        }
        if (Strings.isNotBlank(msg)) {
            msg = msg.substring(0, msg.length() - 1);
        }
        return Result.failure(HttpStatus.BAD_REQUEST.value(), msg, errorMap);
    }

    /**
     * 处理所有接口数据验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("数据校验出现问题{}，异常类型：{}", e.getMessage(), e.getClass());
        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> errorMap = new HashMap<>();
        bindingResult.getFieldErrors().forEach((fieldError) -> {
            errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        });
        if (CollectionUtils.isEmpty(errorMap)) {
            bindingResult.getAllErrors().forEach((error) -> {
                errorMap.put(error.getObjectName(), error.getDefaultMessage());
            });
        }
        String msg = "";
        for (Map.Entry<String, String> entry : errorMap.entrySet()) {
            msg += entry.getValue() + ";";
        }
        if (Strings.isNotBlank(msg)) {
            msg = msg.substring(0, msg.length() - 1);
        }
        return Result.failure(HttpStatus.BAD_REQUEST.value(), msg, errorMap);
    }
}
