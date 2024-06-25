package com.lch.suyu.controller;

import com.alibaba.druid.support.json.JSONUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lch.suyu.Result.Result;
import com.lch.suyu.constant.MessageConstant;
import com.lch.suyu.exception.BusinessException;
import com.lch.suyu.pojo.dto.LoginDto;
import com.lch.suyu.pojo.dto.RegisterDto;
import com.lch.suyu.pojo.entity.User;
import com.lch.suyu.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin
@Tag(name = "用户接口")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterDto registerDto) {
        log.info("register method was called");
        if (registerDto == null) {
            throw new BusinessException(MessageConstant.PARAMS_NULL);
        }
        String message = "";
        message = userService.registerUser(registerDto.getUserAccount(),
                registerDto.getUserPassword(), registerDto.getCheckPassword());
        return Result.success(JSONUtils.toJSONString(message));
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginDto loginDto, HttpServletRequest request) {
        log.info("login method was called");
        String userAccount = loginDto.getUserAccount();
        String password = loginDto.getUserPassword();
        boolean anyBlank = StringUtils.isAnyBlank(userAccount, password);
        if (anyBlank) {
            throw new BusinessException(MessageConstant.PARAMS_NULL);
        }
        return Result.success(userService.userLogin(userAccount, password, request));
    }

    /**
     * 获取当前用户信息，获取一个登录态
     * @param request 请求
     * @return 当前用户信息
     */
    @GetMapping("/current")
    public Result<User> getCurrentUser(HttpServletRequest request) {

        return Result.success(userService.getCurrentUser(request));
    }

    /**
     * 用户退出登录
     * @param request 请求
     * @return 返回1代表成功
     */
    @GetMapping("/logout")
    public Result<String> logout(HttpServletRequest request){
        if (request==null)
            throw new BusinessException(MessageConstant.PARAMS_NULL);
        return Result.success(userService.logout(request));
    }

   @Operation(summary = "根据标签搜索用户")
    @GetMapping("/search/tags")
    public Result<List<User>> searchByTags(@RequestParam(required = false) List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(MessageConstant.PARAMS_NULL);
        }
        List<User> users = userService.searchByTags(tagNameList);
        return Result.success(users);
    }


    @PostMapping("/update")
    public Result<Integer> updateUser(@RequestBody User user,HttpServletRequest request){
        if (user==null)
            throw new BusinessException(MessageConstant.PARAMS_NULL);
        User loginUser = userService.getCurrentUser(request);
        if (loginUser==null){
            throw new BusinessException(MessageConstant.NOT_LOGIN);
        }
        return Result.success(userService.updateUser(user,request));
    }
    @GetMapping("/recommend")
    public Result<List<User>> recommendUser(int pageSize, int pageNum,HttpServletRequest request){
        return Result.success(userService.recommendUser(pageSize,pageNum,request));
    }

}
