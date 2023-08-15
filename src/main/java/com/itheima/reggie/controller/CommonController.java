package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;


//文件上传下载
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    //引用SpringBoot配置文件
    @Value("${reggie.path}")
    private String basePath;

    //文件上传
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){ //参数名必须和前端的name属性对应
        //此时file是临时文件, 需要转存到指定位置
        log.info(file.toString());

        //截取原始文件名的后缀
        String originalFileName = file.getOriginalFilename();
        String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));

        //用uuid重新生成文件名, 防止文件名称重复造成覆盖
        String fileName = UUID.randomUUID().toString() + suffix;

        //判断指定位置目录是否存在
            //创建目录对象
            File dir = new File(basePath);
        if(!dir.exists()){
            //目录不存在, 新建文件夹
            dir.mkdir();
        }

        //把临时文件转存到指定位置
        try {
            file.transferTo(new File(basePath+ fileName));
        } catch (IOException e) {
            e.printStackTrace();
            //return R.error("文件上传失败");
        }

        //返回文件名, 前端要用
        return R.success(fileName);
    }


    //文件下载
    @GetMapping("/download")
    public void download(String name, HttpServletResponse httpServletResponse){
        try {
            //通过输入流读取服务器文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            //通过输出流把文件写入浏览器
            ServletOutputStream outputStream = httpServletResponse.getOutputStream();

            //httpServletResponse.setContentType("image/jpeg");


            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //关闭资源
            outputStream.close();
            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }










}
