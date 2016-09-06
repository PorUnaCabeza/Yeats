package entity;

import java.util.*;

/**
 * Created by Cabeza on 2016/9/1.
 */
public class Account {
    private String userName;
    private String password;
    private String cookieStr;
    private Map<String, String> cookies = new HashMap<>();
    private String state;
    private Date createTime;
    private Date updateTime;
    private boolean isLogin;

    public Account() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCookieStr() {
        return cookieStr;
    }

    public void setCookieStr(String cookieStr) {
        this.cookieStr = cookieStr;
        if (cookieStr == null) return;
        cookies.clear();
        List<String> list = Arrays.asList(cookieStr.split(";"));
        for (String str : list) {
            int index = str.indexOf("=");
            cookies.put(
                    str.substring(0, index),
                    str.substring(index + 1, str.length())
            );
        }
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue().replace("\"", "") + ";");
        }
        this.cookieStr = sb.substring(0, sb.length() - 1);
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    @Override
    public String toString() {
        return "Account{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", cookies=" + cookies +
                ", cookieStr='" + cookieStr + '\'' +
                ", state='" + state + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", isLogin=" + isLogin +
                '}';
    }
}
