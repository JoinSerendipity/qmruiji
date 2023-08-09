package com.qmkf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qmkf.domain.Dish;
import dto.DishDto;

import java.util.List;

/**
 * Author：qm
 *
 * @Description：
 */
public interface DishService extends IService<Dish> {

    void saveWithFlavor(DishDto dishDto);

    DishDto getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);

    void stateUpdate(Integer state, List<Long> ids);

    void removeWithFlavor(List<Long> ids);
}
