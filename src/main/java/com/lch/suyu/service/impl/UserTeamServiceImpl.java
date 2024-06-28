package com.lch.suyu.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lch.suyu.mapper.UserTeamMapper;
import com.lch.suyu.pojo.entity.UserTeam;
import com.lch.suyu.service.UserTeamService;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【user_team(用户-队伍关系表)】的数据库操作Service实现
* @createDate 2024-06-26 18:21:06
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService {

}




