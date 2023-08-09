package com.qmkf.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmkf.domain.Category;
import com.qmkf.domain.Dish;
import com.qmkf.domain.DishFlavor;
import com.qmkf.domain.Result;
import com.qmkf.service.CategoryService;
import com.qmkf.service.DishFlavorService;
import com.qmkf.service.DishService;
import dto.DishDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author：qm
 *
 * @Description：
 */

@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto) {

        dishService.saveWithFlavor(dishDto);

        return Result.success("添加菜品成功！");
    }

    //菜品信息分页
    @GetMapping("/page")
    public Result<Page> showPage(Integer page, @RequestParam("pageSize") Integer size, String name) {
        Page<Dish> pageInfo = new Page<>(page, size);
        Page<DishDto> dtoPageInfo = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, queryWrapper);

        //将page中的数据拷贝到dtoPageInfo.除了records中的数据
        BeanUtils.copyProperties(pageInfo, dtoPageInfo, "records");

        //得到分页查询的数据
        List<Dish> records = pageInfo.getRecords();

        //遍历records
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);//将从records中得到的数据item拷贝到dishDto
            Long categoryId = item.getCategoryId();//得到当前item（DishDto）的CategoryId

            //根据Id查询对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());//转成List集合

        //将处理好的records放到dtoPageInfo中
        dtoPageInfo.setRecords(list);

        return Result.success(dtoPageInfo);
    }

    @GetMapping("/{id}")
    public Result<DishDto> findId(@PathVariable Long id) {

        DishDto byIdWithFlavor = dishService.getByIdWithFlavor(id);
        return Result.success(byIdWithFlavor);
    }

    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return Result.success(null, "修改成功！");
    }

//    @PostMapping("/status/{state}")
//    @Transactional
//    public Result<String> stateInfo(@PathVariable Integer state, @RequestParam Long[] ids) {
//
//        if (state == 1) {
//            for (Long id :
//                    ids) {
//                Dish dish = dishService.getById(id);
//                dish.setStatus(1);
//                dishService.updateById(dish);
//            }
//        }
//
//        if (state == 0) {
//            for (Long id :
//                    ids) {
//                Dish dish = dishService.getById(id);
//                dish.setStatus(0);
//                dishService.updateById(dish);
//            }
//        }
//
//        return Result.success(null, "修改成功");
//    }

    //停售与起售
    @PostMapping("/status/{state}")
    public Result<String> stateInfo(@PathVariable Integer state, @RequestParam List<Long> ids) {
        dishService.stateUpdate(state,ids);
        return Result.success(null, "菜品状态修改成功！");
    }

//    @DeleteMapping
//    public Result<String> delete(@RequestParam Long[] ids) {
//        for (Long id : ids
//        ) {
//            Dish dish = dishService.getById(id);
//            dishService.removeById(dish);
//        }
//        return Result.success(null, "删除成功！");
//    }

    //删除菜品以及口味
    @DeleteMapping
    public Result<String> delete(@RequestParam List<Long> ids) {
        dishService.removeWithFlavor(ids);
       return Result.success(null,"删除菜品成功！");
    }

    //菜品分类中的菜品显示
//    @GetMapping("/list")
//    public Result<List<Dish>> list(Dish dish){
//        LambdaQueryWrapper<Dish> queryWrapper =new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
//        queryWrapper.eq(Dish::getStatus,1);
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);
//        return Result.success(list);
//    }

    //菜品分类中的菜品显示
    @GetMapping("/list")
    public Result<List<DishDto>> list(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();//得到当前item（DishDto）的CategoryId

            //根据Id查询对象，查询菜品分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);//设置菜品分类名称
            }

            //得到当前菜品的ID
            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,id);
            //得到菜品口味集合
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            //将菜品口味集合放到dishDto中的Flavors
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());


        return Result.success(dishDtoList);
    }
}
