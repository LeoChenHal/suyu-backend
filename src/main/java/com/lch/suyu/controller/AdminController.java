package com.lch.suyu.controller;

import com.lch.suyu.Result.Result;
import com.lch.suyu.constant.MessageConstant;
import com.lch.suyu.exception.BusinessException;
import com.lch.suyu.pojo.entity.User;
import com.lch.suyu.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Tag(name = "管理员接口")
@Slf4j
public class AdminController {
    @Autowired
    private UserService userService;

    /**
     * 根据用户名模糊查询
     *
     * @param userName 用户名
     * @return 用户集合
     */
    @GetMapping("/getByName")
    public Result<List<User>> getUsersByName(String userName, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(MessageConstant.LOGIN_ROLE_ERROR);
        }
        return Result.success(userService.getByName(userName));
    }

    @DeleteMapping("/deleteById/{id}")
    public Result<Boolean> deleteUser
            (@PathVariable long id, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(MessageConstant.LOGIN_ROLE_ERROR);
        }
        return Result.success(userService.removeById(id));
    }


}
