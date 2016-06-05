package thread;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Config;
import util.JsoupUtil;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Cabeza on 2016-06-05.
 */
public class FolloweeTask implements Runnable {
    private static Logger log= LoggerFactory.getLogger(FansTask.class);
    private String userId;
    private String page;
    private String url;
    private Map<String,String> cookies;

    public FolloweeTask(String userId, String page,Map<String,String> cookies) {
        this.userId = userId;
        this.page = page;
        this.cookies=cookies;
        this.url= Config.getValue("followeeUrl").replaceAll("#userId#",userId).replaceAll("#page#",page);
    }

    @Override
    public void run() {
        getFolloweeInfo();
    }

    public boolean getFolloweeInfo(){
        Connection con= JsoupUtil.getGetCon(url);
        Connection.Response rs=null;
        try {
            rs=con.cookies(cookies).ignoreContentType(true).execute();
            JSONObject jsonObject=new JSONObject(rs.body());
            String modeType=jsonObject.getJSONArray("cards").getJSONObject(0).getString("mod_type");
            System.out.println(modeType);
            if(modeType.equals("mod/empty"))
                return false;
            JSONArray cardGroup=jsonObject.getJSONArray("cards").getJSONObject(0).getJSONArray("card_group");
            for(int i=0;i<cardGroup.length();i++){
                JSONObject card=cardGroup.getJSONObject(i);
                if(card.getInt("card_type")!=10)
                    return false;
                System.out.print("微博id:"+card.getString("scheme"));
                JSONObject userInfo=card.getJSONObject("user");
                System.out.print(" 微博名:"+userInfo.getString("screen_name"));
                System.out.print(" 是否认证:"+userInfo.get("verified"));
                System.out.println("  微博总数"+userInfo.get("statuses_count"));
            }
        } catch (Exception e) {
            System.out.println("错误url"+url);
            e.printStackTrace();
        }
        return false;
    }
}
