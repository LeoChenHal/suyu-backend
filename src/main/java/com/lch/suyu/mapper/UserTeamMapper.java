package com.lch.suyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lch.suyu.pojo.entity.UserTeam;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【user_team(用户-队伍关系表)】的数据库操作Mapper
* @createDate 2024-06-26 18:21:06
* @Entity generator.domain.UserTeam
*/
@Mapper
public interface UserTeamMapper extends BaseMapper<UserTeam> {

}




