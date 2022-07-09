package com.example.user.mail;

import com.example.user.utils.FormatChecker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * @author monody
 * @date 2022-04-23 下午2:02
 */
@Component
@RefreshScope
public class SendEmail {
    @Value("${mail.sender.mailbox.account}")
    private String senderMailBox;
    @Value("${mail.sender.mailbox.nickname}")
    private String senderNickname;
    @Value("${mail.sender.mailbox.secret}")
    private String secret;
    @Value("${mail.smtp.host}")
    private String host;
    @Value("${mail.smtp.auth}")
    private String auth;
    @Value("${mail.verification-code.template}")
    private String template;
    @Value("${mail.verification-code.timeout}")
    private int timeout;


    /**
     * 发送验证码
     * 注意：需要在服务调用端做好发送邮件的「内容、次数」限制
     *
     * @param userMailBox 收件人邮箱
     * @param function    该验证码功能（例如 用户注册 ）
     * @param nickName    用户昵称
     * @param code        验证码
     * @return 错误信息，没有错误则返回null
     */
    public String sendVerificationCode(String userMailBox, String function, String nickName, int code) {
        String text = String.format(template, nickName, userMailBox, code, timeout);
        return send(userMailBox, function, text);
    }

    /**
     * 发送邮件
     *
     * @param receiveMailBox 收件人邮箱
     * @param subject        主题
     * @param text           消息
     * @return 错误信息
     */
    public String send(String receiveMailBox, String subject, String text) {
        FormatChecker.mailCheck(receiveMailBox);
        FormatChecker.blankCheck(subject,text);
        // 获取系统属性
        Properties properties = System.getProperties();
        // 设置邮件服务器
        properties.setProperty("mail.smtp.host", host);
        // 设置邮件验证
        properties.put("mail.smtp.auth", auth);
        // 获取默认session对象
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderMailBox, secret); //发件人邮件用户名、授权码
            }
        });
        try {
            // 创建默认的 MimeMessage 对象
            MimeMessage message = new MimeMessage(session);

            // Set From: 头部头字段
            message.setFrom(new InternetAddress(senderMailBox, senderNickname, "UTF-8"));

            // Set To: 头部头字段
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(receiveMailBox));

            // Set Subject: 头部头字段
            message.setSubject(subject);

            // 设置消息体
            message.setText(text);

            // 发送消息
            Transport.send(message);
            return null;
        } catch (MessagingException | UnsupportedEncodingException mex) {
            mex.printStackTrace();
            return mex.getMessage();
        }
    }

}