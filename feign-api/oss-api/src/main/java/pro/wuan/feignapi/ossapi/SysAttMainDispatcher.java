package pro.wuan.feignapi.ossapi;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.db.service.IDispatcherService;
import pro.wuan.feignapi.ossapi.fegin.SysAttMainService;
import pro.wuan.feignapi.ossapi.model.ISysAttMainVo;
import pro.wuan.feignapi.ossapi.model.SysAttMainVo;

@Service
public class SysAttMainDispatcher implements IDispatcherService {



	@Autowired
	private SysAttMainService sysAttMainService;


	//附件分发添加
	@Override
	public void dispatcherSave(IDModel idModel) throws RuntimeException {
		if(idModel instanceof ISysAttMainVo){
			ISysAttMainVo sysAttMainVo = (ISysAttMainVo) idModel;
			// 执行附件逻辑的分发操作
			SysAttMainVo pageParams = sysAttMainVo.getSysAttMainVo();
			// 页面上传递过来的数据
			if(pageParams != null ){
				sysAttMainService.dispatchSave(idModel.getId(), pageParams);
			}
		}
	}

	@Override
	public void dispatcherUpdate(IDModel idModel) throws RuntimeException {
		if(idModel instanceof ISysAttMainVo){
			ISysAttMainVo sysAttMainVo = (ISysAttMainVo) idModel;
			// 执行附件逻辑的分发操作
			SysAttMainVo pageParams = sysAttMainVo.getSysAttMainVo();
			// 页面上传递过来的数据
			if(pageParams != null ){
				sysAttMainService.dispatchUpdate(idModel.getId(), pageParams);
			}
		}
	}




}
