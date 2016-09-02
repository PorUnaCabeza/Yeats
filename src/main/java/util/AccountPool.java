package util;

import dao.AccountDao;
import entity.Account;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Cabeza on 2016/9/1.
 */
public class AccountPool {
    private static Logger log = LoggerFactory.getLogger(AccountPool.class);

    private static List<Account> accountList;

    static {
        accountList = AccountDao.getAccountList();
        log.info("检测登录...");
        for (Account account : accountList) {
            login(account);
        }
        System.out.println(accountList);
    }

    public AccountPool() {

    }


    private static boolean checkLogin(Account account) {
        Connection con = JsoupUtil.getGetCon(Config.getValue("checkLoginUrl"));
        try {
            Connection.Response rs = con.cookies(account.getCookies()).execute();
            if (rs.body().indexOf("\"isLogin\":true") > 0) {
                log.info(account.getUserName() + "登录成功,状态码" + rs.statusCode());
                account.setLogin(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return account.isLogin();
    }

    private static boolean loginBySavedCookies(Account account) {
        Map<String, String> cookies = null;
        if (account.getCookies() == null) {
            log.info("读取的cookie为空");
            return false;
        }
        return checkLogin(account);
    }

    private static boolean loginByPassword(Account account) {
        Connection con = JsoupUtil.getPostCon(Config.getValue("loginUrl"));
        Connection.Response rs = null;
        try {
            con.data("username", account.getUserName());
            con.data("password", account.getPassword());
            con.data("savestate", "1");
            con.data("ec", "0");
            con.data("entry", "mweibo");
            con.referrer("https://passport.weibo.cn/signin/login");
            rs = con.execute();
            JSONObject jsonObject = new JSONObject(rs.body());
            String retCode = jsonObject.get("retcode").toString();
            if (retCode.equals("20000000")) {
                log.info("登录成功");
                account.setLogin(true);
                account.setCookies(rs.cookies());
                AccountDao.updateAccount(account);
            } else {
                log.info("登录失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return account.isLogin();
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
            account.setState("-1");
            AccountDao.updateAccount(account);
        }
    }


}
