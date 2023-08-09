package com.qmkf.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qmkf.domain.Result;
import com.qmkf.domain.User;
import com.qmkf.service.SendMailService;
import com.qmkf.service.UserService;
import com.qmkf.utils.BaseContext;
import com.qmkf.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Author：qm
 *
 * @Description：
 */

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SendMailService sendMailService;

    //发送人
    @Value("${spring.mail.username}")
    private String from;

    //接收人
    private String to;

    //标题
    private String title = "瑞吉外卖登录验证码：";

    //正文
    private String context;

    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpSession session) {

        //获取邮箱号
        String phone = user.getPhone();
        if (StringUtils.isNoneEmpty(phone)) {
            to = phone + "@qq.com";
            to = "1685771351@qq.com";
            //生成随机的四位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}", code);
            context = "您的验证码为：" + code;
            //调用SendMailService发送邮件
//            sendMailService.sendMail(from,to,title,context);

            //将生成的验证码保存到Session中
            session.setAttribute(phone, code);

            Result.success(null, "邮箱验证码发送成功！");
        }
        return Result.error("邮箱验证码发送失败！");
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody Map map, HttpSession session) {

        log.info(map.toString());

        //获取手机号
        String phone = map.get("phone").toString();

        //获取验证码
        String code = map.get("code").toString();

        //从Session中获取保存的验证码
        Object codeInSession = session.getAttribute(phone);

        //进行验证码的比对（页面的验证码和Session中的验证码比对）
        if (codeInSession != null && codeInSession.equals(code)) {
            //如果比对成功，则登录成功


            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);

            if (user == null) {
                //判断当前手机号对应的用户是否为新用户，如果是新用户自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return Result.success(user);
        }

        return Result.error("登录失败！");
    }


}