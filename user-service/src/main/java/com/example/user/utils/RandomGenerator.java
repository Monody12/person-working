package com.example.user.utils;

import java.util.Random;

/**
 * @author monody
 * @date 2022/4/24 3:41 下午
 */
public class RandomGenerator {

    static Random random = new Random();

    /**
     * 六位数生成器
     * @return 六位整数（x > 100000)
     */
    public static int codeGenerate() {
        int x;
        do {
            x = random.nextInt();
            x = (Math.abs(x) + 123456) % 1000000;
        } while (x <= 100000);
        return x;
    }

}
