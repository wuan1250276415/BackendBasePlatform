package pro.wuan.common.core.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import pro.wuan.common.core.annotation.constraints.annotation.NotNullList;

import java.util.List;

/**
 * List集合不能为空和size必须>0校验
 *
 * @author: huangtianji
 * @date: 2021/11/25 21:43
 */
@Slf4j
public class NotNullListValidator implements ConstraintValidator<NotNullList, List> {

    @Override
    public boolean isValid(List list, ConstraintValidatorContext constraintValidatorContext) {
        if(list == null){
            return false;
        }
        if(list.size() == 0){
            return false;
        }
        return true;
    }
}
