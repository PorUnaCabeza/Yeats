package util;

import org.n3r.diamond.client.Miner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Cabeza on 2016-06-03.
 */
public class Config {
   /* private static Properties pro = new Properties();
    static{
        ClassLoader loader = Config.class.getClassLoader();
        InputStream in = loader.getResourceAsStream("Yeats.properties");
        try {
            pro.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getValue(String key){
        String value = pro.getProperty(key);
        return value;
    }*/

    public static String getValue(String key) {
        return new Miner().getMiner("yeats", "weibo").getString(key);
    }

}
