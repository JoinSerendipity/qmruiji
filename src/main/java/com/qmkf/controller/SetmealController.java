package com.qmkf.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmkf.domain.Category;
import com.qmkf.domain.Result;
import com.qmkf.domain.Setmeal;
import com.qmkf.service.CategoryService;
import com.qmkf.service.SetmealDishService;
import com.qmkf.service.SetmealService;
import dto.DishDto;
import dto.SetmealDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Author：qm
 *
 * @Description：
 */

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;


    //新增套餐
    @PostMapping
    public Result<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        //清除指定套餐缓存数据
        String key = "setmeal_" +setmealDto.getCategoryId()+"_"+setmealDto.getStatus();
        redisTemplate.delete(key);
        return Result.success(null, "添加套餐成功！");
    }

    @GetMapping("/page")
    public Result<Page> showPage(Integer page, @RequestParam("pageSize") Integer size, String name) {
        Page<Setmeal> pageInfo = new Page<>(page, size);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> setmealDtos = records.stream().map((item) -> {

            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);

            //得到分类ID
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(setmealDtos);

        return Result.success(dtoPage);
    }


    //删除套餐
    @DeleteMapping
    public Result<String> delete(@RequestParam List<Long> ids) {
        setmealService.removeWithDish(ids);
        return Result.success(null, "套餐删除成功！");
    }


    //套餐停售与起售
    @PostMapping("/status/{state}")
    public Result<String> stateInfo(@PathVariable Integer state, @RequestParam List<Long> ids) {
        setmealService.stateUpdate(state, ids);
        return Result.success(null, "套餐状态修改成功");
    }

    //根据ID查询套餐及菜品信息
    @GetMapping("/{id}")
    public Result<SetmealDto> findId(@PathVariable Long id) {
        SetmealDto byIdWithDish = setmealService.getByIdWithDish(id);
        return Result.success(byIdWithDish);
    }

    //修改套餐以及菜品
    @PutMapping
    public Result<String> update(@RequestBody SetmealDto setmealDto) {

        setmealService.updateWithDish(setmealDto);
        //清除指定套餐缓存数据
        String key = "setmeal_" +setmealDto.getCategoryId()+"_"+setmealDto.getStatus();
        redisTemplate.delete(key);

        return Result.success(null, "套餐信息更新成功！");
    }

    @GetMapping("/list")
    public Result<List<Setmeal>> list(Setmeal setmeal) {

        List<Setmeal> setmealList = null;

        //动态构造key
        String key = "setmeal_" + setmeal.getCategoryId() + "_" + setmeal.getStatus();

        //从Redis中获取缓存数据
        setmealList = (List<Setmeal>) redisTemplate.opsForValue().get(key);

        if(setmealList !=null) {
            //如果存在直接返回，无需查询数据库
            return Result.success(setmealList);
        }

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealList = setmealService.list(queryWrapper);

        //如果不存在，则需要查询数据库，将查询到的数据缓存到Redis中
        redisTemplate.opsForValue().set(key,setmealList,30, TimeUnit.MINUTES);

        return Result.success(setmealList);
    }
}
