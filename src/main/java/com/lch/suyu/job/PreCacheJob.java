package com.lch.suyu.job;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lch.suyu.constant.RedisConstant;
import com.lch.suyu.mapper.UserMapper;
import com.lch.suyu.pojo.entity.User;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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
    @Autowired
    private RedissonClient redisson;

    @Scheduled(cron = "0 0 0 * * ?")//每天零点执行一次
//    @Scheduled(cron = "*/5 * * * * ?")//每五秒
    public void preCache() {
        //建立锁对象
        RLock lock = redisson.getLock(RedisConstant.SUYU_LOCK_KEY + 3);
        try {
            //尝试获取锁对象,不填参数则没有等待时间，锁过期时间默认30秒（看门狗）
            if (lock.tryLock(0,30,TimeUnit.SECONDS)) {
                String key = RedisConstant.SUYU_RECOMMEND_KEY + 3;
                List<User> userList = (List<User>) redisTemplate.opsForValue().get(key);
                if (userList != null && !userList.isEmpty()) {
                    return;
                }
                Page<User> userPage = new Page<>(1, 20);
                Page<User> page = userMapper.selectPage(userPage, null);
                redisTemplate.opsForValue().set(key, page.getRecords(), 30, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
            //释放锁对象,只能释放自己的锁
            if (lock.isHeldByCurrentThread())
            lock.unlock();
        }
    }
}
