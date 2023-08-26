package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.mapper.OrderMapper;
import com.itheima.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    //用户下单
    @Override
    @Transactional //操作多次数据库, 需要事务
    public void submit(Orders orders) {
        //获取当前用户id
        Long currentId = BaseContext.getCurrentId();

        //查询当前用户的购物车
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);

        List<ShoppingCart> shoppingCarts = shoppingCartService.list(lambdaQueryWrapper);

                //如果购物车为空, 跳过
                if (shoppingCarts == null || shoppingCarts.size() == 0) {
                    throw new CustomException("购物车为空, 下单失败");
                }

                //根据id获取用户信息 地址信息
                User user = userService.getById(currentId);
                Long addressBookId = orders.getAddressBookId();
                AddressBook addressBook = addressBookService.getById(addressBookId);
                if(addressBook==null){
                    throw new CustomException("用户地址信息错误, 下单失败");
                }

        //自动生成订单号
        long orderId = IdWorker.getId();

        //声明订单总金额, 默认0  原子操作, 线程安全
        AtomicInteger amount = new AtomicInteger(0);

        //计算订单总金额(遍历购物车), 且生成订单详细数据
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item)->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());



        //向order表插入1条数据
            orders.setId(orderId);
            orders.setOrderTime(LocalDateTime.now());
            orders.setCheckoutTime(LocalDateTime.now());
            orders.setStatus(2);//待派送状态
            orders.setAmount(new BigDecimal(amount.get()));//订单总金额
            orders.setUserId(currentId);
            orders.setNumber(String.valueOf(orderId));
            orders.setUserName(user.getName());
            orders.setConsignee(addressBook.getConsignee());
            orders.setPhone(addressBook.getPhone());
            orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                    + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                    + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                    + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        this.save(orders);

        //向order_detail表插入多条数据
        orderDetailService.saveBatch(orderDetails);

        //清空用户的购物车
        shoppingCartService.remove(lambdaQueryWrapper);
    }
}