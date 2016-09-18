package yeatsapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by Cabeza on 2016/9/18.
 */
@Service
public class YeatsResourcesService {
    private static Logger log = LoggerFactory.getLogger(YeatsResourcesService.class);

    public void test() {
        try {
            Thread.sleep(5000);
            System.out.println("cabeza over");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
