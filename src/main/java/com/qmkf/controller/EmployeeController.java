package com.qmkf.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmkf.domain.Employee;
import com.qmkf.domain.Result;
import com.qmkf.service.EmployeeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * Author：qm
 *
 * @Description：
 */

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //1.将页面提交过来的密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
//        System.out.println(emp);

        //3.如果没有查到返回登录失败
        if(emp == null) {
            return Result.error("该用户名未注册，登录失败！");
        }

        //4.进行密码比对，如果不相同返回登录失败
        if(!emp.getPassword().equals(password)){
            return Result.error("密码错误，登录失败！");
        }

        //5.查看员工账户的状态，如果已禁用，则返回返回员工已禁用结果
        if(emp.getStatus() != 1){
            return Result.error("账号已禁用，无法进行登录！");
        }

        //6.登录成功，将员工id存入Session并响应登录结果
        request.getSession().setAttribute("employee",emp.getId());
        return Result.success(emp);

    }

    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");

        return Result.success("退出成功！");
    }

    @PostMapping
    public Result<String> save(HttpServletRequest request, @RequestBody Employee employee){
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        Long aLong = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(aLong);
//        employee.setUpdateUser(aLong);

        boolean save = employeeService.save(employee);
        return save == true? Result.success("添加成功！"):Result.error("添加失败！");
    }

    @GetMapping("/page")
    public Result<Page> pageShow(Integer page, @RequestParam("pageSize")Integer size, String name) {
        Page page1 = new Page(page,size);

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        employeeService.page(page1, queryWrapper);

        return Result.success(page1);
    }

    @PutMapping
    public Result<String> update(HttpServletRequest request,@RequestBody Employee employee){

//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser((Long)request.getSession().getAttribute("employee"));
        employeeService.updateById(employee);
        return Result.success("员工转态修改成功");
    }

    @GetMapping("/{id}")
    public Result<Employee> findById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        return employee != null ? Result.success(employee) : Result.error("查询失败！");
    }
}
