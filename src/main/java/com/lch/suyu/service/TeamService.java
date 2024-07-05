package com.lch.suyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lch.suyu.pojo.dto.TeamJoinDto;
import com.lch.suyu.pojo.dto.TeamQueryDto;
import com.lch.suyu.pojo.dto.TeamQuitDto;
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
    /**
     * 创建队伍
     * @param team
     * @param loginUser
     * @return int
     */
    int addTeam(Team team, User loginUser);

    /**
     * 获取队伍列表
     * @param teamQueryDto
     * @param admin
     * @return  List<TeamUserVo>
     */

    List<TeamUserVo> getTeamList(TeamQueryDto teamQueryDto, boolean admin);

    /**
     * 修改队伍
     * @param team
     * @param request
     * @return  Boolean
     */

    Boolean updateTeam(Team team, HttpServletRequest request);

    /**
     * 加入队伍
     * @param teamjoinDto
     * @param loginUser
     * @return boolean
     */

    boolean joinTeam(TeamJoinDto teamjoinDto, User loginUser);

    /**
     * 退出队伍
     * @param teamDto
     * @param request
     * @return boolean
     */

    boolean quitTeam(TeamQuitDto teamDto, HttpServletRequest request);

    /**
     * 队长解散队伍
     * @param teamId
     * @param request
     * @return
     */
    boolean deleteTeam(Integer teamId, HttpServletRequest request);
}
