package com.example.firstservice;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/first-service/")
@Slf4j
public class FirstServiceController {

    Environment env;

    //Environment는 생성자 주입을 권장하고 있음
    public FirstServiceController(Environment env) {
        this.env = env;
    }

//    private static final Logger log = LoggerFactory.getLogger(FirstServiceController.class);

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to the First service";
    }

    @GetMapping("/message")
    public String message(@RequestHeader("first-request") String header) {
        log.info("header : {}", header);
        return "Hello World in First service";
    }

    @GetMapping("check")
    public String check(HttpServletRequest request) {
        log.info("Server port={}", request.getServerPort());
        return String.format("Hi, there. This is a message from First Service on Port %s", env.getProperty("local.server.port"));
    }
}
