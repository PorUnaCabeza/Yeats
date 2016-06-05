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
import util.Config;
import util.JedisUtil;
import util.JsoupUtil;
import util.YeatsUtil;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Cabeza on 2016-06-03.
 */
public class Main {

    private static Logger log= LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        ThreadPool threadPool = ThreadPool.getThreadPool(30);
        Scanner sc=new Scanner(System.in);
        LoginInfo loginInfo=new LoginInfo();
        loginInfo.login();
       /* System.out.println("请输入userid");
        String userId=sc.nextLine();*/
        String userId="1629467041";
        User user=null;
        String url=Config.getValue("userInfoUrl").replaceAll("#userId#",userId).replaceAll("#unixTime#",System.currentTimeMillis()+"");
        Connection con= JsoupUtil.getGetCon(url);
        Response rs=null;
        try {
            rs=con.cookies(loginInfo.getLoginCookies()).execute();
            user=new User(rs.body());
            System.out.println(user);

        } catch (IOException e) {
            log.info("获得用户信息失败");
            e.printStackTrace();
            return;
        }
        /*Jedis jedis= JedisUtil.getJedis();
        jedis.set(Config.getValue("jedisUserId"),userId);*/
        int fansPageCount=YeatsUtil.ceil(user.getFansCount(),Config.getValue("fansPageSize"));
        int followeeCount=YeatsUtil.ceil(user.getFolloweeCount(),Config.getValue("followeePageSize"));
        if(fansPageCount>20)
            fansPageCount=22;
       /* for(int i=1;i<=fansPageCount;i++){
            threadPool.execute(new FansTask(userId,i+"",loginInfo.getLoginCookies()));
        }*/
        for(int i=1;i<followeeCount;i++){
            threadPool.execute(new FolloweeTask(userId,i+"",loginInfo.getLoginCookies()));
        }

    }
}