package com.lch.suyu.service.impl;

import java.util.ArrayList;
import java.util.Date;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lch.suyu.constant.RedisConstant;
import com.lch.suyu.mapper.UserMapper;
import com.lch.suyu.pojo.entity.User;
import com.lch.suyu.service.UserService;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class UserServiceImplTest {
    //@Autowired
//    private UserServiceImpl userService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redisson;

    @Test
    void testRegister() {
        String userAccount = "";
        String password = "";
        String checkPassword = "";
        //检验空情况
        userAccount = null;
        System.out.println(userService.registerUser(userAccount, password, checkPassword));
        //检验账号长度
        userAccount = "j";
        password = "1234567";
        checkPassword = "1234567";
        System.out.println(userService.registerUser(userAccount, password, checkPassword));
        //检验账号重复
        System.out.println(userService.registerUser(userAccount, password, checkPassword));
        //检验密码长度
        userAccount = "joyBoy";
        password = "1234";
        System.out.println(userService.registerUser(userAccount, password, checkPassword));
        //
    }

    @Test
    void testSearchByTags() {
        List<String> tagNameList = Arrays.asList(
                "java",
                "spring",
                "springboot"
        );
        List<User> userList = userService.searchByTags(tagNameList);
        System.out.println(userList);
    }

    //批量插入
    @Test
    void insertUserBatch() {
        int batchSize = 2500;
        int j = 0;
        long start = System.currentTimeMillis();
        while (j < 20) {
            ArrayList<User> userList = new ArrayList<>();
            for (int i = 0; i < batchSize; i++) {
                User user = new User();
                user.setUsername("测试用户");
                user.setUserAccount("testlch");
                user.setUserPassword("123456");
                user.setPhone(123456);
                user.setEmail("123@qq.com");
                user.setAvatarUrl("https://i.postimg.cc/sXqKQWdJ/20230714140118.jpg");
                user.setUserStatus(1);
                user.setTags("[]");
                userList.add(user);
            }
            userService.saveBatch(userList, batchSize);
            j++;
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    @Test
    void redisTest() {
        User user = new User();
        user.setUsername("testlch");
        redisTemplate.opsForValue().set("user", user);
        System.out.println(redisTemplate.opsForValue().get("user"));
    }

    @Test
    void watchDogTest() {
        RLock lock = redisson.getLock(RedisConstant.SUYU_LOCK_KEY + 3);
        try {
            //尝试获取锁对象,不填参数则没有等待时间，锁过期时间默认30秒（看门狗）
            if (lock.tryLock(0, -1, TimeUnit.SECONDS)) {
//                String key = RedisConstant.SUYU_RECOMMEND_KEY + 3;
//                List<User> userList = (List<User>) redisTemplate.opsForValue().get(key);
//                if (userList != null && !userList.isEmpty()) {
//                    return;
//                }
//                Page<User> userPage = new Page<>(1, 20);
//                Page<User> page = userMapper.selectPage(userPage, null);
//                redisTemplate.opsForValue().set(key, page.getRecords(), 30, TimeUnit.SECONDS);
                Thread.sleep(10000000);
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