package pro.wuan.common.db.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ignore
 */
@RequestMapping("/core")
@Slf4j
@Controller
public class CoreTestController {

    /**
     * @param name 测试
     * @return
     */
    @GetMapping("/sayHello/{name}")
    public String sayHello(@PathVariable("name") String name){
        System.out.print("I'm tellhowCore by dynamic gateway...");
        return "indexcore";
    }

}
