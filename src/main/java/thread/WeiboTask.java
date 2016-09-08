package thread;

import entity.Account;
import entity.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import util.AccountPool;
import util.Config;
import util.JedisUtil;
import util.JsoupUtil;

import java.io.IOException;
import java.util.List;

/**
 * Created by zhangxs on 2016/9/8.
 */
public class WeiboTask implements Runnable {
    private static Logger log = LoggerFactory.getLogger(WeiboTask.class);
    private User user;
    private int page;
    private boolean success = false;

    public WeiboTask(User user, int page) {
        this.user = user;
        this.page = page;
    }

    @Override
    public void run() {
        int i = 0;
        while (i < 3) {
            if (success = getWeiboList()) break;
            i++;
        }
        if (!success) {
            log.info("三次均未成功");
        }
    }

    public boolean getWeiboList() {
        Connection con = JsoupUtil
                .getGetCon(Config.getValue("weiboListUrl").replaceAll("#userId#", user.getId()).replaceAll("#page#", page + ""))
                .cookies(AccountPool.getAccount().getCookies());
        Connection.Response rs = null;
        Jedis jedis = JedisUtil.getJedis();
        try {
            rs = con.ignoreContentType(true).execute();
            JSONArray cards = new JSONObject(rs.body()).getJSONArray("cards");
            JSONArray list = cards.getJSONObject(0).getJSONArray("card_group");
            for (int i = 0; i < list.length(); i++) {
                JSONObject mblog = list.getJSONObject(i).getJSONObject("mblog");
                System.out.println(mblog.getString("mid") + "评论" + mblog.get("comments_count").toString() + mblog.getString("text"));
                if (mblog.getInt("comments_count") > 0)
                    jedis.rpush(Config.getValue("jedisWeiboList"), mblog.getString("mid") + "|" + mblog.get("comments_count").toString());
                //do something
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            JedisUtil.returnResource(jedis);
        }
    }
}
