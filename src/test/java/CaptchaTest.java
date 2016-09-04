import com.github.bingoohuang.utils.codec.Base64;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.junit.Test;
import util.AccountPool;
import util.Config;
import util.JsoupUtil;
import util.YeatsUtil;

import java.io.*;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * Created by Cabeza on 2016-09-01.
 */
public class CaptchaTest {
    @Test
    public void makeSuTest() {
        String su = "17079715071";
        try {
            su = Base64.base64(URLEncoder.encode(su, "utf8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(su);
    }

    @Test
    public void zuoyiTest() {
        int weight = -1;
        weight <<= 1;
        System.out.println(weight);
    }

    @Test
    public void preLoginTest() {
        String userName = "17079715071";
        String password = "11";
        String base64Str = null;
        try {
            base64Str = com.github.bingoohuang.utils.codec.Base64.base64(URLEncoder.encode(userName, "utf8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Connection con = JsoupUtil.getGetCon(
                Config.getValue("preLogin")
                        .replaceAll("#userBase64#", base64Str)
                        .replaceAll("#unixTime#", System.currentTimeMillis() + "")
        ).ignoreContentType(true);
        Connection.Response rs = null;
        try {
            rs = con.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject preLoginJson = new JSONObject(rs.body().replaceAll("jsonpcallback\\d+\\((\\{.*?\\})\\)", "$1"));
        if (preLoginJson.getInt("showpin") == 0) return;
        Connection captchaCon = JsoupUtil.getGetCon(Config.getValue("captchaImage")).ignoreContentType(true);
        Connection.Response captchaResponse = null;
        try {
            captchaResponse = captchaCon.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject captchaDataJson = new JSONObject(captchaResponse.body()).getJSONObject("data");
        byte[] bytes = com.github.bingoohuang.utils.codec.Base64.unBase64(StringUtils.substringAfter(captchaDataJson.getString("image"), "base64,"));
        String pcid = captchaDataJson.getString("pcid");
        File captchaFile = new File("yeats_captcha.png");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(captchaFile);
            out.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("验证码已保存" + ",路径为:" + captchaFile.getAbsolutePath() + "  pcid:" + pcid);
        Scanner sc = new Scanner(System.in);
        String captchaContent = sc.nextLine();
        Connection loginCon = JsoupUtil.getPostCon(Config.getValue("loginUrl"));
        Connection.Response loginResponse = null;
        try {
            loginCon.data("username", userName);
            loginCon.data("password", password);
            loginCon.data("savestate", "1");
            loginCon.data("ec", "0");
            loginCon.data("entry", "mweibo");
            loginCon.data("pincode", captchaContent);
            loginCon.data("pcid", pcid);
            loginCon.referrer("https://passport.weibo.cn/signin/login");
            loginResponse = loginCon.execute();
            JSONObject jsonObject = new JSONObject(loginResponse.body());
            String retCode = jsonObject.get("retcode").toString();
            if (retCode.equals("20000000")) {
                System.out.println("success");
            } else {
                System.out.println("failed");
            }
            System.out.println(YeatsUtil.unicode2Character(loginResponse.body()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}