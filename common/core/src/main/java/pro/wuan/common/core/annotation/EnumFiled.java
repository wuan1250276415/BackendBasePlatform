package pro.wuan.common.core.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumFiled {

	//类全路径
	Class<? extends Enum> type();

	//需要转换的属性名
	String targetProperty();

	//获取code的get方法
	String codeMethod() default "getValue";

	//获取名称的get方法
	String nameMethod() default "getName";
}
