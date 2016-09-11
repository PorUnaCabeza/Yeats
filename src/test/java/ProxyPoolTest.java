import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import proxy.ProxyPool;
import util.JsoupUtil;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by Cabeza on 2016-09-11.
 */
public class ProxyPoolTest {
    public static void main(String[] args) {
        Connection con = JsoupUtil.getGetCon("http://www.xicidaili.com/nn/");
        Connection.Response rs = null;
        try {
            rs = con.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document doc = Jsoup.parse(rs.body());
        Elements elmts = doc.select("tr");
        elmts.stream()
                .filter(e -> Pattern.matches("\\d+\\.\\d+\\.\\d+\\.\\d+", e.select("td").eq(1).text()))
                .forEach(e -> ProxyPool.addProxy(new String[]{e.select("td").eq(1).text(), e.select("td").eq(2).text()}));
        ProxyPool.checkProxy();

        /*Connection proxyCon=JsoupUtil.getGetCon("http://httpbin.org/ip").proxy("39.1.46.69",8080).ignoreContentType(true);
        Connection.Response proxyRs= null;
        try {
            proxyRs = proxyCon.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject json=new JSONObject(proxyRs.body());
        System.out.println(json.getString("origin"));*/
    }
}
