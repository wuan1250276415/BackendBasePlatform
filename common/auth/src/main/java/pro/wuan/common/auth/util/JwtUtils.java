package pro.wuan.common.auth.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.wuan.common.auth.JwtProperties;
import pro.wuan.common.core.constant.SecurityConstants;
import pro.wuan.common.core.exception.JwtException;
import pro.wuan.common.redis.util.JedisUtil;
import pro.wuan.common.redis.util.RedisLockUtil;

import java.util.Date;

/**
 * @description: jwt共工具类，生成token、校验token
 * @author: oldone
 * @date: 2020/7/31 16:29
 */
@Component
public class JwtUtils {
    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    @Autowired
    JwtProperties jwtProperties;
    @Autowired
    JedisUtil jedisUtil;

    private static JwtUtils jwtUtils;

    @PostConstruct
    public void init() {
        jwtUtils = this;
        jwtUtils.jwtProperties = this.jwtProperties;
        jwtUtils.jedisUtil = this.jedisUtil;
    }

    /**
     * 校验token是否正确
     *
     * @param token
     * @return
     */
    public static boolean verify(String token) {
        String secret = getClaim(token, SecurityConstants.ACCOUNT) + jwtUtils.jwtProperties.getSecretKey();
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm)
                .build();
        verifier.verify(token);
        return true;
    }

    /**
     * 获得Token中的信息无需secret解密也能获得
     *
     * @param token
     * @param claim
     * @return
     */
    public static String getClaim(String token, String claim) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(claim).asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 生成签名,n分钟后过期
     *
     * @param account
     * @param currentTimeMillis
     * @return
     */
    public static String sign(String account, String currentTimeMillis) {

        // 帐号加JWT私钥加密
        String secret = account + jwtUtils.jwtProperties.getSecretKey();
        // 此处过期时间，单位：毫秒
        //token过期时间
        Long expire = jwtUtils.jwtProperties.getTokenExpireTime() * 60 * 1000L + jwtUtils.jwtProperties.getRefreshCheckTime() * 60 * 1000L;
        Date date = new Date(System.currentTimeMillis() + expire);
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withClaim(SecurityConstants.ACCOUNT, account)
                .withClaim(SecurityConstants.CURRENT_TIME_MILLIS, currentTimeMillis)
                .withExpiresAt(date)
                .sign(algorithm);
    }

    /**
     * 生成token
     *
     * @param account
     * @param millTimes
     * @return
     */
    public static String genToken(String account, long millTimes) {
        log.info(String.format("为账户%s颁发新的令牌", account));

        String currentTimeMillis = String.valueOf(millTimes);
        String token = JwtUtils.sign(account, String.valueOf(millTimes));

        //更新RefreshToken缓存的时间戳
        String refreshTokenKey = SecurityConstants.PREFIX_REFRESH_TOKEN + account;
        jwtUtils.jedisUtil.set(refreshTokenKey, currentTimeMillis, jwtUtils.jwtProperties.getTokenExpireTime() * 60);
        return token;
    }

    /**
     * 检查token是否合法
     *
     * @param token
     * @return
     */
    public static boolean checkToken(String token) throws JwtException {
        String account = JwtUtils.getClaim(token, SecurityConstants.ACCOUNT);
        //判断token中是否含有账户信息
        if (null == account) {
            throw new JwtException("无效令牌");
        }
        //校验token合法性
        if (!JwtUtils.verify(token)) {
            //token校验失败
            throw new JwtException("令牌已过期或无效，请重新登录");
        }

        String refreshTokenKey = SecurityConstants.PREFIX_REFRESH_TOKEN + account;
        //判断是否需要踢人功能
        if (jwtUtils.jwtProperties.getIsTick() != null && !jwtUtils.jwtProperties.getIsTick()) {
            return true;
        }
        //判断刷新token key是否存在
        if (jwtUtils.jedisUtil.exists(refreshTokenKey)) {
            //存在
            //获取redis中的时间戳
            log.info("tokenTimeStamp:" + String.valueOf(jwtUtils.jedisUtil.get(refreshTokenKey)));
            Long tokenTimeStamp = Long.valueOf(String.valueOf(jwtUtils.jedisUtil.get(refreshTokenKey)));
            //获取token中的
            log.info("tokenMillis:" + JwtUtils.getClaim(token, SecurityConstants.CURRENT_TIME_MILLIS));
            Long tokenMillis = Long.valueOf(JwtUtils.getClaim(token, SecurityConstants.CURRENT_TIME_MILLIS));
            //判断redis时间戳与token中的时间戳是否相等且误差是否在冗余时间内
            if (!tokenTimeStamp.equals(tokenMillis) && (tokenTimeStamp - tokenMillis > jwtUtils.jwtProperties.getRedundancyTime())) {
                throw new JwtException("令牌已过期，请重新登录");
            }
        } else {
            log.error("redis token有效时间key:" + refreshTokenKey, "，对应值：" + jwtUtils.jedisUtil.get(refreshTokenKey));
            throw new JwtException("缓存中未获取到token有效时间");
        }
        return true;
    }


    /**
     * 检查是否需要刷新token，需要则刷新
     *
     * @param token
     */
    public static String refreshTokenIfNeed(String token) {
        //系统当前时间
        Long currentTimeMillis = System.currentTimeMillis();
        //获取token的生成时间
        String tokenMillis = JwtUtils.getClaim(token, SecurityConstants.CURRENT_TIME_MILLIS);
        //获取token中的用户账号
        String account = JwtUtils.getClaim(token, SecurityConstants.ACCOUNT);
        //锁名
        String lockName = SecurityConstants.PREFIX_REFRESH_CHECK + account;
        //根据当前时间-token生成时间是否大于设定的刷新间隔时间判断是否需要刷新token
        if (currentTimeMillis - Long.parseLong(tokenMillis) > (jwtUtils.jwtProperties.getRefreshCheckTime() * 60 * 1000L)) {
            //需要刷新token
            //是否获取到锁
            boolean hasLock = false;
            try {
                log.info(String.format("用户：%s,当前线程：%s,准备获取分布式锁...", account, Thread.currentThread().getName()));
                //获取锁--尝试获取锁等待时间设置为0，即没获取到锁就不在阻塞等待
                hasLock = RedisLockUtil.tryLock(lockName, 0l);
                if (!hasLock) {
                    //没有获取锁直接退出
                    log.info(String.format("用户：%s,当前线程：%s,没有获取到锁直接退出...", account, Thread.currentThread().getName()));
                    return Strings.EMPTY;
                }

                log.info(String.format("用户：%s,当前线程：%s,成功获取到分布式锁...", account, Thread.currentThread().getName()));
                //生成新的token
                String newToken = JwtUtils.genToken(account, currentTimeMillis);
                return newToken;
            } catch (Exception e) {
                log.error(String.format("用户：%s,当前线程：%s,token刷新异常：", account, Thread.currentThread().getName()), e);
            } finally {
                //判断是否获取锁
                if (!hasLock) {
                    //没有获取锁直接退出
                    log.info(String.format("用户：%s,当前线程：%s,没有获取到锁直接退出...", account, Thread.currentThread().getName()));
                    return Strings.EMPTY;
                }

                try {
                    log.info(String.format("用户：%s,当前线程：%s,准备释放分布式锁...", account, Thread.currentThread().getName()));
                    //释放锁
                    RedisLockUtil.unlock(lockName);
                } catch (Exception ex) {
                    log.error(String.format("用户：%s,当前线程：%s,释放锁异常：", account, Thread.currentThread().getName()), ex);
                }
            }
        }

        return Strings.EMPTY;
    }

    /**
     * 设置token无效
     *
     * @param account
     */
    public static void setTokenInvalid(String account) {
        String refreshTokenKey = SecurityConstants.PREFIX_REFRESH_TOKEN + account;
        jwtUtils.jedisUtil.delete(refreshTokenKey);
    }

}
