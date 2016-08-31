package thread;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import util.Config;
import util.JedisUtil;
import util.JsoupUtil;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Cabeza on 2016-06-05.
 */
public class FolloweeTask implements Runnable {
    private static Logger log = LoggerFactory.getLogger(FansTask.class);
    private String userId;
    private String page;
    private String url;
    private Map<String, String> cookies;
    private boolean getInfo = false;

    public FolloweeTask(String userId, String page, Map<String, String> cookies) {
        this.userId = userId;
        this.page = page;
        this.cookies = cookies;
        this.url = Config.getValue("followeeUrl").replaceAll("#userId#", userId).replaceAll("#page#", page);
    }

    @Override
    public void run() {
        int count = 0;
        while (!getInfo && !getFolloweeInfo() && count < 2) {
            log.info(userId + "第" + count + "次拉取失败");
            count++;
        }
    }

    public boolean getFolloweeInfo() {
        System.out.println(url);
        Connection con = JsoupUtil.getGetCon(url);
        Response rs = null;
        Jedis jedis=JedisUtil.getJedis();
        try {
            rs = con.cookies(cookies).ignoreContentType(true).execute();
            JSONObject jsonObject = new JSONObject(rs.body());
            String modeType = jsonObject.getJSONArray("cards").getJSONObject(0).getString("mod_type");
            if (modeType.equals("mod/empty")){
                System.out.println("mod 为空");
                return false;
            }
            JSONArray cardGroup = jsonObject.getJSONArray("cards").getJSONObject(0).getJSONArray("card_group");
            for (int i = 0; i < cardGroup.length(); i++) {
                JSONObject card = cardGroup.getJSONObject(i);
                if (card.getInt("card_type") != 10) {
                    return true;
                }
                JSONObject userInfo = card.getJSONObject("user");
                String peopleId=card.getString("scheme").replaceAll("/u/","");
                boolean verified=userInfo.getBoolean("verified");
                int mCount=userInfo.getInt("statuses_count");
                System.out.println(peopleId);
               jedis.rpush(Config.getValue("jedisPeopleList"),peopleId+"|"+mCount);
            }
            getInfo = true;
            return true;
        } catch (Exception e) {
            System.out.println("错误url" + url);
            System.out.println(rs.body());
            e.printStackTrace();
        }finally {
            JedisUtil.returnResource(jedis);
        }
        return false;
    }
}
