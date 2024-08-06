//package pro.wuan.gateway.config;//package com.tellhow.gateway.config;
//
//import com.alibaba.cloud.nacos.NacosConfigProperties;
//import com.alibaba.nacos.api.NacosFactory;
//import com.alibaba.nacos.api.PropertyKeyConst;
//import com.alibaba.nacos.api.config.ConfigService;
//import com.alibaba.nacos.api.config.listener.Listener;
//import com.alibaba.nacos.api.exception.NacosException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//import java.util.concurrent.Executor;
//import java.util.concurrent.LinkedBlockingDeque;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
//@Slf4j
//@Component
//public class NacosConfigLocalCatch implements InitializingBean {
//    private final Map<String, Object> localCatchMap = new HashMap<>();
//
//    private static final String SCG_DATA_ID = "license";
//    private static final String SCG_GROUP_ID = "tellhowcloud";
//    @Autowired
//    private NacosConfigProperties nacosConfigProperties;
//
//    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
//            2, 4, 1, TimeUnit.SECONDS, new LinkedBlockingDeque<>(100),
//            new ThreadPoolExecutor.CallerRunsPolicy()
//    );
//
//    protected final String clazzSimpleName = getClass().getSimpleName();
//
//
//    /**
//     * bean初始化 触发 执行
//     *
//     * @throws Exception
//     */
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        NacosConfigInfo nacosConfigInfo = new NacosConfigInfo(nacosConfigProperties.getServerAddr(),
//                nacosConfigProperties.getNamespace(), SCG_GROUP_ID,
//                SCG_DATA_ID, true, String.class);
//
//        this.listener(nacosConfigInfo);
//    }
//
//    public void listener(NacosConfigInfo nacosConfigInfo) {
//        Listener listener = new Listener() {
//            /**
//             * 监听-工作线程池
//             * @return
//             */
//            @Override
//            public Executor getExecutor() {
//                return threadPoolExecutor;
//            }
//
//            /**
//             * 接受到Nacos 具体 配置信息变动
//             *
//             * @param configInfo
//             */
//            @Override
//            public void receiveConfigInfo(String configInfo) {
//                log.info("{}#receiveConfigInfo receive configInfo. configInfo={}", clazzSimpleName, configInfo);
//                compile(configInfo, nacosConfigInfo);
//            }
//        };
//        ConfigService configService = this.getConfigService(nacosConfigInfo);
//        String config = null;
//        try {
//            config = configService.getConfig(nacosConfigInfo.getDataId(), nacosConfigInfo.getGroup(), nacosConfigInfo.getTimeout());
//            log.info("{}#afterPropertiesSet init configInfo. configInfo={}", clazzSimpleName, config);
//            // 初始化
//            compile(config, nacosConfigInfo);
//            // 监听
//            configService.addListener(nacosConfigInfo.getDataId(), nacosConfigInfo.getGroup(), listener);
//        } catch (NacosException e) {
//            log.error(e.getErrMsg());
//            throw new RuntimeException("nacos server 监听 异常! dataId = " + nacosConfigInfo.getDataId());
//        }
//    }
//
//    private void compile(String config, NacosConfigInfo nacosConfigInfo) {
//        localCatchMap.put(nacosConfigInfo.getDataId(), config);
//    }
//
//    /**
//     * 获取ConfigService
//     *
//     * @return
//     */
//    private ConfigService getConfigService(NacosConfigInfo nacosConfigInfo) {
//        String serverAddr = nacosConfigInfo.getServerAddr();
//        String nameSpace = nacosConfigInfo.getNamespace();
//        Properties properties = new Properties();
//        properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
//        // 设置为public后会控制台会循环打印
////        properties.put(PropertyKeyConst.NAMESPACE, nameSpace);
//        ConfigService configService;
//        try {
//            configService = NacosFactory.createConfigService(properties);
//        } catch (NacosException e) {
//            e.printStackTrace();
//            throw new RuntimeException("Nacos config 配置 异常");
//        }
//        return configService;
//    }
//
//    public String getLicense() {
//        return this.get(SCG_DATA_ID, String.class);
//    }
//
//    public <T> T get(String dataId, Class<T> cls) {
//        return (T) localCatchMap.get(dataId);
//    }
//
//
//}
