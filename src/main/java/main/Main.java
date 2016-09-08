package main;

import entity.LoginInfo;
import entity.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import thread.FansTask;
import thread.FolloweeTask;
import thread.ThreadPool;
import thread.WeiboTask;
import us.codecraft.webmagic.thread.CountableThreadPool;
import util.*;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Cabeza on 2016-06-03.
 */
public class Main {

    private static Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        //ThreadPool threadPool = ThreadPool.getThreadPool(30);
        CountableThreadPool threadPool = new CountableThreadPool(5);
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入userid");
        String userId = sc.nextLine();
        User user = null;
        String url = Config.getValue("userHomeHtml").replaceAll("#userId#", userId);
        Connection con = JsoupUtil.getGetCon(url);
        Response rs = null;
        try {
            rs = con.cookies(AccountPool.getAccount().getCookies()).execute();
            user = new User(rs.body());
            System.out.println(user);

        } catch (IOException e) {
            log.info("获得用户信息失败");
            e.printStackTrace();
            return;
        }
        Jedis jedis = JedisUtil.getJedis();
        jedis.del(Config.getValue("jedisPeopleList"));
        jedis.del(Config.getValue("jedisWeiboList"));
        JedisUtil.returnResource(jedis);
        int fansPageCount = YeatsUtil.ceil(user.getFansCount(), Config.getValue("fansPageSize"));
        int followeeCount = YeatsUtil.ceil(user.getFolloweeCount(), Config.getValue("followeePageSize"));
        int weiboPageSize = YeatsUtil.ceil(user.getWeiboCount(), Config.getValue("weiboPageSize"));

       /* if (fansPageCount > 32)
            fansPageCount = 32;
        for (int i = 1; i <= fansPageCount; i++) {
            threadPool.execute(new FansTask(userId, i + "", AccountPool.getAccount().getCookies()));
        }
        for (int i = 1; i <= followeeCount; i++) {
            threadPool.execute(new FolloweeTask(userId, i + "", AccountPool.getAccount().getCookies()));
        }*/
        for (int i = 0; i <= weiboPageSize; i++) {
            threadPool.execute(new WeiboTask(user, i));
        }
    }
}
