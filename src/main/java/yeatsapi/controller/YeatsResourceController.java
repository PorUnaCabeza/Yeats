package yeatsapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import util.Collections;
import yeatsapi.service.YeatsResourcesService;

import java.util.Map;

/**
 * Created by Cabeza on 2016/9/18.
 */
@RestController
@RequestMapping("/yeats-resource/v1")
public class YeatsResourceController {
    @Autowired
    YeatsResourcesService service;

    @Async
    @RequestMapping("/index")
    public String greeting() {
        System.out.println("cabeza___________________");
        service.test();
        return "hhh";
    }

    @RequestMapping(value = "test", method = RequestMethod.POST)
    public Map test() {

        return Collections.asMap("a", 1, "b", "2");
    }
}
