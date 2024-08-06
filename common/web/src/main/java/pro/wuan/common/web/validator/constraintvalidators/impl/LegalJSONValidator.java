package pro.wuan.common.web.validator.constraintvalidators.impl;

import com.alibaba.fastjson.JSONObject;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pro.wuan.common.web.validator.annotation.LegalJSON;

/**
 * 合法json校验器
 *
 * @author robertHu
 * @date 2024/1/15
 */
public class LegalJSONValidator implements ConstraintValidator<LegalJSON, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        JSONObject jsonObject;
        try {
            jsonObject = JSONObject.parseObject(value);
        } catch (Exception e) {
            return false;
        }
        return jsonObject != null;
    }
}