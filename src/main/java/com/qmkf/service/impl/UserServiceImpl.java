package com.qmkf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qmkf.dao.UserDao;
import com.qmkf.domain.User;
import com.qmkf.service.UserService;
import org.springframework.stereotype.Service;

/**
 * Author：qm
 *
 * @Description：
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {
}
