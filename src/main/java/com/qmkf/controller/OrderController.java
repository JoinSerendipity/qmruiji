package com.qmkf.controller;

import com.qmkf.domain.Orders;
import com.qmkf.domain.Result;
import com.qmkf.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author：qm
 *
 * @Description：
 */

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    //用户下单
    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return null;
    }
}
