package com.qmkf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qmkf.common.CustomException;
import com.qmkf.dao.DishDao;
import com.qmkf.domain.*;
import com.qmkf.service.DishFlavorService;
import com.qmkf.service.DishService;
import dto.DishDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Author：qm
 *
 * @Description：
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishDao, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    //新增菜品同时保存对应的口味数据
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        Long id = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
           item.setDishId(id);
           return item;
        }).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {

        //查询菜品基本信息
        Dish dish = this.getById(id);
        DishDto dishDto= new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询当前菜品对应的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表
        this.updateById(dishDto);

        //清理当前菜品对应口味数据
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //插入新的数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }


    //起售与停售
    @Override
    @Transactional
    public void stateUpdate(Integer state, List<Long> ids) {
        List<Dish> dishList = ids.stream().map((id) -> {
            Dish dish = this.getById(id);
            if (state != 0) {
                dish.setStatus(1);
            } else {
                dish.setStatus(0);
            }
            return dish;
        }).collect(Collectors.toList());
        this.updateBatchById(dishList);
    }

    @Override
    @Transactional
    public void removeWithFlavor(List<Long> ids) {
        //判断菜品状态，起售无法删除
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        queryWrapper.eq(Dish::getStatus,1);

        long count = this.count(queryWrapper);
        if(count>0){
            throw new CustomException("菜品正在售卖中，删除菜品失败！");
        }

        //删除菜品（1）
//        List<Dish> dishList = ids.stream().map((id)->{
//            Dish dish = this.getById(id);
//            return dish;
//        }).collect(Collectors.toList());
//        this.removeBatchByIds(dishList);

        //删除菜品（2）
        this.removeByIds(ids);

//        删除口味（测试版本，尚未开发成功）
//        List<DishFlavor> dishFlavorList = ids.stream().map((id)->{
//            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
//            queryWrapper.eq(DishFlavor::getDishId,id);
//            List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper);
//
//        }).collect(Collectors.toList());

        //删除口味(1)
//        for (Long id: ids
//        ) {
//            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
//            queryWrapper1.eq(DishFlavor::getDishId,id);
//            List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper1);
//            dishFlavorService.removeBatchByIds(dishFlavorList);
//        }

        //删除口味（2）
        LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(queryWrapper1);

        //删除图片（等待开发）
    }
}
