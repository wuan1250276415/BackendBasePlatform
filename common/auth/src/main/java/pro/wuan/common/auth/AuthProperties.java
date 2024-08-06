/**
 * Copyright (c) 2018-2028, Chill Zhuang 庄骞 (smallchill@163.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pro.wuan.common.auth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限过滤
 *
 * @author Chill
 */
@Data
@RefreshScope
@ConfigurationProperties(value = "tellhow.auth")
@Component
public class AuthProperties {

    /**
     * 忽略token认证地址
     */
    private List<String> skipUrl = new ArrayList<>();
    /**
     * 忽略鉴权地址
     */
    private List<String> skipAuthUrl = new ArrayList<>();

    /**
     * 是否开启鉴权
     */
    private Boolean isEnable;
    /**
     * 是否开启授权码校验
     */
    private Boolean isLicenseEnable = false;
    /**
     * 授权文件物理路径
     */
    private String licensePath;

}
