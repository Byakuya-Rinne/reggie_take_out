package com.itheima.reggie.common;


//基于ThreadLocal封装工具类, 用于保存和获取当前登录用户id
//作用范围在该次线程之内
public class BaseContext {
    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }

}
