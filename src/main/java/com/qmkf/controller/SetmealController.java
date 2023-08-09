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
import org.springframework.web.bind.annotation.*;

import java.util.List;
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


    //新增套餐
    @PostMapping
    public Result<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
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
        return Result.success(null, "套餐信息更新成功！");
    }

    @GetMapping("/list")
    public Result<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = setmealService.list(queryWrapper);
        return Result.success(setmealList);
    }
}
