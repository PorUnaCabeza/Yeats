import main.Main;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.jsoup.Connection;
import util.JsoupUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Cabeza on 2016-06-09.
 */
public class WanmeiPets {
    private static Logger log=Logger.getLogger(Main.class);
    private static Random random=new Random();
    private static String userAgent="Mozilla/5.0 (iPhone; CPU iPhone OS 9_3_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Mobile/13E238 MicroMessenger/6.3.15 NetType/WIFI Language/zh_CN";
    private static Map<String,String> zhangCookies=new HashMap<>();
    private static Map<String,String> xuCookies=new HashMap<>();
    private static Map<String,String> yaoCookies=new HashMap<>();
    private static int index=0;
    public WanmeiPets(){
        loadCookies("wanmei.properties",zhangCookies);
        loadCookies("xu.properties",xuCookies);
        loadCookies("yao.properties",yaoCookies);
    }
    public static void main(String[] args) {
        WanmeiPets main=new WanmeiPets();
        while(true){
            try {
                if(index%3==0){
                    System.out.println("刷张的");
                    main.shuachongwu(zhangCookies);
                }
                else if(index%3==1){
                    System.out.println("刷徐的");
                    main.shuachongwu(xuCookies);
                }else if(index%3==2){
                    System.out.println("刷姚的");
                    main.shuachongwu(yaoCookies);
                }
                index++;
                if(index>30)
                    index=0;
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void shuachongwu(Map<String,String> cookies){
        fumo(cookies);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dasao(cookies);
    }

    //抚摸
    public void fumo(Map<String,String> cookies){
        String url="http://event.wanmei.com/m/w2i/pethatch/act?action=hand&r=0."+random.nextInt(10000)+""+System.currentTimeMillis();
        Connection con= JsoupUtil.getGetCon(url).ignoreContentType(true).userAgent(userAgent);
        Connection.Response rs=null;
        try {
            rs=con.cookies(cookies).execute();
            JSONObject jsonObject=new JSONObject(rs.body());
            if(jsonObject.getString("message").indexOf("领取")>0){
                lingyang(cookies);
            }
            if(jsonObject.getBoolean("success")){
                log.info("成功！返回值："+jsonObject.get("message"));
            }else{
                log.info("失败，30分钟限制");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //打扫
    public void dasao(Map<String,String> cookies){
        String url="http://event.wanmei.com/m/w2i/pethatch/act?action=sao&r=0."+random.nextInt(10000)+""+System.currentTimeMillis();
        Connection con= JsoupUtil.getGetCon(url).ignoreContentType(true).userAgent(userAgent);
        Connection.Response rs=null;
        try {
            rs=con.cookies(cookies).execute();
            JSONObject jsonObject=new JSONObject(rs.body());
            System.out.println("打扫:"+jsonObject.getBoolean("success"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //领养
    public void lingyang(Map<String,String> cookies){
        String url="http://event.wanmei.com/m/w2i/pethatch/train";
        Connection con=JsoupUtil.getGetCon(url)
                .ignoreContentType(true)
                .userAgent(userAgent)
                .referrer("http://event.wanmei.com/m/w2i/pethatch/get");
        Connection.Response rs=null;
        try {
            rs=con.cookies(cookies).execute();
            log.info("领养宠物");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadCookies(String fileName,Map<String,String> cookies){
        try {
            ClassLoader loader = Main.class.getClassLoader();
            InputStream in = loader.getResourceAsStream(fileName);
            InputStreamReader isr
                    = new InputStreamReader(in);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
