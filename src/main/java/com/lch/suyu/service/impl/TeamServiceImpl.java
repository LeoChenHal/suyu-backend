package com.lch.suyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lch.suyu.constant.MessageConstant;
import com.lch.suyu.exception.BusinessException;
import com.lch.suyu.mapper.TeamMapper;
import com.lch.suyu.pojo.entity.Team;
import com.lch.suyu.pojo.entity.User;
import com.lch.suyu.service.TeamService;
import com.lch.suyu.service.UserTeamService;
import generator.domain.UserTeam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

/**
* @author Administrator
* @description 针对表【team(队伍表)】的数据库操作Service实现
* @createDate 2024-06-26 09:06:41
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {

    @Autowired
    private UserTeamService userTeamService;
    @Override
    @Transactional
    public void addTeam(Team team, User loginUser) {
        if (team == null) {
        throw new BusinessException(MessageConstant.PARAMS_NULL);
        }
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum<1||maxNum>20){
            throw new BusinessException("队伍人数不符");
        }
        if (team.getTeamDesc().length()>200|| StringUtils.isBlank(team.getTeamDesc())){
            throw new BusinessException("队伍描述字数不符");
        }
        if (team.getTeamName().length()>10||StringUtils.isBlank(team.getTeamName())){
            throw new BusinessException("队伍名称字数不符");
        }
        int status = team.getStatus();
        if (status==2&&team.getTeamPwd().length()>4){
            throw new BusinessException("队伍密码长度不符");
        }
        if (!(status==0||status==1||status==2)){
            throw new BusinessException("队伍状态错误");
        }
        //当前面的时间晚于后面的时间返回true
        if (new Date().after(team.getExpireTime())){
            throw new BusinessException("队伍过期时间需要大于创建时间");
        }
        int userId = loginUser.getId();
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        long count = this.count(queryWrapper);
        if (count>5){
            throw new BusinessException("创建队伍数量不能超过五个");
        }
        //插入队伍表
        team.setUserId(userId);
        boolean result = this.save(team);
        Integer teamId = team.getId();
        if (!result||teamId==null){
            throw new BusinessException("创建队伍失败");
        }
        //插入队伍关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(teamId);
        userTeam.setUserId(userId);
        userTeam.setJoinTime(new Date());
        boolean b = userTeamService.save(userTeam);
        if(!b){
            throw new BusinessException("创建队伍失败");
        }
    }
}




