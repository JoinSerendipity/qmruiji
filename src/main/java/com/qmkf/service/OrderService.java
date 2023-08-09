package com.qmkf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qmkf.domain.Orders;

/**
 * Author：qm
 *
 * @Description：
 */
public interface OrderService extends IService<Orders> {

    //用户下单
    void submit(Orders orders);
}
