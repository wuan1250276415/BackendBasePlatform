package pro.wuan.common.log;


import pro.wuan.common.log.constant.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AspectLog {
	
	//描述3
	String description();

	//类型： 设置默认操作类型为查询
	String type() default OperationType.OPERATION_QUERY;
}
