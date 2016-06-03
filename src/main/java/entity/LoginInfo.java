package entity;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Config;
import util.JsoupUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Cabeza on 2016-06-03.
 */
public class LoginInfo {
    private static Logger log= LoggerFactory.getLogger(LoginInfo.class);
    private Map<String,String> loginCookies=new HashMap<>();
    private Boolean isLogin=false;

    public static void main(String[] args) {
        new LoginInfo().login();
    }

    public void login(){
        int count=0;
        while(!loginBySavedCookies()&&!isLogin&&count<3){
            count++;
        }
        count=0;
        while(!isLogin&&!loginByPassword()&&count<3){
            count++;
        }
    }
    public boolean loginBySavedCookies(){
        Map<String,String> cookies=null;
        cookies=LoginInfo.readCookies(Config.getValue("cookiesFilePath"));
        if(cookies==null){
            log.info("读取的cookie为空");
            return false;
        }
        return checkLogin(cookies);
    }


    public boolean checkLogin(Map<String,String> cookies){
        Connection con=JsoupUtil.getGetCon(Config.getValue("checkLoginUrl"));
        try {
            Response rs=con.cookies(cookies).execute();
            String str=rs.body();
            if(str.indexOf("\"isLogin\":true")>0){
                log.info("登录成功");
                loginCookies.clear();
                loginCookies.putAll(cookies);
                isLogin=true;
                return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  false;
    }

    public boolean loginByPassword(){
        Scanner sc=new Scanner(System.in);
        System.out.println("请输入账号");
        String username=sc.nextLine();
        System.out.println("请输入密码");
        String password=sc.nextLine();
        Connection con= JsoupUtil.getPostCon(Config.getValue("loginUrl"));
        Response rs=null;
        try {
            con.data("username",username);
            con.data("password",password);
            con.data("savestate","1");
            con.data("ec","0");
            con.data("entry","mweibo");
            con.referrer("https://passport.weibo.cn/signin/login");
            rs=con.execute();
            JSONObject jsonObject=new JSONObject(rs.body());
            String retCode=jsonObject.get("retcode").toString();
            if(retCode.equals("20000000")){
                log.info("登录成功");
                isLogin=true;
                saveCookies(Config.getValue("cookiesFilePath"), rs.cookies());
                loginCookies.clear();
                loginCookies.putAll(rs.cookies());
                return true;
            }else{
                log.info("登录失败");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void saveCookies(String fileName, Map<String, String> cookies) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedOutputStream bos
                = new BufferedOutputStream(fos);
        PrintWriter pw = new PrintWriter(bos);
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            pw.println(entry.getKey() + "=" + entry.getValue().replace("\"", ""));
        }
        pw.close();
        log.info("cookies已保存");
    }
    public static Map<String,String> readCookies(String filename) {
        Map<String, String> cookies=new HashMap<>();
        if (!new File(filename).exists()) {
            log.info(filename + "不存在");
            return null;
        }
        try {
            FileInputStream fis
                    = new FileInputStream(filename);
            InputStreamReader isr
                    = new InputStreamReader(fis);
            BufferedReader br
                    = new BufferedReader(isr);
            String str = null;
            while ((str = br.readLine()) != null) {
                int index = str.indexOf("=");
                cookies.put(
                        str.substring(0, index),
                        str.substring(index + 1, str.length())
                );
            }
            return cookies;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, String> getLoginCookies() {
        return loginCookies;
    }

    public void setLoginCookies(Map<String, String> loginCookies) {
        this.loginCookies = loginCookies;
    }

    public Boolean getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(Boolean isLogin) {
        this.isLogin = isLogin;
    }
}
