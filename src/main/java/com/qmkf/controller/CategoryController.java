package com.qmkf.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmkf.domain.Category;
import com.qmkf.domain.Result;
import com.qmkf.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author：qm
 *
 * @Description：
 */

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Result<String> save(@RequestBody Category category) {
        boolean save = categoryService.save(category);

        return save != true ? Result.error("添加失败！") : Result.success("添加成功！");
    }

    @GetMapping("/page")
    public Result<Page> showPage(Integer page, @RequestParam("pageSize") Integer size) {
        Page<Category> categoryPage = new Page<>(page, size);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(categoryPage, queryWrapper);
        return Result.success(categoryPage);
    }

    @DeleteMapping
    public Result<String> delete(Long id) {
//        boolean remove = categoryService.removeById(id);

        categoryService.remove(id);
        return Result.success("删除成功！");
    }

    @PutMapping
    public Result<String> update(@RequestBody Category category) {
        boolean flag = categoryService.updateById(category);
        return flag != true ? Result.error("修改失败！") : Result.success("修改成功！");
    }

    @GetMapping("/list")
    public Result<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return Result.success(list);
    }


}
