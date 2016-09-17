package thread;

import entity.Comment;
import org.apache.http.HttpHost;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proxy.Proxy;
import proxy.ProxyPool;
import util.AccountPool;
import util.Config;
import util.JsoupUtil;

import java.io.IOException;
import java.util.Map;

/**
 * Created by zhangxs on 2016/9/8.
 */
public class CommentTask implements Runnable {
    private static Logger log = LoggerFactory.getLogger(CommentTask.class);
    private String mid;
    private String page;
    private String compareUserId;
    private Map cookies;
    private boolean success = false;

    public CommentTask(String mid, String page, Map cookies, String compareUserId) {
        this.mid = mid;
        this.page = page;
        this.cookies = cookies;
        this.compareUserId = compareUserId;
    }

    @Override
    public void run() {
        int i = 0;
        while (i < 5) {
            if (success = getCommentList()) break;
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }
        if (!success) {
            log.info("五次均未成功");
        }
    }

    public boolean getCommentList() {
        String url = Config.getValue("commentUrl").replaceAll("#mid#", mid).replaceAll("#page#", page);
        Connection con = JsoupUtil.getGetCon(url)
                .cookies(cookies)
                .ignoreContentType(true)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36")
                .header("Upgrade-Insecure-Requests", "1");
        Connection.Response rs = null;
        HttpHost httpHost = ProxyPool.getProxy();
        try {
            rs = con
                    .proxy(httpHost.getHostName(), httpHost.getPort())
                    .execute();
            ProxyPool.returnProxy(httpHost, rs.statusCode());
            JSONArray resultList = new JSONArray(rs.body());
            for (int i = 0; i < resultList.length(); i++) {
                JSONObject mod = resultList.getJSONObject(i);
                if (mod.getString("mod_type").equals("mod/pagelist")) {
                    JSONArray comments = mod.getJSONArray("card_group");
                    for (int j = 0; j < comments.length(); j++) {
                        JSONObject commentJson = comments.getJSONObject(j);
                        Comment comment = new Comment();
                        comment.setCommentId(commentJson.get("id").toString());
                        comment.setText(commentJson.getString("text"));
                        comment.setCreateTime(commentJson.getString("created_at"));
                        comment.setUserName(commentJson.getJSONObject("user").getString("screen_name"));
                        comment.setUserId(commentJson.getJSONObject("user").get("id").toString());
                        System.out.println(comment);
                        if (compareUserId != null && compareUserId.equals(comment.getUserId()))
                            log.info(comment.toString());
                    }
                }
            }
            return true;
        } catch (Exception e) {
            ProxyPool.returnProxy(httpHost, Proxy.ERROR_403);
            e.printStackTrace();
            return false;
        }
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getCompareUserId() {
        return compareUserId;
    }

    public void setCompareUserId(String compareUserId) {
        this.compareUserId = compareUserId;
    }

    public static void main(String[] args) {
        new CommentTask("3990971530582668", "32", AccountPool.getAccount().getCookies(), "").run();
    }
}
