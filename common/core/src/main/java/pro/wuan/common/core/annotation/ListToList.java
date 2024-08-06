package pro.wuan.common.core.annotation;


import pro.wuan.common.core.model.IDModel;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 数据读取：根据当前IDModel找到关联子表的记录。一般配合IDToString注解，将其子表关联的实体类属性值合并到该实体某一个属性上，分号间隔
 * 数据保存：
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface ListToList {

	/**
	 * @description 关联表外键
	 * @return
	 */
	String fk();
	
	/**
	 * @description 关联对象属性
	 * @return
	 */
	Class<? extends IDModel> fkType();
}
