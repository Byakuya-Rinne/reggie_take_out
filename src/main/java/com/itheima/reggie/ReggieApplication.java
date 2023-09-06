package com.itheima.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.function.IntBinaryOperator;

//
//@Slf4j
//@SpringBootApplication
//@EnableTransactionManagement
//@ServletComponentScan
//@EnableCaching
public class ReggieApplication {
    public static void main(String[] args) {
        //SpringApplication.run(ReggieApplication.class,args);
        //log.info("项目启动成功...");

        int i = calculate(new IntBinaryOperator() {
            @Override
            public int applyAsInt(int left, int right) {
                return left + right;
            }
        });

        System.out.println(i);

    }



    public static int calculate(IntBinaryOperator operator){
        int a = 111;
        int b = 222;
        return operator.applyAsInt(a,b);
    }







}
