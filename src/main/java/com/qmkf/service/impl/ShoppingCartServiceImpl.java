package com.qmkf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qmkf.dao.ShoppingCartDao;
import com.qmkf.domain.ShoppingCart;
import com.qmkf.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * Author：qm
 *
 * @Description：
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartDao, ShoppingCart> implements ShoppingCartService {
}
