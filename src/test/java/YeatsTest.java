import entity.LoginInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Base64;
import util.Config;
import util.JsoupUtil;
import util.YeatsUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Cabeza on 2016-06-03.
 */
public class YeatsTest {
    private static Logger log= LoggerFactory.getLogger(YeatsTest.class);

    @Test
    public void preLoginTest(){
        String base64Code= Base64.encode("123");
        String timeStr=System.currentTimeMillis()+"";
        String url="https://login.sina.com.cn/sso/prelogin.php?checkpin=1&entry=mweibo&su="+base64Code+"&callback=jsonpcallback"+timeStr;
        Connection con=JsoupUtil.getGetCon(url);
        Response rs=null;
        try {
            rs=con.execute();
            log.info(rs.body());
            Map<String, String> cookies=rs.cookies();
            for (Map.Entry<String, String> entry : cookies.entrySet()) {
                log.info(entry.getKey() + "=" + entry.getValue().replace("\"", ""));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getLoginCookieTest(){
        Connection con= JsoupUtil.getPostCon(Config.getValue("loginUrl"));
        Response rs=null;
        try {
            con.data("username","745158068@qq.com");
            con.data("password","cabeza");
            con.data("savestate","1");
            con.data("ec","0");
            con.data("entry","mweibo");
            con.referrer("https://passport.weibo.cn/signin/login");
            rs=con.execute();
            JSONObject jsonObject=new JSONObject(rs.body());
            String retCode=jsonObject.get("retcode").toString();
            if(retCode.equals("20000000")){
                LoginInfo.saveCookies("Yeats_cookies.txt", rs.cookies());
            }else{
                log.info("登录失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loginBySavedCookiesTest(){
        Map<String,String> cookies=null;
        cookies=LoginInfo.readCookies("Yeats_cookies.txt");
        if(cookies==null){
            log.info("读取的cookie为空");
            return;
        }
        Connection con=JsoupUtil.getGetCon("http://m.weibo.cn/");
        Response rs=null;
        try {
            rs=con.cookies(cookies).execute();
            log.info(rs.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void parseFollowers(){
        String url="http://m.weibo.cn/page/card?itemid=1005052064246252_-_WEIBO_INDEX_PROFILE_APPS&callback=_1464939118240_4";
        Connection con=JsoupUtil.getGetCon(url);
        Response rs=null;
        try {
            rs=con.cookies(LoginInfo.readCookies("Yeats_cookies.txt")).execute();
            log.info(rs.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void parseFollowersJson(){
        String str="_1464939118240_4({\"card_type\":2,\"card_type_name\":\"\\u5e94\\u7528\\u5217\\u8868\",\"itemid\":\"1005052064246252_-_WEIBO_INDEX_PROFILE_APPS\",\"title\":\"\\u5e94\\u7528\\u5217\\u8868\",\"apps\":[{\"title\":\"\\u8d44\\u6599\",\"icon\":\"\",\"scheme\":\"http:\\/\\/m.weibo.cn\\/users\\/2064246252\\/?\",\"count\":\"\\u8be6\\u7ec6\",\"type\":\"detail\",\"openurl\":\"\"},{\"title\":\"\\u5fae\\u535a\",\"icon\":\"\",\"scheme\":\"http:\\/\\/m.weibo.cn\\/page\\/tpl?containerid=1005052064246252_-_WEIBO_SECOND_PROFILE_WEIBO&itemid=&title=%E5%85%A8%E9%83%A8%E5%BE%AE%E5%8D%9A\",\"count\":\"149\",\"type\":\"weibo\",\"openurl\":\"\"},{\"title\":\"\\u5173\\u6ce8\",\"icon\":\"\",\"scheme\":\"http:\\/\\/m.weibo.cn\\/page\\/tpl?containerid=1005052064246252_-_FOLLOWERS\",\"count\":\"112\",\"type\":\"attention\",\"openurl\":\"\"},{\"title\":\"\\u7c89\\u4e1d\",\"icon\":\"\",\"scheme\":\"http:\\/\\/m.weibo.cn\\/page\\/tpl?containerid=1005052064246252_-_FANS\",\"count\":\"180\",\"page_type\":\"0\",\"type\":\"fans\",\"openurl\":\"\"},{\"title\":\"\\u8d5e\",\"icon\":\"http:\\/\\/u1.sinaimg.cn\\/upload\\/2012\\/08\\/06\\/userinfo_apps_gallery.png\",\"scheme\":\"http:\\/\\/m.weibo.cn\\/page\\/tpl?containerid=1005052064246252_-_WEIBO_SECOND_PROFILE_LIKE&title=%E8%B5%9E&uid=1629467041\",\"count\":\"875\",\"type\":\"like\"},{\"title\":\"\\u8db3\\u8ff9\",\"icon\":\"http:\\/\\/u1.sinaimg.cn\\/upload\\/2012\\/08\\/06\\/userinfo_apps_topic.png\",\"scheme\":\"http:\\/\\/m.weibo.cn\\/page\\/tpl?containerid=1005052064246252_-_WEIBO_SECOND_PROFILE_TRACE&title=%E8%B6%B3%E8%BF%B9&uid=1629467041\",\"count\":\"3\",\"page_type\":\"01\",\"type\":\"trace\",\"openurl\":\"\"},{\"title\":\"\\u8bdd\\u9898\",\"icon\":\"http:\\/\\/u1.sinaimg.cn\\/upload\\/2012\\/08\\/06\\/userinfo_apps_topic.png\",\"scheme\":\"http:\\/\\/m.weibo.cn\\/page\\/tpl?containerid=1005052064246252_-_WEIBO_SECOND_PROFILE_LIKE_ALL_TOPIC&page=1&count=20&title=%E8%AF%9D%E9%A2%98&extparam=page_index_card&uid=1629467041\",\"count\":\"0\",\"type\":\"topic\",\"openurl\":\"\"},{\"title\":\"\\u6e38\\u620f\",\"icon\":\"http:\\/\\/u1.sinaimg.cn\\/upload\\/2012\\/08\\/06\\/userinfo_apps_game.png\",\"scheme\":\"http:\\/\\/m.weibo.cn\\/page\\/tpl?containerid=1007072064246252_-_PROFILE&title=%E6%B8%B8%E6%88%8F&uid=1629467041\",\"count\":\"\",\"type\":\"game\",\"openurl\":\"\"},{\"title\":\"\\u97f3\\u4e50\",\"scheme\":\"http:\\/\\/m.weibo.cn\\/p\\/index?containerid=1035032064246252_-_WEIBO_SECOND_PROFILE_LIKE_AUDIO&containerid=1035032064246252_-_WEIBO_SECOND_PROFILE_LIKE_AUDIO&title=%E8%B5%9E%E8%BF%87%E7%9A%84%E9%9F%B3%E4%B9%90&uid=1629467041\",\"count\":\"\",\"type\":\"music\",\"openurl\":\"\"},{\"title\":\"\\u5fae\\u4eba\\u8109\",\"scheme\":\"http:\\/\\/renmai.weibo.cn\\/2064246252?from=&sinainternalbrowser=topnav&uid=1629467041\",\"count\":\"\",\"type\":\"weirenmai\",\"openurl\":\"\"}],\"cache_time\":0,\"skip_format\":0,\"openurl\":\"\",\"ok\":\"1\"})";
        String jsonStr=str.substring(str.indexOf("(")+1,str.lastIndexOf(")"));
        JSONObject jsonObject=new JSONObject(jsonStr);
        JSONArray apps=jsonObject.getJSONArray("apps");
        for(int i=0;i<apps.length();i++){
            System.out.print(apps.getJSONObject(i).getString("title")+": ");
            System.out.println(apps.getJSONObject(i).getString("count"));
        }

    }

}
