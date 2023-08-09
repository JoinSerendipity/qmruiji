package com.qmkf.service;

/**
 * Author：qm
 *
 * @Description：
 */
public interface SendMailService {
    void sendMail(String from,String to,String title,String context);
}
