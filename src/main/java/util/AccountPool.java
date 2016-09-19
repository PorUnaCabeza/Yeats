package util;

import com.github.bingoohuang.utils.codec.*;
import dao.AccountDao;
import entity.Account;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Cabeza on 2016/9/1.
 */
public class AccountPool {
    private static Logger log = LoggerFactory.getLogger(AccountPool.class);

    private static List<Account> accountList;

    private static Scanner sc = new Scanner(System.in);

    private static AtomicInteger index = new AtomicInteger(0);

    static {
        log.info("检测登录...");
        accountList = AccountDao.getAccountList();
        accountList.stream().filter(a -> !a.getState().equals("-1")).forEach(a -> login(a));
        log.info("成功登录" + accountList.size() + "个账号");
    }

    public AccountPool() {

    }

    public static Account getAccount() {
        Account account = accountList.get(index.getAndIncrement());
        index.compareAndSet(accountList.size(), 0);
        return account;
    }

    private static void login(Account account) {
        int count = 0;
        while (!loginBySavedCookies(account) && !account.isLogin() && count < 3) {
            count++;
        }
        count = 0;
        while (!account.isLogin() && !loginByPassword(account) && count < 3) {
            count++;
        }
        if (!account.isLogin()) {
            log.info(account.getUserName() + "账号存在问题，请手动检测");
            account.setState("-1");
            AccountDao.updateAccount(account);
        }
    }


    private static boolean checkLogin(Account account) {
        Connection con = JsoupUtil.getGetCon(Config.getValue("checkLoginUrl"));
        try {
            Connection.Response rs = con.cookies(account.getCookies()).execute();
            if (rs.body().indexOf("\"isLogin\":true") > 0) {
                log.info(account.getUserName() + "使用保存的cookie登录成功,状态码" + rs.statusCode());
                YeatsUtil.jedisLog(account.getUserName() + "使用保存的cookie登录成功,状态码" + rs.statusCode());
                account.setLogin(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return account.isLogin();
    }

    private static boolean loginBySavedCookies(Account account) {
        if (account.getCookies() == null) {
            log.info("读取的cookie为空");
            return false;
        }
        return checkLogin(account);
    }

    private static boolean loginByPassword(Account account) {
        log.info(account.getUserName() + "进行账号密码登录...");
        Boolean needCaptcha = checkCaptcha(account);
        String pcid = null;
        String captchaContent = null;
        if (needCaptcha) {
            pcid = getCaptcha();
            System.out.println(account.getUserName() + "登录需要验证码,请输入验证码");
            captchaContent = sc.nextLine();
        }
        Connection con = JsoupUtil.getPostCon(Config.getValue("loginUrl"));
        Connection.Response rs = null;
        try {
            con.data("username", account.getUserName());
            con.data("password", account.getPassword());
            con.data("savestate", "1");
            con.data("ec", "0");
            con.data("entry", "mweibo");
            if (needCaptcha) {
                con.data("pincode", captchaContent);
                con.data("pcid", pcid);
            }
            con.referrer("https://passport.weibo.cn/signin/login");
            rs = con.execute();
            JSONObject jsonObject = new JSONObject(rs.body());
            String retCode = jsonObject.get("retcode").toString();
            if (retCode.equals("20000000")) {
                log.info(account.getUserName() + "帐号密码登录成功");
                account.setLogin(true);
                account.setCookies(rs.cookies());
                account.setState("1");
                AccountDao.updateAccount(account);
            } else {
                log.info(account.getUserName() + "账号密码登录失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return account.isLogin();
    }

    private static boolean checkCaptcha(Account account) {
        String base64Str = null;
        try {
            base64Str = com.github.bingoohuang.utils.codec.Base64.base64(URLEncoder.encode(account.getUserName(), "utf8"));
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
        if (preLoginJson.getInt("showpin") == 0)
            return false;
        return true;
    }

    private static String getCaptcha() {
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
        return pcid;
    }

    public static void main(String[] args) {
        new AccountPool();
    }

}
