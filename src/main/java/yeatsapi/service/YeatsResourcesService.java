package yeatsapi.service;

import entity.User;
import org.jsoup.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import proxy.ProxyPool;
import redis.clients.jedis.Jedis;
import thread.CommentTask;
import thread.CountableThreadPool;
import thread.WeiboTask;
import util.*;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Cabeza on 2016/9/18.
 */
@Service
public class YeatsResourcesService {
    private static Logger log = LoggerFactory.getLogger(YeatsResourcesService.class);

    public void crawlComment(String userId, String compareId) {
        CountableThreadPool threadPool = new CountableThreadPool(5);
        User user = null;
        String url = Config.getValue("userHomeHtml").replaceAll("#userId#", userId);
        Connection con = JsoupUtil.getGetCon(url);
        Connection.Response rs = null;
        try {
            rs = con.cookies(AccountPool.getAccount().getCookies()).execute();
            user = new User(rs.body());
            System.out.println(user);

        } catch (IOException e) {
            log.info("获得用户信息失败");
            YeatsUtil.jedisLog("获得用户信息失败");
            e.printStackTrace();
            return;
        }
        YeatsUtil.jedisLog(user.toString());
        ProxyPool.initAndCheckProxy();
        System.out.println("等待代理初始化");
        YeatsUtil.jedisLog("等待代理初始化");
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Jedis jedis = JedisUtil.getJedis();
        jedis.del(Config.getValue("jedisPeopleList"));
        jedis.del(Config.getValue("jedisWeiboList"));
        JedisUtil.returnResource(jedis);
        int weiboPageSize = YeatsUtil.ceil(user.getWeiboCount(), Config.getValue("weiboPageSize"));
        for (int i = 1; i <= weiboPageSize; i++) {
            threadPool.execute(new WeiboTask(user, i));
        }
        new Thread(() -> {
            while (true) {
                Jedis commentJedis = JedisUtil.getJedis();
                if (threadPool.getThreadAlive() == 0) {
                    threadPool.shutdown();
                    break;
                }
                try {
                    Thread.sleep(5000);
                    long size = commentJedis.llen(Config.getValue("jedisWeiboList"));
                    if (size == 0) continue;
                    for (int i = 0; i < size; i++) {
                        String[] commentArr = commentJedis.lpop(Config.getValue("jedisWeiboList")).split(",");
                        for (int j = 1; j <= YeatsUtil.ceil(commentArr[1], Config.getValue("commentPageSize")); j++) {
                            threadPool.execute(new CommentTask(commentArr[0], j + "", AccountPool.getAccount().getCookies(), compareId));
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    JedisUtil.returnResource(commentJedis);
                }
            }
        }).run();
        Jedis clearJedis = JedisUtil.getJedis();
        clearJedis.set(Config.getValue("jedisTaskFlag"), "0");
        JedisUtil.returnResource(clearJedis);
        System.out.println("Yeats comment shutdown");
    }


    public List<String> getLog(long start) {
        Jedis jedis = JedisUtil.getJedis();
        List list = jedis.lrange(Config.getValue("jedisLogList"), start, start + 10);
        JedisUtil.returnResource(jedis);
        return list;
    }
}
