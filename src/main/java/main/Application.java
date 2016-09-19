package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import util.Config;
import util.JedisUtil;


@SpringBootApplication
@RestController
@EnableAutoConfiguration
@ComponentScan(basePackages = "yeatsapi")
@EnableAsync
public class Application {

    public static void main(String[] args) {
        Jedis jedis = JedisUtil.getJedis();
        jedis.set(Config.getValue("jedisTaskFlag"), "0");
        jedis.del(Config.getValue("jedisLogList"));
        JedisUtil.returnResource(jedis);
        SpringApplication.run(Application.class, args);
    }

    @RequestMapping("/")
    public String greeting() {
        return "Hello World!";
    }
}