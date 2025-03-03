package com.example.secondservice;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/second-service/")
public class SecondServiceController {

//    private static final Logger log = LoggerFactory.getLogger(SecondServiceController.class);

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to the Second service";
    }

    @GetMapping("/message")
    public String message(@RequestHeader("second-request") String header) {
        log.info(header);
        return "Hello World in Second service";
    }

    @GetMapping("check")
    public String check() {
        return "Hi, there. This is a message from Second Service.";
    }
}
