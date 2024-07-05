package com.lch.suyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lch.suyu.pojo.entity.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author lch
* @description 针对表【user】的数据库操作Service
* @createDate 2024-04-12 18:47:37
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册功能
     * @param userAccount 账号
     * @param password 密码
     * @param checkPassword 校验密码
     * @return 注册信息
     */
    public String registerUser(String userAccount, String password,String checkPassword);

    /**
     * 用户登录业务
     * @param userAccount 账号
     * @param password 密码
     * @param request 前端的请求数据
     * @return 用户实体信息
     */
    public User userLogin(String userAccount, String password, HttpServletRequest request);

    /**
     * 根据用户名查询用户
     * @param userName 用户名
     * @return 用户集合
     */
    public List<User> getByName(String userName);

    /**
     * 获取脱敏后的用户信息
     * @param user 用户实体
     * @return 返回脱敏的用户信息
     */

    public User getSafetyUser(User user);

    /**
     * 获取当前用户信息
     * @param request 用户id
     * @return 用户信息
     */
    public User getCurrentUser(HttpServletRequest request);

    String logout(HttpServletRequest request);
    List<User> searchByTags(List<String> tagNameList);

    int updateUser(User user, HttpServletRequest request);
    /**
     * 返回为true则是管理员
     * @param request
     * @return boolean
     */
   boolean isAdmin(HttpServletRequest request);

    List<User> recommendUser(int pageSize, int pageNum, HttpServletRequest request);

    List<User> matchUsers(long num, User user);
}
