package dao;

import entity.Account;
import org.n3r.eql.Eql;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Cabeza on 2016/9/1.
 */
public class AccountDao {
    private static Logger log= LoggerFactory.getLogger(AccountDao.class);

    public static List<Account> getAccountList(){
        return new Eql().returnType(Account.class).execute();
    }

    public static void updateAccount(Account account){
        new Eql().params(account).execute();
    }
}
