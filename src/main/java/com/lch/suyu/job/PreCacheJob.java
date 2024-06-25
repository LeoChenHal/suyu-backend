package com.lch.suyu.job;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lch.suyu.constant.RedisConstant;
import com.lch.suyu.mapper.UserMapper;
import com.lch.suyu.pojo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class PreCacheJob {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Scheduled(cron = "0 0 0 * * ?")//每天零点执行一次
//    @Scheduled(cron = "*/5 * * * * ?")//每五秒
    public void preCache(){
        String key = RedisConstant.SUYU_RECOMMEND_KEY+3;
        List<User> userList = (List<User>)redisTemplate.opsForValue().get(key);
        if (userList!=null && !userList.isEmpty()){
            return ;
        }
        Page<User> userPage = new Page<>(1,20);
        Page<User> page = userMapper.selectPage(userPage,null);
        redisTemplate.opsForValue().set(key,page.getRecords(),30, TimeUnit.SECONDS);
    }
}
