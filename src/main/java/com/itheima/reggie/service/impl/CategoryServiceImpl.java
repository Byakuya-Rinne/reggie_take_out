package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;
    private Exception exception;


    //判断分类有无关联, 再根据id删除分类
    @Override
    public void remove(Long ids){
        //TODO 判断有无 菜品 套餐 关联 LambdaQueryWrapper

        //判断有无 菜品和套餐 关联, 若有则抛出异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件, 根据id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,ids);
        int dishCount = dishService.count(dishLambdaQueryWrapper);

            LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
            //添加查询条件, 根据id进行查询
            setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,ids);
            int setmealCount = setmealService.count(setmealLambdaQueryWrapper);


            if(dishCount>0||setmealCount>0){
                //如果有关联, 则抛出异常
                throw new CustomException("当前分类下已有关联, 删除失败");
            }else{
                //若无则删除分类
                log.info("正常删除分类");
                super.removeById(ids);
            }
    }
}
