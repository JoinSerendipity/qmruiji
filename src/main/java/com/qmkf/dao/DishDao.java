package com.qmkf.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qmkf.domain.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * Author：qm
 *
 * @Description：
 */
@Mapper
public interface DishDao extends BaseMapper<Dish> {
}
