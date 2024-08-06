package pro.wuan.common.cloud.interceptor;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Response;

import java.io.IOException;

/**
 * @description:
 * @author: oldone
 * @date: 2021/8/18 17:04
 */
@Slf4j
public class OkHttpLogInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        log.info("OkHttpUrl : {}", chain.request().url());
        return chain.proceed(chain.request());
    }
}
