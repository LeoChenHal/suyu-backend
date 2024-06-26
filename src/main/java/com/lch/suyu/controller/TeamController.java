package com.lch.suyu.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lch.suyu.Result.Result;
import com.lch.suyu.constant.MessageConstant;
import com.lch.suyu.exception.BusinessException;
import com.lch.suyu.mapper.TeamMapper;
import com.lch.suyu.pojo.dto.TeamDto;
import com.lch.suyu.pojo.entity.Team;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public Result<Integer> updateTeam(@RequestBody TeamDto teamDto) {
        if (teamDto == null) {
            throw new BusinessException(MessageConstant.PARAMS_NULL);
        }
        Team team = new Team();
        BeanUtil.copyProperties(teamDto,team);
        int i = teamMapper.updateById(team);
        return Result.success(i);
    }

    @PostMapping ("/add")
    public Result<Integer> addTeam(@RequestBody TeamDto teamDto) {
        if (teamDto == null) {
            throw new BusinessException(MessageConstant.PARAMS_NULL);
        }
        Team team = new Team();
        BeanUtil.copyProperties(teamDto,team);
        int i = teamMapper.insert(team);
        return Result.success(i);
    }

    @GetMapping("/list")
    public Result<List<Team>> getTeamList(TeamDto teamDto) {
        Team team = new Team();
        BeanUtil.copyProperties(teamDto,team);
        List<Team> teamList = teamMapper.selectList(new QueryWrapper<>(team));
        return Result.success(teamList);
    }
    @GetMapping("/list/page")
    public Result<List<Team>> getListByPage(TeamDto teamDto) {
        Team team = new Team();
        BeanUtil.copyProperties(teamDto,team);
        Page<Team> teamPage = new Page<>(teamDto.getPageNum(), teamDto.getPageSize());
        List<Team> teamList = teamMapper.selectPage(teamPage, null).getRecords();
        return Result.success(teamList);
    }



}