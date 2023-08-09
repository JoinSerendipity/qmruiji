package com.qmkf.service.impl;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qmkf.common.CustomException;
import com.qmkf.dao.SetmealDao;
import com.qmkf.dao.SetmealDishDao;
import com.qmkf.domain.Dish;
import com.qmkf.domain.DishFlavor;
import com.qmkf.domain.Setmeal;
import com.qmkf.domain.SetmealDish;
import com.qmkf.service.SetmealDishService;
import com.qmkf.service.SetmealService;
import dto.DishDto;
import dto.SetmealDto;
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
public class SetmealServiceImpl extends ServiceImpl<SetmealDao, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) ->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关联关系
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查询套餐的状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        //如果不能删除，抛出一个业务异常
        long count = this.count(queryWrapper);
        if(count>0) {
            throw new CustomException("套餐正在售卖中，不能删除！");
        }

        //先删除套餐表中的数据
        this.removeByIds(list());


        //删除关系表中的数据
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getDishId,ids);

        setmealDishService.remove(queryWrapper1);
    }

    //套餐停售与起售
    @Override
    public void stateUpdate(Integer state,List<Long> ids) {
        List<Setmeal> setmealList = ids.stream().map((id) -> {
            Setmeal setmeal = this.getById(id);
            if (state != 0) {
                setmeal.setStatus(1);
            } else {
                setmeal.setStatus(0);
            }
            return setmeal;
        }).collect(Collectors.toList());
        this.updateBatchById(setmealList);
    }

    //根据ID查询套餐及菜品信息
    @Override
    public SetmealDto getByIdWithDish(Long id) {
            //查询套餐基本信息
            Setmeal setmeal = this.getById(id);
            SetmealDto setmealDto= new SetmealDto();
            BeanUtils.copyProperties(setmeal,setmealDto);

            //查询当前套餐对应的菜品信息
            LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
            List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
            setmealDto.setSetmealDishes(setmealDishes);
            return setmealDto;
    }

    //修改套餐以及菜品
    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        //更新Setmea表
        this.updateById(setmealDto);

        //清理当前套餐对应菜品数据
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        //插入新的数据
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }
}
