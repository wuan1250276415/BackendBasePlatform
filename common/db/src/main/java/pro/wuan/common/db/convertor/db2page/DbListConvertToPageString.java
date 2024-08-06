package pro.wuan.common.db.convertor.db2page;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pro.wuan.common.core.annotation.ConvertMapping;
import pro.wuan.common.core.annotation.ListToString;
import pro.wuan.common.core.annotation.ServiceType;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.core.service.IBaseService;
import pro.wuan.common.core.utils.ReflectionUtils;
import pro.wuan.common.db.service.IDbConvertToPageService;
import pro.wuan.common.db.util.TableUtil;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description 将db中的list转换到页面中的string
 *     适用场景：后台保存的是一对多的关系，比如人员多选，前端展示人员的拼接字符串
 * @author ivan
 *
 */
@Slf4j
@Service
public class DbListConvertToPageString implements IDbConvertToPageService {

	@Override
	public void execute(IDModel idModel) {
		if(idModel == null) {
			return;
		}
		List<Field> fields = ReflectionUtils.getDeclareFieldByAnnotation(idModel.getClass(), ListToString.class);
        for (Field field : fields) {
        	this.executeField(idModel, field);
        }
	}

	@Override
	public void executeField(IDModel idModel, Field field) {
		if(idModel == null || field == null) {
			return;
		}
		if(field.isAnnotationPresent(ListToString.class)) {
			log.debug("开始模块{}的{}的属性转换，将DB-List转换成Page-String",idModel.getClass().getName(),field.getName());
	        ListToString ListToString = field.getAnnotation(ListToString.class);
	        String fk = ListToString.fk();
	        Class classes = ListToString.fkType();
	        ServiceType fkServiceType = ListToString.fkType().getAnnotation(ServiceType.class);
	        if(fkServiceType != null) {
	        	//step2 获取明细表清单
	        	IBaseService fkCoreService = getService(ListToString.fkType());
				QueryWrapper queryWrapper = Wrappers.query();
				queryWrapper.eq(TableUtil.property2column(fk, classes), idModel.getId());
	        	List<? extends IDModel> rtnList = fkCoreService.selectList(queryWrapper);
	        	//step3 获取需要进项转换的字段
	        	ConvertMapping[] convertMappings = ListToString.mappings();
	            Map<String,String> cm = new HashMap<>();
	            for (ConvertMapping convertMapping : convertMappings) {
	                cm.put(convertMapping.sourceProperty(),convertMapping.targetProperty());
	            }
	            JSONObject values = new JSONObject();
	            for (IDModel eachModel : rtnList) {
	                Long subFkValue = (Long)ReflectionUtils.getFieldValue(eachModel,ListToString.subFk());
	                ServiceType subServiceType = ListToString.subType().getAnnotation(ServiceType.class);
	                if(subServiceType != null) {
	                	IBaseService subCoreService = getService(subServiceType);
	                	IDModel subModel = subCoreService.selectById(subFkValue);
	                	if(subModel != null) {
	                		for (String key : cm.keySet()) {
	                            //map.keySet()返回的是所有key的值
	                            String jsonValue = values.getString(cm.get(key));

	                            //获取本次的数据值
								String eachValue = "";
								if("showText".equals(key)){
									try{
										PropertyDescriptor pd = new PropertyDescriptor(key, subModel.getClass());
										Method md = pd.getReadMethod();
										eachValue = (String) md.invoke(subModel);
									}catch (Exception e){
										log.error("类{}获取展示字段showText的过程中出现异常，情况如下：{}",subModel.getClass().getName(),e.getMessage());
									}
								}else{
									eachValue = String.valueOf(ReflectionUtils.getFieldValue(subModel,key));
								}
	                            if(!StringUtils.isEmpty(jsonValue)){
	                                //jsonValue += jsonValue + ";" + String.valueOf(ReflectionUtils.getFieldValue(subModel,key));
	                            	jsonValue = jsonValue + ";" + eachValue;
	                            }else{
	                            	jsonValue = eachValue;

	                            }
	                            values.put(cm.get(key),jsonValue);
	                        }
	                	}
	                }else {
	                	log.error("类{}未定义注解@ServiceType,无法进行ListPage转换工作！",ListToString.subType().getName());
	                }
	            }
	            for(String targetField : values.keySet()){
	                ReflectionUtils.setFieldValue(idModel,targetField,values.getString(targetField));
	            }
	        }else {
	        	log.error("模块{}.{}的属性转换异常，模块{}未定义@ServiceType注解",idModel.getClass().getName(),field.getName(),ListToString.fkType());
	        }
		}
	}

