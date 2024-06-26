package com.lch.suyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lch.suyu.mapper.TeamMapper;
import com.lch.suyu.pojo.entity.Team;
import com.lch.suyu.service.TeamService;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【team(队伍表)】的数据库操作Service实现
* @createDate 2024-06-26 09:06:41
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {

}




