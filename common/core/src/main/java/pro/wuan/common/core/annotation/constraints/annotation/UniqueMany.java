package pro.wuan.common.core.annotation.constraints.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pro.wuan.common.core.validator.UniqueManyValidator;

import java.lang.annotation.*;

/**
 * 多属性唯一性校验,使用方法：@UniqueMany(groups = ValidatorUpdateCheck.class, equalPropertys = {"name"}, notEqualPropertys = {"id"})
 *
 * @program: tellhowcloud
 * @author: HawkWang
 * @create: 2021-09-17 17:13
 **/
@Repeatable(UniqueMany.UniqueManys.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Constraint(validatedBy = UniqueManyValidator.class)
public @interface UniqueMany {
    String message() default "数据已经存在，请重新输入";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 相等的属性
     *
     * @return 属性数组
     */
    String[] equalPropertys();

    /**
     * 不相等的属性
     *
     * @return 属性数组
     */
    String[] notEqualPropertys() default {};

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Documented
    public @interface UniqueManys {
        UniqueMany[] value();
    }
}
