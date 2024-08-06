package pro.wuan.common.db.service;


import pro.wuan.common.core.model.IDModel;

/**
 * 标记作用
 * @author ivan
 *
 */
public interface IDispatcherService {
	
	
	/**
	 * 执行分发新增操作
	 * @param idModel
	 */
	public void dispatcherSave(IDModel idModel) throws RuntimeException;
	
	
	/**
	 * 执行分发更新操作
	 * @param idModel
	 */
	public void dispatcherUpdate(IDModel idModel) throws RuntimeException;
	
}
