package com.lch.suyu.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lch.suyu.Result.Result;
import com.lch.suyu.constant.MessageConstant;
import com.lch.suyu.exception.BusinessException;
import com.lch.suyu.mapper.TeamMapper;
import com.lch.suyu.pojo.dto.*;
import com.lch.suyu.pojo.entity.Team;
import com.lch.suyu.pojo.entity.User;
import com.lch.suyu.pojo.entity.UserTeam;
import com.lch.suyu.pojo.vo.TeamUserVo;
import com.lch.suyu.service.TeamService;
import com.lch.suyu.service.UserService;
import com.lch.suyu.service.UserTeamService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/team")
@CrossOrigin
@Tag(name = "队伍接口")
@Slf4j
public class TeamController {
    @Autowired
    private TeamMapper teamMapper;
    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserTeamService userTeamService;

    @GetMapping("/get")
    public Result<Team> getTeamById(Integer teamId) {
        if (teamId==null||teamId < 1) {
            throw new BusinessException(MessageConstant.PARAMS_ERROR);
        }
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            throw new BusinessException("队伍不存在");
        }
        return Result.success(team);
    }

    @PostMapping("/delete")
    public Result<Boolean> deleteTeamById(@RequestBody TeamQuitDto teamDto, HttpServletRequest request) {
        if (teamDto == null) {
            throw new BusinessException(MessageConstant.PARAMS_NULL);
        }
        Integer teamId = teamDto.getTeamId();
        if (teamId == null || teamId < 1) {
            throw new BusinessException(MessageConstant.PARAMS_ERROR);
        }
        boolean result = teamService.deleteTeam(teamId, request);

        return Result.success(result);
    }

    @PutMapping("/update")
    public Result<Boolean> updateTeam(@RequestBody TeamUpdateDto teamDto, HttpServletRequest request) {
        if (teamDto == null) {
            throw new BusinessException(MessageConstant.PARAMS_NULL);
        }

        Team team = new Team();
        BeanUtil.copyProperties(teamDto, team);
        Boolean result = teamService.updateTeam(team, request);
        return Result.success(result);
    }

    @PostMapping("/add")
    public Result<Integer> addTeam(@RequestBody TeamRequestDto teamDto, HttpServletRequest request) {
        if (teamDto == null) {
            throw new BusinessException(MessageConstant.PARAMS_NULL);
        }
        User loginUser = userService.getCurrentUser(request);
        Team team = new Team();
        BeanUtil.copyProperties(teamDto, team);
        int i = teamService.addTeam(team, loginUser);
        return Result.success(i);
    }

    /**
     * 获取队伍列表信息
     *
     * @param teamQueryDto
     * @param request
     * @return List<TeamUserVo>
     */
    @GetMapping("/list")
    public Result<List<TeamUserVo>> getTeamList(TeamQueryDto teamQueryDto, HttpServletRequest request) {
        if (teamQueryDto == null) {
            throw new BusinessException(MessageConstant.PARAMS_NULL);
        }
        boolean admin = userService.isAdmin(request);
        List<TeamUserVo> teamList = teamService.getTeamList(teamQueryDto, admin);
        //判断用户是否已加入队伍
        List<Integer> teamIdList = teamList.stream().map(teamUserVo -> teamUserVo.getId()).collect(Collectors.toList());

        try {
            User loginUser = userService.getCurrentUser(request);
            QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", loginUser.getId());
            if (!CollectionUtils.isEmpty(teamIdList)) {
                queryWrapper.in("team_id", teamIdList);
            }
//            queryWrapper.in(!CollectionUtils.isEmpty(teamIdList),"team_id", teamIdList);
            List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
            Set<Integer> hasJoinIdSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
            teamList.stream().forEach(team -> {
                boolean hasJoin = hasJoinIdSet.contains(team.getId());
                team.setHasJoin(hasJoin);
            });
        } catch (Exception e) {

        }
        //设置当前队伍已加入人数
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        if (!CollectionUtils.isEmpty(teamIdList)) {
            userTeamQueryWrapper.in("team_id", teamIdList);
        }
        List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
        Map<Integer, List<UserTeam>> integerListMap = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        //遍历查到的队伍设置队伍人数
        teamList.forEach(team -> {
            int joinCount = integerListMap.getOrDefault(team.getId(), new ArrayList<>()).size();
            team.setHasJoinNum(joinCount);
        });

        return Result.success(teamList);
    }

    @GetMapping("/list/page")
    public Result<List<Team>> getListByPage(TeamQueryDto teamQueryDto) {
        if (teamQueryDto == null) {
            throw new BusinessException(MessageConstant.PARAMS_NULL);
        }
        Team team = new Team();
        BeanUtil.copyProperties(teamQueryDto, team);
        Page<Team> teamPage = new Page<>(teamQueryDto.getPageNum(), teamQueryDto.getPageSize());
        List<Team> teamList = teamMapper.selectPage(teamPage, null).getRecords();
        return Result.success(teamList);
    }

    @PostMapping("/join")
    public Result<Boolean> joinTeam(@RequestBody TeamJoinDto teamDto, HttpServletRequest request) {
        if (teamDto == null) {
            throw new BusinessException(MessageConstant.PARAMS_NULL);
        }
        User loginUser = userService.getCurrentUser(request);
        boolean result = teamService.joinTeam(teamDto, loginUser);
        return Result.success(result);
    }

    @PostMapping("/quit")
    public Result<Boolean> quitTeam(@RequestBody TeamQuitDto teamDto, HttpServletRequest request) {
        if (teamDto == null) {
            throw new BusinessException(MessageConstant.PARAMS_NULL);
        }
        boolean result = teamService.quitTeam(teamDto, request);
        return Result.success(result);
    }

    /**
     * 获取我创建的队伍
     *
     * @param teamQueryDto
     * @param request
     * @return List<TeamUserVo>
     */
    @GetMapping("/list/my/create")
    public Result<List<TeamUserVo>> getMyCreateTeamList(TeamQueryDto teamQueryDto, HttpServletRequest request) {
        if (teamQueryDto == null) {
            throw new BusinessException(MessageConstant.PARAMS_NULL);
        }
        User loginUser = userService.getCurrentUser(request);
        Integer id = loginUser.getId();
        teamQueryDto.setUserId(id);
        List<TeamUserVo> teamList = teamService.getTeamList(teamQueryDto, true);
        return Result.success(teamList);
    }

    /**
     * 获取我加入的队伍
     *
     * @param teamQueryDto
     * @param request
     * @return List<TeamUserVo>
     */
    @GetMapping("/list/my/join")
    public Result<List<TeamUserVo>> getMyJoinTeamList(TeamQueryDto teamQueryDto, HttpServletRequest request) {
        if (teamQueryDto == null) {
            throw new BusinessException(MessageConstant.PARAMS_NULL);
        }
        User loginUser = userService.getCurrentUser(request);
        Integer id = loginUser.getId();
        teamQueryDto.setUserId(id);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", id);
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        Map<Integer, List<UserTeam>> listMap = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        ArrayList<Integer> list = new ArrayList<>(listMap.keySet());
        teamQueryDto.setIdList(list);
        List<TeamUserVo> teamList = teamService.getTeamList(teamQueryDto, true);
        return Result.success(teamList);
    }
}