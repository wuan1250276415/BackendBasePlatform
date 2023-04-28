package pro.wuan.core.controller.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pro.wuan.common.redis.util.JedisUtil;

@RestController
@RequestMapping("/api/redis")
public class RedisController {

    @Autowired
    private JedisUtil redisService;


    @PostMapping("/set/{key}/{value}")
    public void set(@PathVariable String key, @PathVariable String value) {
        redisService.set(key, value);
    }

    @GetMapping("/get/{key}")
    public String get(@PathVariable String key) {
        return redisService.get(key).toString();
    }

    @DeleteMapping("/delete/{key}")
    public void delete(@PathVariable String key) {
        redisService.delete(key);
    }

    @PutMapping("/update/{key}/{newValue}")
    public void update(@PathVariable String key, @PathVariable String newValue) {
        redisService.set(key, newValue);
    }
}
