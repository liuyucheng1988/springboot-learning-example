package org.spring.springboot.web;

import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.spring.springboot.domain.User;
import org.springframework.web.bind.annotation.*;

/**
 * Spring Boot HelloWorld 案例
 *
 * Created by bysocket on 16/4/26.
 */
@RestController
@RequestMapping("/gsc")
@Slf4j
public class HelloWorldController {

    @GetMapping("/{age}")
    public String sayHello(@RequestParam(defaultValue = "world ",name = "name",required = true) String name,
                           @PathVariable String age) {
        log.info("get Hello,"+name +"!my age="+age);
        return "Hello,"+name +"!my age="+age;
    }
    @PostMapping("/adduser")
    public String createUser(@RequestBody User user)
    {
        log.info("post adduser req="+user.toString());
        return "HTTP POST was called："+user.toString();
    }
    @DeleteMapping("/{name}")
    public String delUser(@PathVariable(name="name") String nam1)
    {
        log.info("delete ="+nam1);
        return "delete:"+nam1;
    }
    @PutMapping("/{name}")
    public String updateUser(@PathVariable String name, @RequestBody User user)
    {
        log.info("updateUser name="+name+", req="+user.toString());
        return "updateUser name="+name+", req="+user.toString();
    }
}
