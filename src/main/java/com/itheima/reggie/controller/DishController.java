package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//菜品管理
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController{
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;

    //保存菜品
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("dishDTO: {}",dishDto);
        //新增菜品dish, 同时新增口味dish_flavor
        dishService.saveWithFlavor(dishDto);

        return null;
    }





















}
