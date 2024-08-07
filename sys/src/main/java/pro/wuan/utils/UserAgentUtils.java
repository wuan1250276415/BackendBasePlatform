package pro.wuan.utils;

import com.alibaba.nacos.common.utils.IPUtil;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Slf4j
public class UserAgentUtils {

    /**
     *获取浏览器名称及版本
     * */
    public static String browserName(HttpServletRequest request){
        String userAgent = request.getHeader("User-Agent");
        UserAgent ua = UserAgent.parseUserAgentString(userAgent);
        Browser browser = ua.getBrowser();
        return browser.getName() + "-" + browser.getVersion(userAgent);
    }

    /**
     * 获取操作系统名称
     * */
    public static String osName(HttpServletRequest request){
        String userAgent = request.getHeader("User-Agent");
        UserAgent ua = UserAgent.parseUserAgentString(userAgent);
        OperatingSystem os = ua.getOperatingSystem();
        return os.getName();
    }

    /**
     * 获取IP地址
     * */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_FORWARDED");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_VIA");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("REMOTE_ADDR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip.contains(",")) {
            return ip.split(",")[0];
        } else {
            return ip;
        }
    }

    /**
     * 根据IP地址获取城市
     * @param ip
     * @return
     */
    public static String getCityInfo(String ip)  {

        URL url = IPUtil.class.getClassLoader().getResource("ip2region.xdb");
        String dbPath = null;
        if (url != null) {
            dbPath = url.getFile();
        }
        String region = null;
        // 1、从 dbPath 中预先加载 VectorIndex 缓存，并且把这个得到的数据作为全局变量，后续反复使用。
        byte[] vIndex;
        try {
            vIndex = Searcher.loadVectorIndexFromFile(dbPath);
        } catch (Exception e) {
            log.error("failed to load vector index from `{}`: {}\n", dbPath, e.getMessage());
            return null;
        }

        // 2、使用全局的 vIndex 创建带 VectorIndex 缓存的查询对象。
        Searcher searcher;
        try {
            searcher = Searcher.newWithVectorIndex(dbPath, vIndex);
        } catch (Exception e) {
            log.error("failed to create vectorIndex cached searcher with `{}`: {}\n", dbPath, e.getMessage());
            return null;
        }

        // 3、查询
        try {
            long sTime = System.nanoTime();
            region = searcher.search(ip);
            long cost = TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - sTime);
            log.info("region: {}, ioCount: {}, took: {} μs\n", region, searcher.getIOCount(), cost);
        } catch (Exception e) {
//            System.out.printf("failed to search(%s): %s\n", ip, e);
        }

        // 4、关闭资源
        try {
            searcher.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return region;
    }


}
