package pro.wuan.user.service;


import pro.wuan.common.core.service.IBaseService;
import pro.wuan.user.entity.SysCaptchaEntity;
import pro.wuan.user.mapper.SysCaptchaMapper;

import java.awt.image.BufferedImage;

public interface ISysCaptchaService extends IBaseService<SysCaptchaMapper, SysCaptchaEntity> {

    /**
     * 获取图片验证码
     */
    BufferedImage getCaptcha(String uuid);


    /**
     * 验证码效验
     * @param uuid  uuid
     * @param code  验证码
     * @return  true：成功  false：失败
     */
    boolean validate(String uuid, String code);
}
