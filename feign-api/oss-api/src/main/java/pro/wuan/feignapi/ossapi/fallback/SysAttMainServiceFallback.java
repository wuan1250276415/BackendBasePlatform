package pro.wuan.feignapi.ossapi.fallback;


import feign.Response;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import pro.wuan.common.core.model.Result;
import pro.wuan.feignapi.ossapi.entity.SysAttMainEntity;
import pro.wuan.feignapi.ossapi.fegin.SysAttMainService;
import pro.wuan.feignapi.ossapi.model.SysAttMainVo;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: zf
 * @date: 2021-9-9 11:16
 */
@Component
public class SysAttMainServiceFallback implements SysAttMainService {


    @Override
    public Result upload(MultipartFile file, SysAttMainEntity sysAttMain) {
        return null;
    }

    @Override
    public Result uploadNew(MultipartFile file, Long moduleId, String moduleBean, String moduleKey, String saveType) {
        return null;
    }

    @Override
    public Result uploadHead(MultipartFile file, Long moduleId) {
        return null;
    }

    @Override
    public Response download(Long id) {
        return null;
    }

    @Override
    public void downloadAll(Long moduleId) {
    }

    @Override
    public void downloadAllNew(Long moduleId) {
    }

    @Override
    public void preview(Long fileId, String fullFileName) {

    }

    @Override
    public Boolean dispatchSave(Long dateId, SysAttMainVo pageParams) {
        return false;
    }

    @Override
    public Boolean dispatchUpdate(Long dateId,SysAttMainVo pageParams) {
        return false;
    }

    @Override
    public Map<Long, String> selectCoverUrl(List<Long> moudleIdList) {
        return null;
    }

    @Override
    public List<SysAttMainEntity> getByModuleId(Long moduleId) {
        return null;
    }

    @Override
    public List<SysAttMainEntity> getByModuleKey(Long moduleId, String moduleKey) {
        return null;
    }

    @Override
    public Boolean deleteById(Long id) {
        return null;
    }

    //获取该业务对应的某种类型附件
    @Override
    public List<SysAttMainEntity> getByModuleIdsAndKey(List<Long> moudleIdList, String moduleKey) {
        return null;
    }

    @Override
    public Result uploadFaceImage(@RequestPart("file") MultipartFile file, @RequestParam("sysAttMain") SysAttMainEntity sysAttMain) {
        return null;
    }

    @Override
    public Result<List<SysAttMainEntity>> selectByProperty(SysAttMainEntity sysAttMain) {
        return null;
    }

    /**
     * 往附件表中批量插入数据
     * @param
     * @return
     * @author wj
     */
    @Override
    public Result addSysAttMainEntity(@RequestBody List<SysAttMainEntity> sysAttMainEntities){
        return Result.failure("feign调用失败！");

    }
    /**
     * 往附件表中批量插入数据
     * @param
     * @return
     * @author libra
     */
    @Override
    public  List<SysAttMainEntity> getOnlyByModuleKey(@RequestParam("moduleKey") String moduleKey){
        return Lists.newArrayList();

    }


    /**
     * 通过附件ids批量获取附件
     * @param
     * @return
     * @author libra
     */
    @Override
    public List<SysAttMainEntity> getFilesByIds(@RequestParam List<Long> ids){
        return Lists.newArrayList();
    }

}
