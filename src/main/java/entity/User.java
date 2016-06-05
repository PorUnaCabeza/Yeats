package entity;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Cabeza on 2016-06-03.
 */
public class User {
    private String weiboCount;
    private String fansCount;
    private String followeeCount;

    public User(){

    }

    public User(String str){
        String jsonStr=str.substring(str.indexOf("(")+1,str.lastIndexOf(")"));
        JSONObject jsonObject=new JSONObject(jsonStr);
        JSONArray apps=jsonObject.getJSONArray("apps");
        for(int i=0;i<apps.length();i++){
            if(apps.getJSONObject(i).getString("title").equals("粉丝")){
                setFansCount(apps.getJSONObject(i).getString("count"));
            }
            if(apps.getJSONObject(i).getString("title").equals("关注")){
                setFolloweeCount(apps.getJSONObject(i).getString("count"));
            }
            if(apps.getJSONObject(i).getString("title").equals("微博")){
                setWeiboCount(apps.getJSONObject(i).getString("count"));
            }
        }
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
                "weiboCount='" + weiboCount + '\'' +
                ", fansCount='" + fansCount + '\'' +
                ", followeeCount='" + followeeCount + '\'' +
                '}';
    }
}
