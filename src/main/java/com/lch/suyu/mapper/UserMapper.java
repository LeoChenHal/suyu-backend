package com.lch.suyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lch.suyu.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【user】的数据库操作Mapper
* @createDate 2024-04-12 18:47:37
* @Entity com.lch.suyu.Entity.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




