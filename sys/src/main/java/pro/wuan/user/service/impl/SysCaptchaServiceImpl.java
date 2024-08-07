package pro.wuan.user.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.wuan.common.core.utils.DateUtil;
import pro.wuan.common.db.service.impl.BaseServiceImpl;
import pro.wuan.user.entity.SysCaptchaEntity;
import pro.wuan.user.mapper.SysCaptchaMapper;
import pro.wuan.user.service.ISysCaptchaService;

import java.awt.image.BufferedImage;
import java.util.Date;

@Service
public class SysCaptchaServiceImpl extends BaseServiceImpl<SysCaptchaMapper, SysCaptchaEntity> implements ISysCaptchaService {

    @Autowired
    private Producer  producer;

    static{
        System.setProperty("java.awt.headless", "true");
    }

    //生成验证码
    @Override
    public BufferedImage getCaptcha(String uuid) {
        //生成文字验证码
        String code = producer.createText();
        SysCaptchaEntity captchaEntity = new SysCaptchaEntity();
        captchaEntity.setUuid(uuid);
        captchaEntity.setCode(code);
        //5分钟后过期,当前时间加5分钟
        captchaEntity.setExpireTime(DateUtil.addDateMinutes(new Date(), 5));
        //保存到数据库,用户后续校验
        this.insert(captchaEntity);
        return producer.createImage(code);
    }


    //校验验证码根据生成时的uuid
    @Override
    public boolean validate(String uuid, String code) {
        // TODO 当测试结束删除此处代码
        if("0000".equalsIgnoreCase(code)){
            return true;
        }

        SysCaptchaEntity captchaEntity = this.selectOne(new QueryWrapper<SysCaptchaEntity>().eq("uuid", uuid));
        if(captchaEntity == null){
            return false;
        }
        //删除验证码
        //this.deleteById(captchaEntity.getId());
        //验证码是否正确,是否过期
        if(captchaEntity.getCode().equalsIgnoreCase(code) && captchaEntity.getExpireTime().getTime() >= System.currentTimeMillis()){
            return true;
        }
        return false;
    }







}
