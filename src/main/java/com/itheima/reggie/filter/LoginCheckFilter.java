package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
@Slf4j
//检查用户是否正常登录
public class LoginCheckFilter implements Filter {

    //路径匹配器, 支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        log.info("LoginCheckFilter拦截请求: {}", request.getRequestURI());

        //获取本次请求的uri
        String requestURI = request.getRequestURI();

        //放行路径:
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/front/**",
                "/backend/**",
        };

        //判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //如果不需要处理则放行
        if(check){
            log.info("本次请求不需要处理: {}",requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        //判断登录状态, 如果已登录则放行
        //已登录则不为空
        if(request.getSession().getAttribute("employee")!=null){
            log.info("用户已登录, 用户id: {}",request.getSession().getAttribute("employee"));
            filterChain.doFilter(request, response);
            return;
        }

        //如果未登录则返回未登录结果, 用输出流向客户端页面相应数据
        log.info("用户未登录, 跳转到登录页");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }


    //判断本次请求是否需要处理
    public boolean check(String[] urls , String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url,requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }


}
