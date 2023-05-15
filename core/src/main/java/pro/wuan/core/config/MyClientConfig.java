package pro.wuan.core.config;

import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.config.EnableElasticsearchAuditing;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.elasticsearch.support.HttpHeaders;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@EnableElasticsearchRepositories
@EnableElasticsearchAuditing
public class MyClientConfig extends ElasticsearchConfiguration {

    @SneakyThrows
    @Override
    public ClientConfiguration clientConfiguration() {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        return ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                .usingSsl(sslContext)
//                .withProxy("localhost:8888")
//                .withPathPrefix("ela")
//                .withConnectTimeout(Duration.ofSeconds(5))
//                .withSocketTimeout(Duration.ofSeconds(3))
//                .withDefaultHeaders(defaultHeaders)
                .withBasicAuth("wuan", "wuanfuck321.")
                .withHeaders(() -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("currentTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    return headers;
                })
                .build();
    }
}
