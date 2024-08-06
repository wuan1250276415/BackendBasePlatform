package pro.wuan.common.redis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisConfig {
    private int database;
    private String host;
    private int port;
    private String password;
    private int timeout;

    /**
     * @return the database
     */
    public int getDatabase() {
        return database;
    }

    /**
     * @param database
     *            the database to set
     */
    public void setDatabase(int database) {
        this.database = database;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host
     *            the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port
     *            the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    /**
     * @return the timeout
     */
    public int getTimeout() {
        return timeout;
    }
    /**
     * @param timeout
     *            the timeout to set
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
