import entity.LoginInfo;
import entity.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thread.CountableThreadPool;
import thread.WeiboTask;
import util.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Cabeza on 2016-06-03.
 */
public class YeatsTest {
    private static Logger log = LoggerFactory.getLogger(YeatsTest.class);

    @Test
    public void preLoginTest() {
        String base64Code = Base64.encode("123");
        String timeStr = System.currentTimeMillis() + "";
        String url = "https://login.sina.com.cn/sso/prelogin.php?checkpin=1&entry=mweibo&su=" + base64Code + "&callback=jsonpcallback" + timeStr;
        Connection con = JsoupUtil.getGetCon(url);
        Response rs = null;
        try {
            rs = con.execute();
            log.info(rs.body());
            Map<String, String> cookies = rs.cookies();
            for (Map.Entry<String, String> entry : cookies.entrySet()) {
                log.info(entry.getKey() + "=" + entry.getValue().replace("\"", ""));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getLoginCookieTest() {
        Connection con = JsoupUtil.getPostCon(Config.getValue("loginUrl"));
        Response rs = null;
        try {
            con.data("username", "745158068@qq.com");
            con.data("password", "cabeza");
            con.data("savestate", "1");
            con.data("ec", "0");
            con.data("entry", "mweibo");
            con.referrer("https://passport.weibo.cn/signin/login");
            rs = con.execute();
            JSONObject jsonObject = new JSONObject(rs.body());
            String retCode = jsonObject.get("retcode").toString();
            if (retCode.equals("20000000")) {
                LoginInfo.saveCookies("Yeats_cookies.txt", rs.cookies());
            } else {
                log.info("登录失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loginBySavedCookiesTest() {
        Map<String, String> cookies = null;
        cookies = LoginInfo.readCookies("Yeats_cookies.txt");
        if (cookies == null) {
            log.info("读取的cookie为空");
            return;
        }
        Connection con = JsoupUtil.getGetCon("http://m.weibo.cn/");
        Response rs = null;
        try {
            rs = con.cookies(cookies).execute();
            log.info(rs.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void parseFollowers() {
        String url = "http://m.weibo.cn/page/card?itemid=1005052064246252_-_WEIBO_INDEX_PROFILE_APPS&callback=_1464939118240_4";
        Connection con = JsoupUtil.getGetCon(url);
        Response rs = null;
        try {
            rs = con.cookies(LoginInfo.readCookies("Yeats_cookies.txt")).execute();
            log.info(rs.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void parseUserInfoJson() {
        String str = "_1464960188320_4({\"card_type\":2,\"card_type_name\":\"\\u5e94\\u7528\\u5217\\u8868\",\"itemid\":\"1005051629467041_-_WEIBO_INDEX_PROFILE_APPS\",\"title\":\"\\u5e94\\u7528\\u5217\\u8868\",\"apps\":[{\"title\":\"\\u5fae\\u535a\",\"icon\":\"\",\"scheme\":\"http:\\/\\/m.weibo.cn\\/page\\/tpl?containerid=1005051629467041_-_WEIBO_SECOND_PROFILE_WEIBO&itemid=&title=%E5%85%A8%E9%83%A8%E5%BE%AE%E5%8D%9A\",\"count\":\"139\",\"type\":\"weibo\",\"openurl\":\"\"},{\"title\":\"\\u5173\\u6ce8\",\"icon\":\"\",\"scheme\":\"http:\\/\\/m.weibo.cn\\/page\\/tpl?containerid=1005051629467041_-_FOLLOWERS\",\"count\":\"232\",\"type\":\"attention\",\"openurl\":\"\"},{\"title\":\"\\u7c89\\u4e1d\",\"icon\":\"\",\"scheme\":\"http:\\/\\/m.weibo.cn\\/page\\/tpl?containerid=1005051629467041_-_FANS\",\"count\":\"94\",\"page_type\":\"0\",\"type\":\"fans\",\"openurl\":\"\"},{\"title\":\"\\u8d5e\",\"icon\":\"http:\\/\\/u1.sinaimg.cn\\/upload\\/2012\\/08\\/06\\/userinfo_apps_gallery.png\",\"scheme\":\"http:\\/\\/m.weibo.cn\\/page\\/tpl?containerid=1005051629467041_-_WEIBO_SECOND_PROFILE_LIKE&title=%E8%B5%9E&uid=1629467041\",\"count\":\"200\",\"type\":\"like\"},{\"title\":\"\\u5206\\u7ec4\",\"icon\":\"http:\\/\\/u1.sinaimg.cn\\/upload\\/2012\\/08\\/06\\/userinfo_apps_group.png\",\"scheme\":\"http:\\/\\/m.weibo.cn\\/follow\\/group?uid=1629467041\",\"count\":\"0\",\"type\":\"group\",\"openurl\":\"\"},{\"title\":\"\\u8bdd\\u9898\",\"icon\":\"http:\\/\\/u1.sinaimg.cn\\/upload\\/2012\\/08\\/06\\/userinfo_apps_topic.png\",\"scheme\":\"http:\\/\\/m.weibo.cn\\/page\\/tpl?containerid=1005051629467041_-_WEIBO_SECOND_PROFILE_LIKE_ALL_TOPIC&page=1&count=20&title=%E8%AF%9D%E9%A2%98&extparam=page_index_card&uid=1629467041\",\"count\":\"0\",\"type\":\"topic\",\"openurl\":\"\"},{\"title\":\"\\u97f3\\u4e50\",\"scheme\":\"http:\\/\\/m.weibo.cn\\/p\\/index?containerid=1035031629467041_-_WEIBO_SECOND_PROFILE_LIKE_AUDIO&containerid=1035031629467041_-_WEIBO_SECOND_PROFILE_LIKE_AUDIO&title=%E8%B5%9E%E8%BF%87%E7%9A%84%E9%9F%B3%E4%B9%90&uid=1629467041\",\"count\":\"\",\"type\":\"music\",\"openurl\":\"\"},{\"title\":\"\\u4e8c\\u7ef4\\u7801\",\"scheme\":\"http:\\/\\/m.weibo.cn\\/home\\/vCard?sinainternalbrowser=topnav\",\"count\":\"\",\"type\":\"code\",\"openurl\":\"\"},{\"title\":\"\\u5fae\\u6570\\u636e\",\"scheme\":\"http:\\/\\/m.weibo.cn\\/pubs\\/profiledata?sinainternalbrowser=topnav&uid=1629467041\",\"count\":\"\",\"type\":\"data\",\"openurl\":\"\"},{\"title\":\"\\u5fae\\u4eba\\u8109\",\"scheme\":\"http:\\/\\/renmai.weibo.cn\\/1629467041?from=&sinainternalbrowser=topnav&uid=1629467041\",\"count\":\"\",\"type\":\"weirenmai\",\"openurl\":\"\"}],\"cache_time\":0,\"skip_format\":0,\"openurl\":\"\",\"ok\":\"1\"})";
        String jsonStr = str.substring(str.indexOf("(") + 1, str.lastIndexOf(")"));
        JSONObject jsonObject = new JSONObject(jsonStr);
        JSONArray apps = jsonObject.getJSONArray("apps");
        for (int i = 0; i < apps.length(); i++) {
            System.out.print(apps.getJSONObject(i).getString("title") + ": ");
            System.out.println(apps.getJSONObject(i).getString("count"));
        }

    }

    @Test
    public void ceilTest() {
        System.out.println(YeatsUtil.ceil(456, 10));
    }

    @Test
    public void parseFansJson() {
        String str = "{\"ok\":1,\"count\":200,\"cards\":[{\"mod_type\":\"mod\\/pagelist\",\"previous_cursor\":\"\",\"next_cursor\":\"\",\"card_group\":[{\"card_type\":10,\"user\":{\"id\":3480942447,\"screen_name\":\"\\u4f5c\\u5e9f1\\u4e0d\\u7528\\u5566\",\"profile_image_url\":\"http:\\/\\/tva2.sinaimg.cn\\/crop.0.0.512.512.180\\/cf7af76fjw8f2uhh63gryj20e80e8dgj.jpg\",\"profile_url\":\"\\/u\\/3480942447\",\"statuses_count\":0,\"verified\":false,\"verified_reason\":\"\",\"description\":\"\\u6ca1\\u5fc5\\u8981\\uff0c\\u4e5f\\u4e0d\\u9700\\u8981\",\"remark\":\"\",\"verified_type\":-1,\"gender\":\"m\",\"mbtype\":0,\"h5icon\":{\"main\":0,\"other\":[]},\"ismember\":0,\"valid\":null,\"fansNum\":9,\"follow_me\":false,\"following\":false,\"desc1\":null,\"desc2\":null},\"scheme\":\"\\/u\\/3480942447\",\"desc1\":\"\\u6ca1\\u5fc5\\u8981\\uff0c\\u4e5f\\u4e0d\\u9700\\u8981\",\"desc2\":\"\"},{\"card_type\":10,\"user\":{\"id\":5508921448,\"screen_name\":\"Zhang_ye_\",\"profile_image_url\":\"http:\\/\\/tva4.sinaimg.cn\\/crop.0.0.512.512.180\\/0060OS7Kjw8f1fh04okeij30e80e83z6.jpg\",\"profile_url\":\"\\/u\\/5508921448\",\"statuses_count\":25,\"verified\":false,\"verified_reason\":\"\",\"description\":\"\",\"remark\":\"\",\"verified_type\":-1,\"text\":\"\\u65b0\\u7684\\u73af\\u5883\\u65b0\\u7684\\u5fc3\\u60c5[\\u54fc][\\u54fc] http:\\/\\/t.cn\\/R2dbwnb\",\"created_at\":\"04-30 22:16\",\"gender\":\"m\",\"mbtype\":0,\"h5icon\":{\"main\":0,\"other\":[]},\"ismember\":0,\"valid\":null,\"fansNum\":2,\"follow_me\":false,\"following\":false,\"desc1\":\"\\u65b0\\u7684\\u73af\\u5883\\u65b0\\u7684\\u5fc3\\u60c5[\\u54fc][\\u54fc] http:\\/\\/t.cn\\/R2dbwnb\",\"desc2\":\"04-30 22:16\"},\"scheme\":\"\\/u\\/5508921448\",\"desc1\":\"\\u65b0\\u7684\\u73af\\u5883\\u65b0\\u7684\\u5fc3\\u60c5[\\u54fc][\\u54fc] http:\\/\\/t.cn\\/R2dbwnb\",\"desc2\":\"04-30 22:16\"},{\"card_type\":10,\"user\":{\"id\":5502288443,\"screen_name\":\"\\u5750\\u770b\\u592a\\u9633\\u9ad8\\u9ad8\\u6302\",\"profile_image_url\":\"http:\\/\\/tva4.sinaimg.cn\\/crop.0.0.512.512.180\\/0060n2zNjw8f14txfgq5hj30e80e8q3d.jpg\",\"profile_url\":\"\\/u\\/5502288443\",\"statuses_count\":37,\"verified\":false,\"verified_reason\":\"\",\"description\":\"\",\"remark\":\"\",\"verified_type\":-1,\"text\":\"\\u8f6c\\u53d1\\u5fae\\u535a\",\"created_at\":\"05-29 20:36\",\"gender\":\"m\",\"mbtype\":0,\"h5icon\":{\"main\":0,\"other\":[]},\"ismember\":0,\"valid\":null,\"fansNum\":10,\"follow_me\":false,\"following\":false,\"desc1\":\"\\u8f6c\\u53d1\\u5fae\\u535a\",\"desc2\":\"05-29 20:36\"},\"scheme\":\"\\/u\\/5502288443\",\"desc1\":\"\\u8f6c\\u53d1\\u5fae\\u535a\",\"desc2\":\"05-29 20:36\"},{\"card_type\":10,\"user\":{\"id\":2165593045,\"screen_name\":\"FAN-\\u9732\\u9732\\u9732\",\"profile_image_url\":\"http:\\/\\/tva2.sinaimg.cn\\/crop.0.0.512.512.180\\/811453d5jw8ezyt7vxwtuj20e80e8js0.jpg\",\"profile_url\":\"\\/u\\/2165593045\",\"statuses_count\":165,\"verified\":false,\"verified_reason\":\"\",\"description\":\"\",\"remark\":\"\",\"verified_type\":-1,\"text\":\"\\u6761\\u7eb9\\u4e0a\\u8863\\u597d\\u559c\\u6b22\\u54e6 \\u62bd\\u6211\\u62bd\\u6211[\\u7231\\u4f60][\\u7231\\u4f60]\",\"created_at\":\"05-14 07:22\",\"gender\":\"f\",\"mbtype\":0,\"h5icon\":{\"main\":0,\"other\":[]},\"ismember\":0,\"valid\":null,\"fansNum\":90,\"follow_me\":false,\"following\":false,\"desc1\":\"\\u6761\\u7eb9\\u4e0a\\u8863\\u597d\\u559c\\u6b22\\u54e6 \\u62bd\\u6211\\u62bd\\u6211[\\u7231\\u4f60][\\u7231\\u4f60]\",\"desc2\":\"05-14 07:22\"},\"scheme\":\"\\/u\\/2165593045\",\"desc1\":\"\\u6761\\u7eb9\\u4e0a\\u8863\\u597d\\u559c\\u6b22\\u54e6 \\u62bd\\u6211\\u62bd\\u6211[\\u7231\\u4f60][\\u7231\\u4f60]\",\"desc2\":\"05-14 07:22\"},{\"card_type\":10,\"user\":{\"id\":5845499073,\"screen_name\":\"\\u90dd\\u886c2009\",\"profile_image_url\":\"http:\\/\\/tva3.sinaimg.cn\\/crop.0.0.664.664.180\\/006nB7l7jw8f0isrmwcetj30ig0igabz.jpg\",\"profile_url\":\"\\/u\\/5845499073\",\"statuses_count\":4,\"verified\":false,\"verified_reason\":\"\",\"description\":\"\\u771f\\u8bda\\u5f85\\u4eba\\uff0c\\u8bda\\u5b9e\\u505a\\u4e8b\",\"remark\":\"\",\"verified_type\":-1,\"text\":\"\\u8fc7\\u5e74\\u4e86\\u8d70\\u4eb2\\u8bbf\\u53cb\\u5bb6\\u5bb6\\u90fd\\u6709\\u5b9d\\u8d1d\\uff0c\\u4f60\\u8fd8\\u5728\\u9001\\u725b\\u5976\\u997c\\u5e72\\u5417\\uff1f\\u65fa\\u65fa\\u793c\\u5305\\u5417\\uff1f\\u4fd7\\u4e0d\\u4fd7..\\uff01\\u770b\\u4e0b\\u9762..\\uff01\\u9996\\u5148\\u8981\\u5fc3\\u610f.. \\uff01\\u5176\\u6b21\\u521b\\u610f.. \\uff01\\u5b69\\u5b50\\u8981\\u6ee1\\u610f.. \\uff01\\u6d82\\u6d82\\u4e50....\\u8bed\\u8a00\\u5b66\\u4e60\\uff0c\\u7f8e\\u672f\\u542f\\u8499\\uff0c\\u52a8\\u624b\\u953b\\u70bc\\uff0c\\u6d82\\u6d82\\u4e50\\u6709\\u4f60\\u5168\\u90fd\\u79f0\\u5fc3\\u5982\\u610f[\\u5f3a][\\u5f3a][\\u5f3a] http:\\/\\/t.cn\\/R2dLiSH http:\\/\\/t.cn\\/RbrFR1F .\",\"created_at\":\"01-31 21:52\",\"gender\":\"f\",\"mbtype\":0,\"h5icon\":{\"main\":0,\"other\":[]},\"ismember\":0,\"valid\":null,\"fansNum\":13,\"follow_me\":false,\"following\":false,\"desc1\":\"\\u8fc7\\u5e74\\u4e86\\u8d70\\u4eb2\\u8bbf\\u53cb\\u5bb6\\u5bb6\\u90fd\\u6709\\u5b9d\\u8d1d\\uff0c\\u4f60\\u8fd8\\u5728\\u9001\\u725b\\u5976\\u997c\\u5e72\\u5417\\uff1f\\u65fa\\u65fa\\u793c\\u5305\\u5417\\uff1f\\u4fd7\\u4e0d\\u4fd7..\\uff01\\u770b\\u4e0b\\u9762..\\uff01\\u9996\\u5148\\u8981\\u5fc3\\u610f.. \\uff01\\u5176\\u6b21\\u521b\\u610f.. \\uff01\\u5b69\\u5b50\\u8981\\u6ee1\\u610f.. \\uff01\\u6d82\\u6d82\\u4e50....\\u8bed\\u8a00\\u5b66\\u4e60\\uff0c\\u7f8e\\u672f\\u542f\\u8499\\uff0c\\u52a8\\u624b\\u953b\\u70bc\\uff0c\\u6d82\\u6d82\\u4e50\\u6709\\u4f60\\u5168\\u90fd\\u79f0\\u5fc3\\u5982\\u610f[\\u5f3a][\\u5f3a][\\u5f3a] http:\\/\\/t.cn\\/R2dLiSH http:\\/\\/t.cn\\/RbrFR1F .\",\"desc2\":\"01-31 21:52\"},\"scheme\":\"\\/u\\/5845499073\",\"desc1\":\"\\u8fc7\\u5e74\\u4e86\\u8d70\\u4eb2\\u8bbf\\u53cb\\u5bb6\\u5bb6\\u90fd\\u6709\\u5b9d\\u8d1d\\uff0c\\u4f60\\u8fd8\\u5728\\u9001\\u725b\\u5976\\u997c\\u5e72\\u5417\\uff1f\\u65fa\\u65fa\\u793c\\u5305\\u5417\\uff1f\\u4fd7\\u4e0d\\u4fd7..\\uff01\\u770b\\u4e0b\\u9762..\\uff01\\u9996\\u5148\\u8981\\u5fc3\\u610f.. \\uff01\\u5176\\u6b21\\u521b\\u610f.. \\uff01\\u5b69\\u5b50\\u8981\\u6ee1\\u610f.. \\uff01\\u6d82\\u6d82\\u4e50....\\u8bed\\u8a00\\u5b66\\u4e60\\uff0c\\u7f8e\\u672f\\u542f\\u8499\\uff0c\\u52a8\\u624b\\u953b\\u70bc\\uff0c\\u6d82\\u6d82\\u4e50\\u6709\\u4f60\\u5168\\u90fd\\u79f0\\u5fc3\\u5982\\u610f[\\u5f3a][\\u5f3a][\\u5f3a] http:\\/\\/t.cn\\/R2dLiSH http:\\/\\/t.cn\\/RbrFR1F .\",\"desc2\":\"01-31 21:52\"},{\"card_type\":10,\"user\":{\"id\":1842834630,\"screen_name\":\"\\u5c0f\\u795e\\u7ae5\\u6cf0\\u56fd\\u4ee3\\u8d2d\",\"profile_image_url\":\"http:\\/\\/tva4.sinaimg.cn\\/crop.0.0.750.750.180\\/6dd76cc6jw8f0el0n2bl8j20ku0kuq50.jpg\",\"profile_url\":\"\\/u\\/1842834630\",\"statuses_count\":111,\"verified\":false,\"verified_reason\":\"\",\"description\":\".\\u6cf0\\u56fd\\u6b63\\u54c1\\u4ee3\\u8d2d\\u3002 CONY\\u4e2d\\u56fd\\u5730\\u533a\\u5408\\u4f5c\\u4f19\\u4f34\\uff0c\\u6cf0\\u8d27\\u4e00\\u624b\\u6279\\u53d1\\uff0c\\u6b22\\u8fce\\u5e7f\\u5927\\u540c\\u884c\\u4ea4\\u6d41\\u5408\\u4f5c\\u3002\\u5fae\\u4fe1\\uff1axc492805082 QQ\\uff1a492805082\\u3002\\u62db\\u4ee3\\u7406\",\"remark\":\"\",\"verified_type\":220,\"text\":\"#\\u6cf0\\u56fd\\u8717\\u725b\\u971cSnailWhite##\\u6cf0\\u56fdmistine\\u773c\\u7ebf\\u6db2\\u7b14##\\u6cf0\\u56fdele# \\u81ea\\u53d6\\u54c8[\\u7231\\u4f60][\\u7231\\u4f60]\",\"created_at\":\"05-24 23:07\",\"gender\":\"m\",\"mbtype\":0,\"h5icon\":{\"main\":\"http:\\/\\/u1.sinaimg.cn\\/upload\\/2013\\/02\\/22\\/star_square_2x.png\",\"other\":[]},\"ismember\":0,\"valid\":null,\"fansNum\":1371,\"follow_me\":false,\"following\":false,\"desc1\":\"#\\u6cf0\\u56fd\\u8717\\u725b\\u971cSnailWhite##\\u6cf0\\u56fdmistine\\u773c\\u7ebf\\u6db2\\u7b14##\\u6cf0\\u56fdele# \\u81ea\\u53d6\\u54c8[\\u7231\\u4f60][\\u7231\\u4f60]\",\"desc2\":\"05-24 23:07\"},\"scheme\":\"\\/u\\/1842834630\",\"desc1\":\"#\\u6cf0\\u56fd\\u8717\\u725b\\u971cSnailWhite##\\u6cf0\\u56fdmistine\\u773c\\u7ebf\\u6db2\\u7b14##\\u6cf0\\u56fdele# \\u81ea\\u53d6\\u54c8[\\u7231\\u4f60][\\u7231\\u4f60]\",\"desc2\":\"05-24 23:07\"},{\"card_type\":10,\"user\":{\"id\":5691730544,\"screen_name\":\"\\u5b9b\\u5982\\u7684\\u4e91\\u5fae\\u5546\",\"profile_image_url\":\"http:\\/\\/tva3.sinaimg.cn\\/crop.0.0.716.716.180\\/006dbV7yjw8eznroswypyj30jw0jx0vr.jpg\",\"profile_url\":\"\\/u\\/5691730544\",\"statuses_count\":25,\"verified\":false,\"verified_reason\":\"\",\"description\":\"\\u8ba9\\u6211\\u4eec\\u6bcf\\u5929\\u90fd\\u7f8e\\u7f8e\\u54d2\",\"remark\":\"\",\"verified_type\":-1,\"text\":\"\\u81ea\\u4ece\\u7528\\u4e86\\u6211\\u4eec\\u5bb6\\u4ea7\\u54c1\\uff0c\\u89e3\\u51b3\\u4e86\\u4e00\\u76f4\\u5934\\u75bc\\u6211\\u7684\\u6bdb\\u5b54\\u7c97\\u5927\\uff0c\\u76ae\\u80a4\\u4e5f\\u53d8\\u7684\\u5149\\u6ed1\\u8bb8\\u591a\\uff0c\\u6628\\u5929\\u665a\\u4e0a\\u4eb2\\u7231\\u7684\\u8001\\u516c\\u8bf4\\u6211\\u7684\\u76ae\\u80a4\\u597d\\u6ed1\\/\\u5bb3\\u7f9e \\/\\u5bb3\\u7f9e \\u4ed6\\u73b0\\u5728\\u5df2\\u7ecf\\u662f\\u7231\\u4e0d\\u91ca\\u624b\\u4e86\\u3002\\u4fd7\\u8bdd\\u8bf4\\u8981\\u6293\\u4f4f\\u7537\\u4eba\\u7684\\u5fc3\\u5148\\u6293\\u4f4f\\u7537\\u4eba\\u7684\\u80c3\\uff0c\\u8fd9\\u5f88\\u91cd\\u8981\\u4f46\\u662f\\u5973\\u4eba\\u7684\\u7f8e\\u8c8c\\u66f4\\u6b63\\u8981\\u3002\",\"created_at\":\"02-03 20:12\",\"gender\":\"f\",\"mbtype\":0,\"h5icon\":{\"main\":0,\"other\":[]},\"ismember\":0,\"valid\":null,\"fansNum\":45,\"follow_me\":false,\"following\":false,\"desc1\":\"\\u81ea\\u4ece\\u7528\\u4e86\\u6211\\u4eec\\u5bb6\\u4ea7\\u54c1\\uff0c\\u89e3\\u51b3\\u4e86\\u4e00\\u76f4\\u5934\\u75bc\\u6211\\u7684\\u6bdb\\u5b54\\u7c97\\u5927\\uff0c\\u76ae\\u80a4\\u4e5f\\u53d8\\u7684\\u5149\\u6ed1\\u8bb8\\u591a\\uff0c\\u6628\\u5929\\u665a\\u4e0a\\u4eb2\\u7231\\u7684\\u8001\\u516c\\u8bf4\\u6211\\u7684\\u76ae\\u80a4\\u597d\\u6ed1\\/\\u5bb3\\u7f9e \\/\\u5bb3\\u7f9e \\u4ed6\\u73b0\\u5728\\u5df2\\u7ecf\\u662f\\u7231\\u4e0d\\u91ca\\u624b\\u4e86\\u3002\\u4fd7\\u8bdd\\u8bf4\\u8981\\u6293\\u4f4f\\u7537\\u4eba\\u7684\\u5fc3\\u5148\\u6293\\u4f4f\\u7537\\u4eba\\u7684\\u80c3\\uff0c\\u8fd9\\u5f88\\u91cd\\u8981\\u4f46\\u662f\\u5973\\u4eba\\u7684\\u7f8e\\u8c8c\\u66f4\\u6b63\\u8981\\u3002\",\"desc2\":\"02-03 20:12\"},\"scheme\":\"\\/u\\/5691730544\",\"desc1\":\"\\u81ea\\u4ece\\u7528\\u4e86\\u6211\\u4eec\\u5bb6\\u4ea7\\u54c1\\uff0c\\u89e3\\u51b3\\u4e86\\u4e00\\u76f4\\u5934\\u75bc\\u6211\\u7684\\u6bdb\\u5b54\\u7c97\\u5927\\uff0c\\u76ae\\u80a4\\u4e5f\\u53d8\\u7684\\u5149\\u6ed1\\u8bb8\\u591a\\uff0c\\u6628\\u5929\\u665a\\u4e0a\\u4eb2\\u7231\\u7684\\u8001\\u516c\\u8bf4\\u6211\\u7684\\u76ae\\u80a4\\u597d\\u6ed1\\/\\u5bb3\\u7f9e \\/\\u5bb3\\u7f9e \\u4ed6\\u73b0\\u5728\\u5df2\\u7ecf\\u662f\\u7231\\u4e0d\\u91ca\\u624b\\u4e86\\u3002\\u4fd7\\u8bdd\\u8bf4\\u8981\\u6293\\u4f4f\\u7537\\u4eba\\u7684\\u5fc3\\u5148\\u6293\\u4f4f\\u7537\\u4eba\\u7684\\u80c3\\uff0c\\u8fd9\\u5f88\\u91cd\\u8981\\u4f46\\u662f\\u5973\\u4eba\\u7684\\u7f8e\\u8c8c\\u66f4\\u6b63\\u8981\\u3002\",\"desc2\":\"02-03 20:12\"},{\"card_type\":10,\"user\":{\"id\":5213270853,\"screen_name\":\"\\u7f81\\u7ecaSoda\",\"profile_image_url\":\"http:\\/\\/tva2.sinaimg.cn\\/crop.0.1.510.510.180\\/005GOlTLjw8f1wpx88tncj30e60e8t9b.jpg\",\"profile_url\":\"\\/u\\/5213270853\",\"statuses_count\":3,\"verified\":false,\"verified_reason\":\"\",\"description\":\"\",\"remark\":\"\",\"verified_type\":-1,\"text\":\"\\u539f\\u6765\\u4e16\\u754c\\u4e0a\\u771f\\u7684\\u6ca1\\u6709\\u4ec0\\u4e48\\u662f\\u6c38\\u8fdc\\u3002\\u4e00\\u6bb5\\u53cb\\u60c5\\uff0c\\u79bb\\u5f00\\u4e86\\u5c31\\u6de1\\u4e86\\u3002\\u4e00\\u6bb5\\u7231\\u60c5\\uff0c\\u5206\\u79bb\\u4e86\\u5c31\\u6563\\u4e86\\u3002\\u73b0\\u5728\\u6240\\u62e5\\u6709\\u7684\\uff0c\\u4e5f\\u8bb8\\u4e0b\\u4e00\\u79d2\\uff0c\\u5c31\\u4e0d\\u518d\\u5c5e\\u4e8e\\u4f60\\u3002\",\"created_at\":\"02-29 22:42\",\"gender\":\"m\",\"mbtype\":0,\"h5icon\":{\"main\":0,\"other\":[]},\"ismember\":0,\"valid\":null,\"fansNum\":9,\"follow_me\":false,\"following\":false,\"desc1\":\"\\u539f\\u6765\\u4e16\\u754c\\u4e0a\\u771f\\u7684\\u6ca1\\u6709\\u4ec0\\u4e48\\u662f\\u6c38\\u8fdc\\u3002\\u4e00\\u6bb5\\u53cb\\u60c5\\uff0c\\u79bb\\u5f00\\u4e86\\u5c31\\u6de1\\u4e86\\u3002\\u4e00\\u6bb5\\u7231\\u60c5\\uff0c\\u5206\\u79bb\\u4e86\\u5c31\\u6563\\u4e86\\u3002\\u73b0\\u5728\\u6240\\u62e5\\u6709\\u7684\\uff0c\\u4e5f\\u8bb8\\u4e0b\\u4e00\\u79d2\\uff0c\\u5c31\\u4e0d\\u518d\\u5c5e\\u4e8e\\u4f60\\u3002\",\"desc2\":\"02-29 22:42\"},\"scheme\":\"\\/u\\/5213270853\",\"desc1\":\"\\u539f\\u6765\\u4e16\\u754c\\u4e0a\\u771f\\u7684\\u6ca1\\u6709\\u4ec0\\u4e48\\u662f\\u6c38\\u8fdc\\u3002\\u4e00\\u6bb5\\u53cb\\u60c5\\uff0c\\u79bb\\u5f00\\u4e86\\u5c31\\u6de1\\u4e86\\u3002\\u4e00\\u6bb5\\u7231\\u60c5\\uff0c\\u5206\\u79bb\\u4e86\\u5c31\\u6563\\u4e86\\u3002\\u73b0\\u5728\\u6240\\u62e5\\u6709\\u7684\\uff0c\\u4e5f\\u8bb8\\u4e0b\\u4e00\\u79d2\\uff0c\\u5c31\\u4e0d\\u518d\\u5c5e\\u4e8e\\u4f60\\u3002\",\"desc2\":\"02-29 22:42\"}]}]}";
        JSONObject jsonObject = new JSONObject(str);
        String modeType = jsonObject.getJSONArray("cards").getJSONObject(0).getString("mod_type");
        System.out.println(modeType);
        if (modeType.equals("mod/empty"))
            return;
        JSONArray cardGroup = jsonObject.getJSONArray("cards").getJSONObject(0).getJSONArray("card_group");
        for (int i = 0; i < cardGroup.length(); i++) {
            JSONObject card = cardGroup.getJSONObject(i);
            System.out.print("微博id:" + card.getString("scheme"));
            JSONObject userInfo = card.getJSONObject("user");
            System.out.print(" 微博名:" + userInfo.getString("screen_name"));
            System.out.print(" 是否认证:" + userInfo.get("verified"));
            System.out.println("  微博总数" + userInfo.get("statuses_count"));
        }
    }

    @Test
    public void testWanmeiChongwu() {
        Random random = new Random();
        String url = "http://event.wanmei.com/m/w2i/pethatch/act?action=hand&r=0." + random.nextInt(10000) + "" + System.currentTimeMillis();
        Connection con = JsoupUtil.getGetCon(url).ignoreContentType(true).userAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 9_3_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Mobile/13E238 MicroMessenger/6.3.15 NetType/WIFI Language/zh_CN");
        Response rs = null;
        try {
            rs = con.cookie("Hm_lvt_69fae2e80f54618b006b557e1d2b3159", "1465448299,1465457151")
                    .cookie("JSESSIONID", "841143A87F7AA222DB9031E7A2E0E520-m1")
                    .cookie("__mtxcar", "(direct):(none)")
                    .cookie("__mtxsd", "f4bc3916.1465458376965.3856.5")
                    .cookie("__mtxsr", "csr:(direct)|cdt:(direct)|advt:(none)|camp:(none)")
                    .cookie("__mtxud", "b89efd5152c8d5fe.1465457150784.1465457150784.1465458145451.2").execute();
            System.out.println(rs.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void lingyang() {
        String url = "http://event.wanmei.com/m/w2i/pethatch/train";
        Connection con = JsoupUtil.getGetCon(url)
                .ignoreContentType(true)
                .userAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 9_3_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Mobile/13E238 MicroMessenger/6.3.15 NetType/WIFI Language/zh_CN")
                .referrer("http://event.wanmei.com/m/w2i/pethatch/get");
        Response rs = null;
        try {
            rs = con.cookie("Hm_lvt_69fae2e80f54618b006b557e1d2b3159", "1465448299,1465457151")
                    .cookie("JSESSIONID", "841143A87F7AA222DB9031E7A2E0E520-m1")
                    .cookie("__mtxcar", "(direct):(none)")
                    .cookie("__mtxsd", "f4bc3916.1465458376965.3856.5")
                    .cookie("__mtxsr", "csr:(direct)|cdt:(direct)|advt:(none)|camp:(none)")
                    .cookie("__mtxud", "b89efd5152c8d5fe.1465457150784.1465457150784.1465458145451.2").execute();
            System.out.println(rs.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUnixTime() {
        Random random = new Random();

        System.out.println(random.nextInt(10000) + "" + System.currentTimeMillis());
    }

    @Test
    public void getUserInfoTest() {
        Connection con = JsoupUtil.getGetCon("http://m.weibo.cn/u/1292253127");
        Response rs = null;
        try {
            rs = con.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        User user = new User(rs.body());
        System.out.println(user);
    }


    @Test
    public void weiboTaskTest() {
        long start = System.currentTimeMillis();
        String userId = "1292253127";
        Connection userCon = JsoupUtil.getGetCon(Config.getValue("userHomeHtml").replaceAll("#userId#", userId));
        Response userRs = null;
        try {
            userRs = userCon.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        User user = new User(userRs.body());
        System.out.println(user);
        int size = YeatsUtil.ceil(user.getWeiboCount(), Config.getValue("weiboPageSize"));
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger error = new AtomicInteger(0);
        for (int a = 0; a <= size; a++) {
            Connection con = JsoupUtil.getGetCon(Config.getValue("weiboListUrl").replaceAll("#userId#", userId).replaceAll("#page#", a + ""));
            Response rs = null;
            try {
                rs = con.cookies(AccountPool.getAccount().getCookies()).ignoreContentType(true).execute();
                JSONArray cards = new JSONObject(rs.body()).getJSONArray("cards");
                JSONArray list = cards.getJSONObject(0).getJSONArray("card_group");
                for (int i = 0; i < list.length(); i++) {
                    System.out.println(list.getJSONObject(i).getJSONObject("mblog").getString("text"));
                    success.incrementAndGet();
                }
            } catch (Exception e) {
                error.incrementAndGet();
                System.out.println(rs.body());
                e.printStackTrace();
            } finally {
                System.out.println("cabeza成功" + success.intValue() + "失败" + error.get());
            }
        }
    }

    @Test
    public void testBoolean() {
        int i = 0;
        boolean a = false;
        while (i < 3) {
            System.out.println(i);
            if (a = true) break;
            i++;
        }
        System.out.println(a);
    }

    @Test
    public void testWeiboTask() {
        CountableThreadPool threadPool = new CountableThreadPool(5);
        String userId = "1629467041";
        Connection userCon = JsoupUtil.getGetCon(Config.getValue("userHomeHtml").replaceAll("#userId#", userId));
        Response userRs = null;
        try {
            userRs = userCon.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        User user = new User(userRs.body());
        System.out.println(user);
        int size = YeatsUtil.ceil(user.getWeiboCount(), Config.getValue("weiboPageSize"));
        System.out.println(size);
        for (int i = 0; i <= size; i++) {
            threadPool.execute(new WeiboTask(user, i));
        }
    }

    public static void main(String[] args) {
        new YeatsTest().testWeiboTask();
    }
}
