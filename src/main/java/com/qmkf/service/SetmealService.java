package com.qmkf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qmkf.domain.Setmeal;
import dto.DishDto;
import dto.SetmealDto;

import java.util.List;

/**
 * Author：qm
 *
 * @Description：
 */
public interface SetmealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);

    //删除套餐和菜品
    void removeWithDish(List<Long> ids);

    //套餐停售与起售
    void stateUpdate(Integer state,List<Long> ids);

    //通过ID查询套餐及菜品信息
    SetmealDto getByIdWithDish(Long id);

    //修改套餐以及菜品
    void updateWithDish(SetmealDto setmealDto);
}
