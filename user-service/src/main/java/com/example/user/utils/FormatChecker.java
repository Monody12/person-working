package com.example.user.utils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author monody
 * @date 2022/4/23 2:39 下午
 */
public class FormatChecker {


    /**
     * 检测一些对象中是否包含null或空字符串
     * 若发现为null或者包含空字符串就抛出异常
     *
     * @param obj 一些对象
     * @throws NullPointerException     对象不能为null
     * @throws IllegalArgumentException 字符串不能为空
     */
    public static void blankCheck(Object... obj) {
        for (Object o : obj) {
            Objects.requireNonNull(o, "对象不能为null");
            if (o instanceof String) {
                String s = (String) o;
                if (s.length() == 0) {
                    throw new IllegalArgumentException("字符串不能为空");
                }
            }
        }
    }

    public static void mailCheck(String mail) {
        String pattern = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(mail);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }
    }

    public static boolean usernameCheck(String username) {
        String pattern = "^\\w{4,18}$";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(username);
        return m.matches();
    }

    public static boolean passwordCheck(String password) {
        String pattern = "^[A-Za-z0-9]{4,8}$";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(password);
        return m.matches();
    }

    public static boolean nicknameCheck(String nickname) {
        String pattern = "^[\\u4E00-\\u9FA5A-Za-z0-9]{1,15}$";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(nickname);
        return m.matches();
    }

    public static void main(String[] args) {
        mailCheck("qq614908309@gm@ail.com");
    }

}
