package com.lch.suyu.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lch.suyu.Result.Result;
import com.lch.suyu.constant.MessageConstant;
import com.lch.suyu.exception.BusinessException;
import com.lch.suyu.mapper.TeamMapper;
import com.lch.suyu.pojo.dto.TeamQueryDto;
import com.lch.suyu.pojo.dto.TeamRequestDto;
import com.lch.suyu.pojo.dto.TeamUpdateDto;
import com.lch.suyu.pojo.entity.Team;
import com.lch.suyu.pojo.entity.User;
import com.lch.suyu.pojo.vo.TeamUserVo;
import com.lch.suyu.service.TeamService;
import com.lch.suyu.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/get")
    public Result<Team> getTeamById(Integer teamId) {
        if (teamId < 1) {
            throw new BusinessException(MessageConstant.PARAMS_ERROR);
        }
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            throw new BusinessException("队伍不存在");
        }
        return Result.success(team);
    }

    @DeleteMapping("/delete")
    public Result<Integer> deleteTeamById(Integer teamId) {
        if (teamId < 1) {
            throw new BusinessException(MessageConstant.PARAMS_ERROR);
        }
        int i = teamMapper.deleteById(teamId);

        return Result.success(i);
    }

    @PutMapping("/update")
    public Result<Boolean> updateTeam(@RequestBody TeamUpdateDto teamDto,HttpServletRequest request) {
        if (teamDto == null) {
            throw new BusinessException(MessageConstant.PARAMS_NULL);
        }

        Team team = new Team();
        BeanUtil.copyProperties(teamDto, team);
        Boolean result=teamService.updateTeam(team,request);
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
        int i=teamService.addTeam(team,loginUser);
        return Result.success(i);
    }

    @GetMapping("/list")
    public Result<List<TeamUserVo>> getTeamList(TeamQueryDto teamQueryDto,HttpServletRequest request) {
        if (teamQueryDto == null) {
            throw new BusinessException(MessageConstant.PARAMS_NULL);
        }
        Team team = new Team();
        BeanUtil.copyProperties(teamQueryDto, team);
        boolean admin = userService.isAdmin(request);
        List<TeamUserVo> teamList = teamService.getTeamList(teamQueryDto,admin);
        return Result.success(teamList);
    }

    @GetMapping("/list/page")
    public Result<List<Team>> getListByPage( TeamQueryDto teamQueryDto) {
        if (teamQueryDto == null) {
            throw new BusinessException(MessageConstant.PARAMS_NULL);
        }
        Team team = new Team();
        BeanUtil.copyProperties(teamQueryDto, team);
        Page<Team> teamPage = new Page<>(teamQueryDto.getPageNum(), teamQueryDto.getPageSize());
        List<Team> teamList = teamMapper.selectPage(teamPage, null).getRecords();
        return Result.success(teamList);
    }
}