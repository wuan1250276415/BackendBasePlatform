package pro.wuan.common.db.service.impl;

import org.springframework.stereotype.Service;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.common.core.utils.SpringUtil;
import pro.wuan.common.db.service.IDispatcherService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class DispatcherService {

    //执行新增分发操作
    public void dispatcherSave(IDModel idModel) {
        List<IDispatcherService> dispatcherServices = getDispatcher();
        if (dispatcherServices != null && !dispatcherServices.isEmpty()) {
            for (IDispatcherService dispatcherService : dispatcherServices) {
                dispatcherService.dispatcherSave(idModel);
            }
        }
    }

    //执行更新分发操作
    public void dispatcherUpdate(IDModel idModel) {
        List<IDispatcherService> rtnList = getDispatcher();
        if (rtnList != null && !rtnList.isEmpty()) {
            try {
                for (IDispatcherService iDispatcherService : rtnList) {
                    iDispatcherService.dispatcherUpdate(idModel);
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    /**
     * 获取所有的辅助业务逻辑处理器
     *
     * @return
     */
    public List<IDispatcherService> getDispatcher() {
        Map<String, IDispatcherService> map = SpringUtil.getBeansOfType(IDispatcherService.class);
        if (map != null && !map.isEmpty()) {
            List<IDispatcherService> rtnList = new ArrayList<>();
            for (IDispatcherService service : map.values()) {
                rtnList.add(service);
            }
            return rtnList;
        }
        return Collections.EMPTY_LIST;
    }
}
