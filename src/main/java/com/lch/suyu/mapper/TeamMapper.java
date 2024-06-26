package com.lch.suyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lch.suyu.pojo.entity.Team;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【team(队伍表)】的数据库操作Mapper
* @createDate 2024-06-26 09:06:41
* @Entity generator.domain.Team
*/
@Mapper
public interface TeamMapper extends BaseMapper<Team> {

}




