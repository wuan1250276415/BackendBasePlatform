package pro.wuan.common.db.convertor.db2page;


import pro.wuan.common.core.model.IDModel;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @description 数据库数据到页面数据转换服务
 * @author chenzhong
 * @date 2019年5月12日
 */
public class DbConvert2Page {

	public DbConvert2Page() {

	}

	//需要转换的属性
	protected Set<String> convertFields = new LinkedHashSet<>();

	//不需要转换的属性
	protected Set<String> excludeFields = new LinkedHashSet<>();

	//需要转换的注解
	protected Set<Class> convertAnnotations = new LinkedHashSet<>();

	//不需要转换的属性
	protected Set<Class> excludeAnnotations = new LinkedHashSet<>();

	//设置需要转换的属性
	public DbConvert2Page setConvertFields(String... properties) {
		for (String property : properties) {
			convertFields.add(property);
		}
		return this;
	}

	//设置不需要转换的字段
	public DbConvert2Page setExcludeFields(String... properties) {
		for (String property : properties) {
			excludeFields.add(property);
		}
		return this;
	}

	//设置需要转换的注解
	public DbConvert2Page setConvertAnnotations(Class... classes) {
		for (Class clazz : classes) {
			convertAnnotations.add(clazz);
		}
		return this;
	}

	//设置不需要转换的注解
	public DbConvert2Page setExcludeAnnotations(Class... classes) {
		for (Class clazz : classes) {
			excludeAnnotations.add(clazz);
		}
		return this;
	}

	public Set<String> getConvertFields(){
		return convertFields;
	}

	public Set<String> getExcludeFields(){
		return excludeFields;
	}

	public Set<Class> getConvertAnnotations() {
		return convertAnnotations;
	}

	public Set<Class> getExcludeAnnotations() {
		return excludeAnnotations;
	}

	/**
	 * @description 制定转换规则，判断是否需要转换
	 *  原则：
	 *  	1、禁止优先原则；
	 *  	2、最少转换原则；
	 * 	规则：
	 * 		1、静止需要转换的逻辑优先；
	 * 		2、相同的Field不考虑存在多个转换逻辑，如果后续一个Field存在多个转换逻辑，需要扩展转换规则；
	 * 		3、如果需要转换的类或注解都为空，则全部转换；
	 * 		4、如果需要转换的类或注解存在非空的，则按照非空的进行转换；
	 * @param field
	 * @return
	 */
	public boolean hasConvert(Field field) {
		if(field == null) {
			return false;
		}
		//指定禁止需要转换的字段
		if(excludeFields.contains(field.getName())) {
			return false;
		}
		if(hasAnnotation(field, excludeAnnotations)) {
			return false;
		}
		//允许转换的字段和注解全部为空，则除去不允许转换的之外，其余全部允许转换
		if(convertFields.isEmpty() && convertAnnotations.isEmpty()) {
			return true;
		}
		//存在指定需要转换的字段，
		if(!convertFields.isEmpty()) {
			if(convertFields.contains(field.getName())) {
				return true;
			}
		}
		if(!convertAnnotations.isEmpty()) {
			if(hasAnnotation(field,convertAnnotations)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @description 判断字段是否需要转换
	 * @param field
	 * @param annotations
	 * @return
	 */
	public boolean hasAnnotation(Field field,Set<Class> annotations) {
    	if(annotations == null || annotations.isEmpty()) {
    		return false;
    	}
    	for (Class annotation : annotations) {
			if(field.isAnnotationPresent(annotation)) {
				return true;
			}
		}
    	return false;
    }


	// 单个对象转换程序
	public void executeDbToPage4Model(IDModel idModel) {
		if(idModel == null) {
			return;
		}
		Field[] fields = idModel.getClass().getDeclaredFields();
		for (Field field : fields) {
			if(hasConvert(field)) {
				//转换Model指定的字段
				Db2PageConvertDispatcher.executeField(idModel, field);
			}
		}
	}

	//多个对象的转换程序
	public void executeDbToPage4List(List<? extends IDModel> idModels) {
		if(idModels != null && !idModels.isEmpty()) {
			//添加不需要注解等标识
			for(IDModel idModel : idModels){
				idModel.setConvertFields(getConvertFields());
				idModel.setExcludeFields(getExcludeFields());
				idModel.setConvertAnnotations(getConvertAnnotations());
				idModel.setExcludeAnnotations(getExcludeAnnotations());
			}
			//重新书写  优化多行数据时外键字典等字段只查询一次
			Db2PageConvertDispatcher.executeListToPage(idModels);
    	}
	}

}