	@Override
	public void executeListToPage(List<? extends IDModel> idModels) {
		// 获取第1个Entity 作为class
		IDModel idModel = idModels.get(0);
		List<Field> fields = ReflectionUtils.getDeclareFieldByAnnotation(idModel.getClass(), ListToString.class);

		for (Field field : fields) {
			if(!idModel.hasConvert(field)) {
				continue;
			}
			List<Object> ids = new ArrayList<>();
			for (IDModel model : idModels) {
				ids.add(model.getId());
			}

			if (field.isAnnotationPresent(ListToString.class)) {
				log.debug("开始模块{}的{}的属性转换，将DB-List转换成Page-String", idModel.getClass().getName(), field.getName());
				ListToString ListToString = field.getAnnotation(ListToString.class);
				String fk = ListToString.fk();
				Class classes = ListToString.fkType();
				// step3 获取需要进项转换的字段
				ConvertMapping[] convertMappings = ListToString.mappings();
				Map<String, String> cm = new HashMap<>();
				for (ConvertMapping convertMapping : convertMappings) {
					cm.put(convertMapping.sourceProperty(), convertMapping.targetProperty());
				}

				ServiceType fkServiceType = ListToString.fkType().getAnnotation(ServiceType.class);
				if (fkServiceType != null) {
					// step2 获取明细表清单
					IBaseService fkCoreService = getService(fkServiceType);

					QueryWrapper queryWrapper = Wrappers.query();
					queryWrapper.in(TableUtil.property2column(fk, classes), ids);
					List<? extends IDModel> rtnList = fkCoreService.selectList(queryWrapper);
					if (rtnList != null && !rtnList.isEmpty()) {
						List<Object> subFkIds = new ArrayList<>();
						for (IDModel eachModel : rtnList) {
							Long subFkValue = (Long) ReflectionUtils.getFieldValue(eachModel, ListToString.subFk());
							subFkIds.add(subFkValue);
						}
						ServiceType subServiceType = ListToString.subType().getAnnotation(ServiceType.class);
						if (subServiceType != null) {

							QueryWrapper subWrapper = Wrappers.query();
							subWrapper.in("id", subFkIds);
							IBaseService subCoreService = getService(subServiceType);

							List<? extends IDModel> subModelList = subCoreService.selectList(subWrapper);

							if (subModelList != null && !subModelList.isEmpty()) {

								Map<String, JSONObject> map = new HashMap<>();
								for (IDModel models : rtnList) {
									JSONObject values = new JSONObject();
									Object modelsFK = ReflectionUtils.getFieldValue(models, fk);
									Object subFK = ReflectionUtils.getFieldValue(models, ListToString.subFk());
									for (IDModel subModel : subModelList) {
										Object subModelFK = ReflectionUtils.getFieldValue(subModel, "id");
										if (null != subFK && Long.compare((Long)subFK,(Long)subModelFK) == 0) {
											for (String key : cm.keySet()) {
												// map.keySet()返回的是所有key的值
												String jsonValue = map.get(modelsFK + "") == null ? "" : map.get(modelsFK +"").getString(cm.get(key));

												// 获取本次的数据值
												String eachValue = "";
												if ("showText".equals(key)) {
													try {
														PropertyDescriptor pd = new PropertyDescriptor(key,
																subModel.getClass());
														Method md = pd.getReadMethod();
														eachValue = (String) md.invoke(subModel);
													} catch (Exception e) {
														log.error("类{}获取展示字段showText的过程中出现异常，情况如下：{}",
																subModel.getClass().getName(), e.getMessage());
													}
												} else {
													eachValue = String
															.valueOf(ReflectionUtils.getFieldValue(subModel, key));
												}
												if (!StringUtils.isEmpty(jsonValue)) {
													// jsonValue += jsonValue + ";" +
													// String.valueOf(ReflectionUtils.getFieldValue(subModel,key));
													jsonValue = jsonValue + ";" + eachValue;
												} else {
													jsonValue = eachValue;

												}
												values.put(cm.get(key), jsonValue);
											}

											map.put(modelsFK + "", values);
										}
									}
								}

								for (IDModel model : idModels) {
									for (String targetField : map.keySet()) {
										if (targetField.equals((model.getId() + ""))) {
											JSONObject values = map.get(targetField);
											for (String targetFields : values.keySet()) {
												ReflectionUtils.setFieldValue(model, targetFields,
														values.getString(targetFields));
											}
										}

									}
								}

							}
						}
					}
				} else {
					log.error("模块{}.{}的属性转换异常，模块{}未定义@ServiceType注解", idModel.getClass().getName(), field.getName(),
							ListToString.fkType());
				}
			}
		}
	}

}
