package pro.wuan.feignapi.ossapi.fegin;


import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.wuan.common.core.constant.AppConstant;
import pro.wuan.common.core.model.Result;
import pro.wuan.feignapi.ossapi.entity.SysAttMainEntity;
import pro.wuan.feignapi.ossapi.fallback.SysAttMainServiceFallback;
import pro.wuan.feignapi.ossapi.model.SysAttMainVo;

import java.util.List;
import java.util.Map;


/**
 * @description:
 * @author: zf
 * @date: 2021-9-6 15:16
 */
@FeignClient(value = AppConstant.APPLICATION_OSS_NAME, fallback = SysAttMainServiceFallback.class)
public interface SysAttMainService {


    //上传附件
    @PostMapping(value = "/sysAttMain/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result upload(@RequestPart("file") MultipartFile file, @RequestParam("sysAttMain") SysAttMainEntity sysAttMain);

    //上传附件
    @PostMapping(value = "/sysAttMain/upload/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result uploadNew(@RequestPart("file") MultipartFile file, @RequestParam("moduleId") Long moduleId, @RequestParam("moduleBean") String moduleBean, @RequestParam("moduleKey") String moduleKey, @RequestParam("saveType") String saveType);


    //上传头像
    @PostMapping(value = "/sysAttMain/uploadHead", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result uploadHead(@RequestPart("file") MultipartFile file, @RequestParam("moduleId") Long moduleId);


    //单个附件下载
    @PostMapping(value = "/sysAttMain/download", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Response download(@RequestParam("id") Long id);


    //附件打包下载
    @PostMapping(value = "/sysAttMain/downloadAll", consumes = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    void downloadAll(@RequestParam("moduleId") Long moduleId);

    //附件打包下载
    @GetMapping(value = "/sysAttMain/downloadAllNew", consumes = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    void downloadAllNew(@RequestParam("moduleId") Long moduleId);


    //附件预览
    @GetMapping(value = "/sysAttMain/preview")
    void preview(@RequestParam("fileId") Long fileId, @RequestParam("fullFileName") String fullFileName);


    //分发添加附件
    @PostMapping(value = "/sysAttMain/dispatchSave",produces = { "application/json;charset=UTF-8"})
    Boolean dispatchSave(@RequestParam("moduleId") Long moduleId, @RequestBody SysAttMainVo pageParams);


    //分发跟新附件
    @PostMapping(value = "/sysAttMain/dispatchUpdate")
    Boolean dispatchUpdate(@RequestParam("moduleId") Long moduleId, @RequestBody SysAttMainVo pageParams);


    //获取封面图片路径
    @GetMapping("/sysAttMain/selectCoverUrl")
    Map<Long, String> selectCoverUrl(@RequestParam(value = "moudleIdList") List<Long> moudleIdList);


    //获取该业务对应的所有附件
    @GetMapping("/sysAttMain/getByModuleId")
    List<SysAttMainEntity> getByModuleId(@RequestParam("moduleId") Long moduleId);


    //获取该业务对应的某种类型附件
    @GetMapping("/sysAttMain/getByModuleKey")
    List<SysAttMainEntity> getByModuleKey(@RequestParam("moduleId") Long moduleId, @RequestParam("moduleKey") String moduleKey);


    //获取该业务对应的某种类型附件
    @GetMapping("/sysAttMain/getByModuleIdsAndKey")
    List<SysAttMainEntity> getByModuleIdsAndKey(@RequestParam(value = "moudleIdList") List<Long> moudleIdList, @RequestParam(value = "moduleKey") String moduleKey);

    //根据id删除附件
    @PostMapping("/sysAttMain/deleteById")
    Boolean deleteById(@RequestParam("id") Long id);

    //上传人脸识别图像
    @PostMapping(value = "/sysAttMain/uploadFaceImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result uploadFaceImage(@RequestPart("file") MultipartFile file, @RequestParam("sysAttMain") SysAttMainEntity sysAttMain);

    //根据实体属性查询实体
    @PostMapping(value = "/sysAttMain/selectByProperty")
    Result<List<SysAttMainEntity>> selectByProperty(@RequestBody SysAttMainEntity sysAttMain);

    /**
     * 往附件表中批量插入数据
     * @param
     * @return
     * @author wj
     */
    @RequestMapping(value = "/sysAttMain/addBatchSysAttMainEntity",method = RequestMethod.POST,produces = { "application/json;charset=UTF-8"})
    Result addSysAttMainEntity(@RequestBody List<SysAttMainEntity> sysAttMainEntities);


    /**
     * 往附件表中批量插入数据
     * @param
     * @return
     * @author libra
     */
    @GetMapping(value = "/sysAttMain/getOnlyByModuleKey",produces = { "application/json;charset=UTF-8"})
    List<SysAttMainEntity> getOnlyByModuleKey(@RequestParam("moduleKey") String moduleKey);

    /**
     * 通过附件ids批量获取附件
     * @param ids
     * @return
     * @author libra
     */
    @RequestMapping(value = "/sysAttMain/getFilesByIds",produces = { "application/json;charset=UTF-8"})
    List<SysAttMainEntity> getFilesByIds(@RequestParam("ids") List<Long> ids);

}
