package test.simple;

import com.example.user.utils.FormatChecker;
import com.example.user.utils.SnowflakeIdWorker;

/**
 * @author monody
 * @date 2022/4/23 1:02 下午
 */
public class SimpleTest {

    public static void main(String[] args) {
        FormatChecker.blankCheck(null,"assa","");
        FormatChecker.mailCheck("6149");

    }
}
