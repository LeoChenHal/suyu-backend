package com.lch.suyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lch.suyu.pojo.dto.TeamQueryDto;
import com.lch.suyu.pojo.entity.Team;
import com.lch.suyu.pojo.entity.User;
import com.lch.suyu.pojo.vo.TeamUserVo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author Administrator
* @description 针对表【team(队伍表)】的数据库操作Service
* @createDate 2024-06-26 09:06:41
*/
public interface TeamService extends IService<Team> {

    int addTeam(Team team, User loginUser);

    List<TeamUserVo> getTeamList(TeamQueryDto teamQueryDto, boolean admin);

    Boolean updateTeam(Team team, HttpServletRequest request);

}
