package yeatsapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import util.Collections;
import util.Config;
import util.JedisUtil;
import yeatsapi.service.YeatsResourcesService;

import java.util.List;
import java.util.Map;

/**
 * Created by Cabeza on 2016/9/18.
 */
@RestController
@RequestMapping("/yeats-resource/v1")
public class YeatsResourceController {
    @Autowired
    YeatsResourcesService yeatsResourcesService;

    @Async
    @RequestMapping("/comment/{userId}/{compareId}")
    public String crawlComment(@PathVariable String userId, @PathVariable String compareId) {
        Jedis jedis = JedisUtil.getJedis();
        if (jedis.get(Config.getValue("jedisTaskFlag")).equals("1")) {
            System.out.println("已有任务了");
            return "full";
        }
        jedis.set(Config.getValue("jedisTaskFlag"), "1");
        JedisUtil.returnResource(jedis);
        yeatsResourcesService.crawlComment(userId, compareId);
        System.out.println("crawl done");
        return "success";
    }

    @RequestMapping(value = "test", method = RequestMethod.POST)
    public Map test() {

        return Collections.asMap("a", 1, "b", "2");
    }

    @RequestMapping("log/{start}")
    public Map getLog(@PathVariable long start) {
        Jedis jedis = JedisUtil.getJedis();
        String status = jedis.get(Config.getValue("jedisTaskFlag"));
        JedisUtil.returnResource(jedis);
        return Collections.asMap(
                "start", start
                , "nextStart", start + Long.parseLong(Config.getValue("logPageSize"))
                , "list", yeatsResourcesService.getLog(start)
                , "status", status
        );
    }
}
