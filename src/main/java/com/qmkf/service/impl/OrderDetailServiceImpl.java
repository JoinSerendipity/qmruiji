package com.qmkf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qmkf.dao.OrderDetailDao;
import com.qmkf.domain.OrderDetail;
import com.qmkf.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * Author：qm
 *
 * @Description：
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailDao, OrderDetail> implements OrderDetailService {
}
