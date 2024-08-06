package pro.wuan.common.db.convertor.db2page;

import org.springframework.stereotype.Component;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.core.utils.SpringBeanUtil;
import pro.wuan.common.db.service.IDbConvertToPageService;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author chenzhong
 */
@Component
public class Db2PageConvertDispatcher {

	//执行转换函数
	public static void execute(IDModel idModel) {
		Map<String, IDbConvertToPageService> map = SpringBeanUtil.getApplicationContext().getBeansOfType(IDbConvertToPageService.class);
		Set<String> keys = map.keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()) {
			String key = it.next();
			IDbConvertToPageService service = map.get(key);
			service.execute(idModel);
		}
	}
	
	//执行转换特定的属性
	public static void executeField(IDModel idModel,Field field) {
		Map<String, IDbConvertToPageService> map = SpringBeanUtil.getApplicationContext().getBeansOfType(IDbConvertToPageService.class);
		Set<String> keys = map.keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()) {
			String key = it.next();
			IDbConvertToPageService service = map.get(key);
			service.executeField(idModel,field);
		}
	}
	
	//执行转换指定的List对象
	public static void executeListToPage(List<? extends IDModel> idModels) {
		Map<String,IDbConvertToPageService> map = SpringBeanUtil.getApplicationContext().getBeansOfType(IDbConvertToPageService.class);
		Set<String> keys = map.keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()) {
			String key = it.next();
			IDbConvertToPageService service = map.get(key);
			service.executeListToPage(idModels);
		}
	}
}
