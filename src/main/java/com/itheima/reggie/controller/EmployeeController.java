package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    //员工端登录
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest servletRequest, @RequestBody Employee employee) {

        //把页面返回的明文密码md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //根据页面提交的username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee employee1 = employeeService.getOne(queryWrapper);

        //判断查询结果, 若空则返回失败
        if (employee1 == null) {
            return R.error("用户名不存在, 登录失败");
        }

        //对比数据库的密码和前台加密后的密码, 若不一致则返回失败
        if (!employee1.getPassword().equals(password)) {
            return R.error("密码错误, 登录失败");
        }

        //查看员工状态是否禁用
        if (employee1.getStatus() == 0) {
            return R.error("账号被禁用!");
        }

        //登录成功, 把员工id存入session并返回登录结果
        servletRequest.getSession().setAttribute("employee", employee1.getId());
        return R.success(employee1);
    }

    //员工退出
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest servletRequest) {

        //清理session中保存的当前登录员工id
        servletRequest.getSession().removeAttribute("employee");

        return R.success("退出成功");
    }


    @PostMapping
    public R<String> save(HttpServletRequest httpServletRequest, @RequestBody Employee employee) {
        log.info("新增员工信息: {}", employee);

        //初始密码123456, MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //账号创建时间, 更新时间, 创建者, 更新者
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        Long id = (Long) httpServletRequest.getSession().getAttribute("employee");
        employee.setCreateUser(id);
        employee.setUpdateUser(id);

        log.info("新增员工信息: {}", employee);

        //Mybatis plus 自带的save方法
        employeeService.save(employee);
        return R.success("添加成功");
    }


    //分页查询员工信息
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);

        Page pageInfo = new Page(page, pageSize);

        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Employee::getCreateTime);
        //执行查询
        employeeService.page(pageInfo, lambdaQueryWrapper);

        return R.success(pageInfo);
    }


    //根据id更新员工信息
    @PutMapping
    public R<String> update(@RequestBody Employee employee, HttpServletRequest httpServletRequest) {
        log.info(employee.toString());
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser( (Long)httpServletRequest.getSession().getAttribute("employee"));
        employeeService.updateById(employee);
        return R.success("社員情報が変更されました");
    }


}
