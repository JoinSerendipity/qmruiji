package com.qmkf.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qmkf.domain.Orders;
import org.apache.ibatis.annotations.Mapper;


/**
 * Author：qm
 *
 * @Description：
 */
@Mapper
public interface OrderDao extends BaseMapper<Orders> {
}
