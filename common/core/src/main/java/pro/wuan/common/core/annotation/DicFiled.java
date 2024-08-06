package pro.wuan.common.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DicFiled {
	//数据字典Key
	String key();
	//字典值
	String name();
	//需要转换的属性名
	String targetProperty();
}
