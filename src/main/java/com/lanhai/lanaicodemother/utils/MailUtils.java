package com.lanhai.lanaicodemother.utils;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * 邮件发送工具类
 */
@Slf4j
@Component
public class MailUtils {

    @Value("${mail.smtp-host}")
    private String smtpHost;

    @Value("${mail.smtp-port}")
    private Integer smtpPort;

    @Value("${mail.from}")
    private String from;

    @Value("${mail.password}")
    private String password;

    @Value("${mail.ssl-enable:true}")
    private Boolean sslEnable;

    /**
     * 生成6位字母数字混合验证码
     *
     * @return 验证码
     */
    public static String generateCode() {
        return RandomUtil.randomString("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", 6);
    }

    /**
     * 发送验证码邮件
     *
     * @param to   收件人邮箱
     * @param code 验证码
     */
    public void sendVerificationCode(String to, String code) {
        String subject = "密码找回验证码";
        String content = "您好，\n\n" +
                "您正在找回密码，验证码为：" + code + "\n\n" +
                "验证码有效期为5分钟，请勿泄露给他人。\n\n" +
                "如非本人操作，请忽略此邮件。\n\n" +
                "此邮件由系统自动发送，请勿回复。";
        try {
            sendMail(to, subject, content);
        } catch (MessagingException e) {
            log.error("发送验证码邮件失败，收件人：{}，错误信息：{}", to, e.getMessage(), e);
            throw new RuntimeException("邮件发送失败", e);
        }
    }

    /**
     * 发送邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容（纯文本格式）
     * @throws MessagingException 邮件发送异常
     */
    private void sendMail(String to, String subject, String content) throws MessagingException {
        // 创建Properties对象
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.auth", "true");
        if (sslEnable) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.port", smtpPort);
        }

        // 创建Session对象
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        // 创建MimeMessage对象
        MimeMessage message = new MimeMessage(session);
        // 设置发件人
        message.setFrom(new InternetAddress(from));
        // 设置收件人
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        // 设置主题
        message.setSubject(subject);
        // 设置内容（纯文本格式）
        message.setText(content, "UTF-8");

        // 发送邮件
        Transport.send(message);
        log.info("邮件发送成功，收件人：{}", to);
    }
}


