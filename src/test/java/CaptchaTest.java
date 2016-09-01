import org.jsoup.Connection;
import org.junit.Test;
import util.Base64;
import util.Config;
import util.JsoupUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Cabeza on 2016-09-01.
 */
public class CaptchaTest {
    @Test
    public void makeSuTest() {
        String su = "17079715071";
        try {
            su = Base64.encode(URLEncoder.encode(su, "utf8"));
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
        String base64Str = null;
        try {
            base64Str = Base64.encode(URLEncoder.encode(userName, "utf8"));
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
        System.out.println(rs.body());
    }
}
