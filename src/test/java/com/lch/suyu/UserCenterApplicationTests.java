package com.lch.suyu;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@MapperScan("com.baomidou.mybatisplus.samples.quickstart.mapper")
class UserCenterApplicationTests {

    @Test
    void contextLoads() {
    }

}
