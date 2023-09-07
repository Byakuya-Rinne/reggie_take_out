/*
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

        
        int i = calculate(Integer::sum);

        System.out.println(i);

    }



    public static int calculate(IntBinaryOperator operator){
        int a = 111;
        int b = 222;
        return operator.applyAsInt(a,b);
    }







}
*/



package com.itheima.reggie;

import java.util.ArrayList;
import java.util.List;

public class ReggieApplication {
    public static List<List<String>> parseCSV(String csvStr) {
        List<List<String>> result = new ArrayList<>();

        String[] lines = csvStr.split("\n");

        for (String line : lines) {
            List<String> row = new ArrayList<>();
            boolean inQuotes = false;
            StringBuilder field = new StringBuilder();

            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);

                if (c == '"') {
                    inQuotes = !inQuotes;
                } else if (c == ',' && !inQuotes) {
                    row.add(field.toString());
                    field.setLength(0);
                } else {
                    field.append(c);
                }
            }

            row.add(field.toString());
            result.add(row);
        }

        return result;
    }

    public static void main(String[] args) {
        String csvStr = "Name,Age,Address\nJohn Doe,30,\"123 Main Street, Apartment 4B\"\nAlice Smith,25,Kwai Plaza\nMary Johnson,35,\"456 Elm Avenue, Suite 10C\"";
        List<List<String>> parsedCSV = parseCSV(csvStr);

        // 打印解析后的CSV内容
        for (List<String> row : parsedCSV) {
            System.out.println(row);
        }
    }
}






























