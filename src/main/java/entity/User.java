package entity;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Cabeza on 2016-06-03.
 */
public class User {
    private String id;
    private String name;
    private String desc;
    private String weiboCount;
    private String fansCount;
    private String followeeCount;

    public User() {

    }

    public User(String str) {
        String jsonStr = str.replaceAll(".*?window\\.\\$render_data\\s*=\\s*(.*?[\\s\\S]*\\});.*", "$1");
        JSONObject jsonObject = new JSONObject(jsonStr);
        JSONArray page = jsonObject.getJSONObject("stage").getJSONArray("page");
        JSONObject data = page.getJSONObject(1);
        setId(data.getString("id"));
        setName(data.getString("name"));
        setDesc(data.getString("description"));
        setFansCount(data.getString("fansNum"));
        setFolloweeCount(data.getString("attNum"));
        setWeiboCount(data.getString("mblogNum"));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getWeiboCount() {
        return weiboCount;
    }

    public void setWeiboCount(String weiboCount) {
        this.weiboCount = weiboCount;
    }

    public String getFansCount() {
        return fansCount;
    }

    public void setFansCount(String fansCount) {
        this.fansCount = fansCount;
    }

    public String getFolloweeCount() {
        return followeeCount;
    }

    public void setFolloweeCount(String followeeCount) {
        this.followeeCount = followeeCount;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", weiboCount='" + weiboCount + '\'' +
                ", fansCount='" + fansCount + '\'' +
                ", followeeCount='" + followeeCount + '\'' +
                '}';
    }
}
