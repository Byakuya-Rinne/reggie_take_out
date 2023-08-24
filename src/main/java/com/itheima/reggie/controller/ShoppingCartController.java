package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Resource
    private ShoppingCartService shoppingCartService;

    //把菜品/套餐添加购物车
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //设置用户id, 指定当前购物车的用户
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);

        //查询当前加入的数据是否已存在
        Long dishId = shoppingCart.getDishId();
        if(dishId!=null){
            //添加的是菜品
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            //添加的是套餐
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        //select * from shopping_cart where user_id = ? and (dish_id / setmeal_id) = ?
        ShoppingCart cartServiceOne = shoppingCartService.getOne(lambdaQueryWrapper);

        //如果不存在, 插入新数据. 如果已存在, 在number上+1
        if(cartServiceOne!=null){//已存在
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number+1);
        }else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }

        return R.success(cartServiceOne);
    }



    //查看购物车
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);
        lambdaQueryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(lambdaQueryWrapper);

        return R.success(list);
    }

    //清空购物车
    @DeleteMapping("/clean")
    public R<String> clean(){
        //delete from shopping_cart where user_id = ?
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        shoppingCartService.remove(lambdaQueryWrapper);
        return R.success("情况购物车成功了了了");
    }



}
