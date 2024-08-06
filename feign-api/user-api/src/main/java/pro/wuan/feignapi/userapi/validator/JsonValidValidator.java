package pro.wuan.feignapi.userapi.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;
import pro.wuan.feignapi.userapi.JsonValid;


/**
 * 自定义注解校验器，需指定注解和校验类型，校验是否是 JSON 格式
 */
public class JsonValidValidator implements ConstraintValidator<JsonValid, String> {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void initialize(JsonValid constraintAnnotation) {
        // 新版本可以不用实现这个方法
    }

    /**
     * 校验逻辑，是否校验成功
     *
     * @param value   被校验的值
     * @param context 上下文环境信息
     * @return boolean 校验结果
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(value)) {
            return true;
        }
        try {
            OBJECT_MAPPER.readTree(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
