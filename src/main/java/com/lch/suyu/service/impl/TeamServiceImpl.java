package com.lch.suyu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lch.suyu.constant.MessageConstant;
import com.lch.suyu.exception.BusinessException;
import com.lch.suyu.mapper.TeamMapper;
import com.lch.suyu.pojo.dto.TeamJoinDto;
import com.lch.suyu.pojo.dto.TeamQueryDto;
import com.lch.suyu.pojo.dto.TeamQuitDto;
import com.lch.suyu.pojo.entity.Team;
import com.lch.suyu.pojo.entity.User;
import com.lch.suyu.pojo.entity.UserTeam;
import com.lch.suyu.pojo.vo.TeamUserVo;
import com.lch.suyu.pojo.vo.UserVo;
import com.lch.suyu.service.TeamService;
import com.lch.suyu.service.UserService;
import com.lch.suyu.service.UserTeamService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    @Autowired
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addTeam(Team team, User loginUser) {
        if (team == null) {
            throw new BusinessException(MessageConstant.PARAMS_NULL);
        }
        team.setUserId(loginUser.getId());
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20) {
            throw new BusinessException("队伍人数不符");
        }
        if (StringUtils.isBlank(team.getTeamDesc()) || team.getTeamDesc().length() > 200) {
            throw new BusinessException("队伍描述字数不符");
        }
        if ((team.getTeamName().length() > 10) || StringUtils.isBlank(team.getTeamName())) {
            throw new BusinessException("队伍名称字数不符");
        }
        int status = team.getStatus();
        if (status == 2 && (StringUtils.isBlank(team.getTeamPwd()) || team.getTeamPwd().length() > 4)) {
            throw new BusinessException("队伍密码长度不符");
        }
        if (!(status == 0 || status == 1 || status == 2)) {
            throw new BusinessException("队伍状态错误");
        }
        //当前面的时间晚于后面的时间返回true
        if (new Date().after(team.getExpireTime())) {
            throw new BusinessException("队伍过期时间需要大于创建时间");
        }
        int userId = loginUser.getId();
        if (userId != team.getUserId()) {
            throw new BusinessException("队伍创建者错误");
        }
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        long count = this.count(queryWrapper);
        if (count >= 5) {
            throw new BusinessException("用户最多创建五个队伍");
        }
        //插入队伍表
        team.setUserId(userId);
        boolean result = this.save(team);
        Integer teamId = team.getId();
        if (!result || teamId == null) {
            throw new BusinessException("创建队伍失败");
        }
        //插入队伍关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(teamId);
        userTeam.setUserId(userId);
        userTeam.setJoinTime(new Date());
        boolean b = userTeamService.save(userTeam);
        if (!b) {
            throw new BusinessException("创建队伍失败");
        }
        return teamId;
    }

    @Override
    public List<TeamUserVo> getTeamList(TeamQueryDto teamQueryDto, boolean admin) {
        Integer id = teamQueryDto.getId();
        String teamName = teamQueryDto.getTeamName();
        String teamDesc = teamQueryDto.getTeamDesc();
        Integer maxNum = teamQueryDto.getMaxNum();
        Integer userId = teamQueryDto.getUserId();
        Integer status = teamQueryDto.getStatus();
        String searchText = teamQueryDto.getSearchText();
        List<Integer> idList = teamQueryDto.getIdList();
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        if (!CollectionUtils.isEmpty(idList)){
            queryWrapper.in("id",idList);
        }
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(wrapper -> wrapper.like("team_name", searchText).or().like("team_desc", searchText));
        }
        if (id != null && id > 0) {
            queryWrapper.eq("id", id);
        }

        if (StringUtils.isNotBlank(teamName)) {
            queryWrapper.like("team_name", teamName);
        }
        if (StringUtils.isNotBlank(teamDesc)) {
            queryWrapper.like("team_desc", teamDesc);
        }
        if (maxNum != null) {
            queryWrapper.eq("max_num", maxNum);
        }
        if (userId != null && userId > 0) {
            queryWrapper.eq("user_id", userId);
        }
        if (status == null) {
            status = 0;
        }
        //不让非管理获取到加密房和私有房信息
        if (!admin && (status == 1)) {
            throw new BusinessException(MessageConstant.LOGIN_ROLE_ERROR);
        }
        if (status == 0 || status == 1 || status == 2) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.and(qw -> qw.gt("expire_time", new Date()).or().isNull("expire_time"));
        List<Team> list = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<TeamUserVo> teamUserVoList = new ArrayList<>();
        for (Team t :
                list) {
            Integer userId1 = t.getUserId();
            if (userId1 != null) {
                User user = userService.getById(userId1);
                UserVo userVo = new UserVo();
                BeanUtil.copyProperties(user, userVo);
                TeamUserVo teamUserVo = new TeamUserVo();
                teamUserVo.setUser(userVo);
                BeanUtil.copyProperties(t, teamUserVo);
                teamUserVoList.add(teamUserVo);
            }
        }
        return teamUserVoList;
    }

    @Override
    public Boolean updateTeam(Team team, HttpServletRequest request) {
        Integer teamId = team.getId();
        if (teamId == null || teamId < 1) {
            throw new BusinessException(MessageConstant.PARAMS_NULL);
        }
        Team oldTeam = this.getById(teamId);
        if (oldTeam == null) {
            throw new BusinessException("要修改的队伍不存在");
        }
        Integer userId = oldTeam.getUserId();
        User loginUser = userService.getCurrentUser(request);
        if (loginUser == null) {
            throw new BusinessException("请先登录");
        }
        Integer loginUserId = loginUser.getId();
        boolean admin = userService.isAdmin(request);
        if (!admin && userId != loginUserId) {
            throw new BusinessException("只有队伍创建者或管理员才能修改队伍");
        }
        Integer status = team.getStatus();
        if (status == 2 && StringUtils.isBlank(team.getTeamPwd())) {
            throw new BusinessException("密码房需要设置队伍密码");
        }
        boolean result = this.updateById(team);
        return result;
    }

    @Override
    public boolean joinTeam(TeamJoinDto teamJoinDto, User loginUser) {
        Integer teamId = teamJoinDto.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(MessageConstant.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException("要加入的队伍不存在");
        }
//不能加入已过期队伍
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new BusinessException(MessageConstant.PARAMS_ERROR + " 队伍已过期");
        }
        //不能加入私有队伍
        Integer status = team.getStatus();
        if (status == 1) {
            throw new BusinessException("禁止加入私有队伍");
        }
        if (status == 2) {
            if (StringUtils.isBlank(teamJoinDto.getTeamPwd()) || !team.getTeamPwd().equals(teamJoinDto.getTeamPwd())) {
                throw new BusinessException("密码错误");
            }
        }
        //最多加入五个队伍
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        synchronized (this) {
            Integer loginUserId = loginUser.getId();
            userTeamQueryWrapper.eq("user_id", loginUserId);
            long count = userTeamService.count(userTeamQueryWrapper);
            if (count >= 5) {
                throw new BusinessException("用户最多加入五个队伍");
            }
            //不能加入已加入的队伍
            userTeamQueryWrapper = new QueryWrapper<UserTeam>();
            userTeamQueryWrapper.eq("team_id", teamId);
            userTeamQueryWrapper.eq("user_id", loginUserId);
            long hasJoinNum = userTeamService.count(userTeamQueryWrapper);
            if (hasJoinNum > 0) {
                throw new BusinessException("您已加入该队伍");
            }
            //不能加入人数已满的队伍
            userTeamQueryWrapper = new QueryWrapper<UserTeam>();
            userTeamQueryWrapper.eq("team_id", teamId);
            long hasNum = userTeamService.count(userTeamQueryWrapper);
            if (hasNum >= team.getMaxNum()) {
                throw new BusinessException("该队人数已满");
            }
            UserTeam userTeam = new UserTeam();
            userTeam.setJoinTime(new Date());
            userTeam.setTeamId(teamId);
            userTeam.setUserId(loginUserId);
            return userTeamService.save(userTeam);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitDto teamDto, HttpServletRequest request) {
        User loginUser = userService.getCurrentUser(request);
        Integer loginUserId = loginUser.getId();
        if (loginUser == null||loginUserId<=0) {
            throw new BusinessException(MessageConstant.NOT_LOGIN);
        }
        Integer teamId = teamDto.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(MessageConstant.PARAMS_NULL);
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException("队伍不存在");
        }
        Integer userId = team.getUserId();
        QueryWrapper<UserTeam> wrapper = new QueryWrapper<>();
        wrapper.eq("team_id", teamId);
        wrapper.eq("user_id", loginUserId);
        long count = userTeamService.count(wrapper);
        if (count==0){
            throw new BusinessException("您并未加入该队伍");
        }
        wrapper=new QueryWrapper<>();
        wrapper.eq("team_id", teamId);
        long teamJoinUserNum = userTeamService.count(wrapper);//队伍有几个人
        if (teamJoinUserNum==1) {//队伍只有一个人
            //只有队长可以解散队伍
            if (userId!= null && userId.equals(loginUserId)) {
               this.removeById(teamId);
               return userTeamService.remove(wrapper);
            } else {
                throw new BusinessException("只有队长可以解散队伍");
            }
        }else {
            if (userId!= null && userId.equals(loginUserId)) {//转移第二早加入的人
                wrapper= new QueryWrapper<>();
                wrapper.eq("team_id",teamId);
                wrapper.orderByAsc("id");
                List<UserTeam> userTeamList = userTeamService.list(wrapper);
                if (CollectionUtils.isEmpty(userTeamList))
                    throw new BusinessException("userTeamList为空");
                UserTeam userTeam = userTeamList.get(1);
                Team updateTeam = new Team();
                updateTeam.setUserId(userTeam.getUserId());
                updateTeam.setId(userTeam.getTeamId());
                boolean b = this.updateById(updateTeam);
                if (!b){
                    throw new BusinessException("转移队长失败");
                }
            }
            wrapper=new QueryWrapper<>();
            wrapper.eq("team_id", teamId);
            wrapper.eq("user_id",loginUserId);
            return userTeamService.remove(wrapper);
            }
    }

    @Override
    public boolean deleteTeam(Integer teamId, HttpServletRequest request) {
        User loginUser = userService.getCurrentUser(request);
        Integer loginUserId = loginUser.getId();
        if (loginUser == null||loginUserId<=0) {
            throw new BusinessException(MessageConstant.NOT_LOGIN);
        }
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(MessageConstant.PARAMS_NULL);
        }
        Team team = this.getById(teamId);
        if(team == null){
            throw new BusinessException("该队伍不存在");
        }
        //判断是否是队长
        Integer userId = team.getUserId();
        if (!userId.equals(loginUserId)) {
            throw new BusinessException("只有队长才可以解散队伍");
        }
        //解散队伍
        this.removeById(teamId);
        //删除UserTeam表中的数据
        QueryWrapper<UserTeam> wrapper = new QueryWrapper<>();
        wrapper.eq("team_id", teamId);
        return userTeamService.remove(wrapper);
    }
}




