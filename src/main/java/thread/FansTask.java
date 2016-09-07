package thread;

import entity.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import util.Config;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import util.JedisUtil;
import util.JsoupUtil;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Cabeza on 2016-06-04.
 */
public class FansTask implements Runnable {
    private static Logger log = LoggerFactory.getLogger(FansTask.class);
    private String userId;
    private String page;
    private String url;
    private Map<String, String> cookies;

    public FansTask(String userId, String page, Map<String, String> cookies) {
        this.userId = userId;
        this.page = page;
        this.cookies = cookies;
        this.url = Config.getValue("fansUrl").replaceAll("#userId#", userId).replaceAll("#page#", page);
    }

    @Override
    public void run() {
        getFansInfo();
    }

    public boolean getFansInfo() {
        Connection con = JsoupUtil.getGetCon(url);
        Response rs = null;
        Jedis jedis = JedisUtil.getJedis();
        try {
            rs = con.cookies(cookies).ignoreContentType(true).execute();
            JSONObject jsonObject = new JSONObject(rs.body());
            String modeType = jsonObject.getJSONArray("cards").getJSONObject(0).getString("mod_type");
            if (modeType.equals("mod/empty"))
                return false;
            JSONArray cardGroup = jsonObject.getJSONArray("cards").getJSONObject(0).getJSONArray("card_group");
            for (int i = 0; i < cardGroup.length(); i++) {
                JSONObject card = cardGroup.getJSONObject(i);
                if (card.getInt("card_type") != 10) {
                    return true;
                }
                JSONObject userInfo = card.getJSONObject("user");
                User user = new User();
                user.setId(userInfo.getInt("id") + "");
                user.setName(userInfo.getString("screen_name"));
                user.setDesc(userInfo.getString("description"));
                user.setWeiboCount(userInfo.getInt("statuses_count") + "");
                user.setFansCount(userInfo.getInt("fansNum") + "");
                System.out.println(user);
                jedis.rpush(Config.getValue("jedisPeopleList"), user.getId() + "|" + user.getWeiboCount());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            JedisUtil.returnResource(jedis);
        }
        return false;
    }

}
