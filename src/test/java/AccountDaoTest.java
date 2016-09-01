import dao.AccountDao;
import entity.Account;
import org.junit.Test;
import util.AccountPool;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Cabeza on 2016/9/1.
 */
public class AccountDaoTest {

    @Test
    public void getAccountList() {
        System.out.println(AccountDao.getAccountList());
    }

    @Test
    public void updateAccountTest() {
        Account account = AccountDao.getAccountList().get(0);
        account.setPassword("hahaha");
        account.setState("1");
        AccountDao.updateAccount(account);
    }

    @Test
    public void setCookiesTest() {
        Map<String, String> cookies = new HashMap<>();
        cookies.put("a", "123");
        cookies.put("b", "789");
        cookies.put("c", "456");
        Account a = new Account();
        a.setCookies(cookies);
        System.out.println(a);
    }

    @Test
    public void setCookieStrTest() {
        String cookieStr = "a=123;b=789;c=456";
        Account a = new Account();
        a.setCookieStr(cookieStr);
        System.out.println(a);
    }

}
