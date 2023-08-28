package com.itheima.reggie.config;



//@Configuration
public class RedisConfig {//extends CachingConfigurerSupport {
//    @Bean
//    public RedisTemplate<Object,Object> redisTemplate(RedisConnectionFactory connectionFactory){
//        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setConnectionFactory(connectionFactory);
//        return redisTemplate;
//    }
//
//主程序加, 开启缓存注解
//@EnableCaching

//返回的数据类型需要实现Serializeable接口

//执行方法前先看缓存中有无数据. 如果有则直接返回缓存数据, 若无缓存则调用方法, 再把返回值放到缓存中
//value:缓存的名称, 每个名称下可以有多个key.     key:缓存的key.     condition:条件, 满足条件才缓存数据/unless:满足条件不缓存
//@Cacheable(value = "", key = "#id", condition = "#result != null")
//@Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")

//
//@CacheEvict(value = "setmealCache", allEntries = true)
//
//
//
//
//
//
//
//
//
//
//
}
