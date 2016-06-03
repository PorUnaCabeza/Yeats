package util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

/**
 * Created by Cabeza on 2016/4/25.
 */
public class JsoupUtil {
    public static final String USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1";
    public static final int TIME_OUT=30000;
    public static Connection getPostCon(String url){
        Connection con= Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIME_OUT).method(Connection.Method.POST);
        return con;
    }
    public static Connection getGetCon(String url){
        Connection con= Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIME_OUT).method(Connection.Method.GET);
        return con;
    }

    public static Connection getResourceCon(String url){
        Connection con=Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIME_OUT).ignoreContentType(true).method(Connection.Method.GET);
        return con;
    }
}
