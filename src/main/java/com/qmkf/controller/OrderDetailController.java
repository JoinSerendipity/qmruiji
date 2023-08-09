package com.qmkf.controller;

import com.qmkf.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author：qm
 *
 * @Description：
 */
@RestController
@RequestMapping("/")
public class OrderDetailController {

    @Autowired
    private OrderDetailService detailService;


}
