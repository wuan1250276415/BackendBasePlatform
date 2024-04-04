/**
 * This class provides the controller for Redis operations.
 * It uses Spring Boot's @RestController annotation to indicate that it's a RESTful controller.
 * It uses Spring Boot's @RequestMapping annotation to map the request path.
 */
package pro.wuan.core.controller.redis;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.wuan.common.redis.util.JedisUtil;

@RestController
@RequestMapping("/api/redis")
public class RedisController {

    /**
     * An instance of JedisUtil to handle Redis operations.
     */
    private final JedisUtil redisService;

    /**
     * Constructor for RedisController.
     *
     * @param redisService the JedisUtil instance
     */
    public RedisController(JedisUtil redisService) {
        this.redisService = redisService;
    }

    /**
     * This method sets a value in Redis.
     * It uses Spring Boot's @PostMapping annotation to handle POST requests.
     *
     * @param key the key
     * @param value the value
     */
    @PostMapping("/set/{key}/{value}")
    public ResponseEntity<String> set(@PathVariable String key, @PathVariable String value) {
        return redisService.set(key, value) ? ResponseEntity.ok("成功") : ResponseEntity.badRequest().build();
    }

    /**
     * This method gets a value from Redis.
     * It uses Spring Boot's @GetMapping annotation to handle GET requests.
     *
     * @param key the key
     * @return the value
     */
    @GetMapping("/get/{key}")
    public ResponseEntity<String> get(@PathVariable String key) {
        return ResponseEntity.ok(redisService.get(key).toString());
    }

    /**
     * This method deletes a value in Redis.
     * It uses Spring Boot's @DeleteMapping annotation to handle DELETE requests.
     *
     * @param key the key
     */
    @DeleteMapping("/delete/{key}")
    public ResponseEntity<String> delete(@PathVariable String key) {
        redisService.delete(key);
        return ResponseEntity.ok("成功");
    }

    /**
     * This method updates a value in Redis.
     * It uses Spring Boot's @PutMapping annotation to handle PUT requests.
     *
     * @param key the key
     * @param newValue the new value
     */
    @PutMapping("/update/{key}/{newValue}")
    public ResponseEntity<String> update(@PathVariable String key, @PathVariable String newValue) {
        return redisService.set(key, newValue) ? ResponseEntity.ok("成功") : ResponseEntity.badRequest().build();
    }
}