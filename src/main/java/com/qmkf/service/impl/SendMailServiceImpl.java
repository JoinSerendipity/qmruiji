package com.qmkf.service.impl;

import com.qmkf.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

/**
 * Author：qm
 *
 * @Description：
 */
@Service
public class SendMailServiceImpl implements SendMailService {
    @Autowired
    private JavaMailSender javaMailSender;

    //发送人
//    private String from = "";
//
//    //接收人
//    private String to = "";
//
//    //标题
//    private String title = "";
//
//    //正文
//    private String context = "";

    @Override
    public void sendMail(String from,String to,String title,String context) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();//简单类型邮件
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(title);
        simpleMailMessage.setText(context);
//        MimeMessage mimeMessage = javaMailSender.createMimeMessage();//复杂类型邮件
//        MimeMessageHelper mimeMessageHelper =new MimeMessageHelper(mimeMessage);
        javaMailSender.send(simpleMailMessage);
    }
}
