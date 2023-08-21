package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

//菜品管理
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController{
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    //保存菜品
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("dishDTO: {}",dishDto);
        //新增菜品dish, 同时新增口味dish_flavor
        dishService.saveWithFlavor(dishDto);

        return R.success("保存菜品成功");
    }

    //分页查询菜品
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){

        //分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);


        //条件构造器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //添加过滤条件
        lambdaQueryWrapper.like(name!=null,Dish::getName,name);

        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);

        //此时分类中只有分类id, 需要分类名称
        dishService.page(pageInfo, lambdaQueryWrapper);

        //对象属性拷贝
        Page<DishDto> pageDTO = new Page<>();
        BeanUtils.copyProperties(pageInfo,pageDTO,"records");

        List<Dish> records = pageInfo.getRecords();


        //TODO 不懂records.stream().map((item)->{
        List<DishDto> list = records.stream().map((item)->{
                    DishDto dishDto = new DishDto();

                    BeanUtils.copyProperties(item,dishDto);

                    Long categoryId = item.getCategoryId(); //分类id

                    Category category = categoryService.getById(categoryId);

                    if(category!=null){
                        String categoryName = category.getName();
                        dishDto.setCategoryName(categoryName);
                    }
                    return dishDto;
               }).collect(Collectors.toList());

        pageDTO.setRecords(list);


        return R.success(pageInfo);
    }



    //根据id查询菜品, 带口味信息
    @GetMapping("/{id}")//id在请求url里, 用PathVariable
    public R<DishDto> getByIdWithFlavor(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }



    //修改菜品
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        log.info("保存菜品成功                  dishDto:{}",dishDto);
        return R.success("保存菜品成功");
    }


    //批量查询菜品
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){

        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dish.getCategoryId()!=null, Dish::getCategoryId, dish.getCategoryId());
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        lambdaQueryWrapper.orderByAsc(Dish::getSort) .orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(lambdaQueryWrapper);

        return R.success(list);
    }


    //TODO 菜品起售停售
    @GetMapping("/status")
    public R<String> stopOrSale(List<Long> ids, int status){


        return R.success("更改成功");
    }



    //TODO 批量删除菜品
    @DeleteMapping
    public R<String> delete(List<Long> ids){

        return R.success("删除成功");
    }



}
