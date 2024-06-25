package com.lch.suyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lch.suyu.constant.MessageConstant;
import com.lch.suyu.constant.RedisConstant;
import com.lch.suyu.constant.UserConstant;
import com.lch.suyu.exception.BusinessException;
import com.lch.suyu.pojo.entity.User;
import com.lch.suyu.service.UserService;
import com.lch.suyu.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2024-04-12 18:47:37
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
private RedisTemplate redisTemplate;

    private static final String SALT = "lch";

    /**
     * 用户注册方法
     *
     * @param userAccount   账号
     * @param password      密码
     * @param checkPassword 校验密码
     * @return 返回注册信息（成功或者异常信息）
     */
    public String registerUser(String userAccount, String password, String checkPassword) {
        log.info("registerUser method was called");
        //判断userAccount和password和checkPassword是否为空
        if (StringUtils.isAnyBlank(userAccount, password, checkPassword)) {

            throw new BusinessException(MessageConstant.PARAMS_NULL);
        }
        //判断userAccount是否小于4位
        if (userAccount.length() < 4) {
            throw new BusinessException("账号小于4位");
        }
        //判断账号是否含有特殊字符(只含有数字和字母）
        String regex = "^[a-zA-Z0-9]+$";
        boolean matches = userAccount.matches(regex);
        if (!matches) {
            throw new BusinessException("含有特殊字符");
        }
        //判断userAccount是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        Long count = userMapper.selectCount(queryWrapper);
        if (count >= 1) {
            throw new BusinessException("账号重复");
        }
//        判断密码是否小于6位
        if (password.length() < 6) {
            throw new BusinessException("密码小于6位");
        }
        //判断密码校验
        if (!password.equals(checkPassword)) {
            throw new BusinessException("密码和校验密码不正确");
        }
        //对密码进行md5校验并加上盐值
        password = DigestUtils.md5DigestAsHex((password + SALT).getBytes());
        //插入数据
        userMapper.insert(User.builder()
                .userPassword(password)
                .userAccount(userAccount)
                .build());
        return "合法规范";
    }

    @Override
    public User userLogin(String userAccount, String password, HttpServletRequest request) {
        //校验用户是否存在,开启一个逻辑删除的配置，会自动查询删除字段为0的数据
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null)
            throw new BusinessException(MessageConstant.PARAMS_NULL);
        password = DigestUtils.md5DigestAsHex((password + SALT).getBytes());//md5加密
        if (!user.getUserPassword().equals(password)) {
            //校验加密后的密码
            throw new BusinessException(MessageConstant.PASSWORD_ERROR);
        }
        //对用户信息脱敏
        User safetyUser = getSafetyUser(user);
        //用session保存登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATUS, safetyUser);
        return safetyUser;
    }

    @Override
    public List<User> getByName(String userName) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(userName)) {
            queryWrapper.like("username", userName);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        //对集合的元素处理，
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    @Override
    public User getSafetyUser(User user) {
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setIsDelete(user.getIsDelete());
        safetyUser.setIsAdmin(user.getIsAdmin());
        safetyUser.setCreateTime(user.getCreateTime());
//        safetyUser.setUpdateTime(user.getUpdateTime());
        safetyUser.setTags(user.getTags());
        return safetyUser;
    }

    @Override
    public User getCurrentUser(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATUS);
        if (user == null) {
            throw new BusinessException(MessageConstant.NOT_LOGIN);
        }
        //todo 校验用户是否合法，比如被封号
        return userMapper.selectById(user.getId());
    }

    @Override
    public String logout(HttpServletRequest request) {
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATUS);
        return "登出成功";
    }

    /**
     * 根据标签搜索用户
     *
     * @param tagNameList
     */
    public List<User> searchByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new IllegalArgumentException(MessageConstant.PARAMS_NULL);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //sql查询
        for (String tagName :
                tagNameList
        ) {
            //.like.like是and查询
            queryWrapper = queryWrapper.like("tags", tagName);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        if (userList.isEmpty()) {
            return Collections.emptyList();
        }
        //对用户信息进行脱敏
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
        //内存查询
        //先查询所有用户
//        List<User> users = userMapper.selectList(queryWrapper);

    }

    @Override
    public int updateUser(User user, HttpServletRequest request) {
        //1. 校验传入参数是否为空
        User loginUser = getCurrentUser(request);
        if (user.getId()==null){
            throw new BusinessException(MessageConstant.PARAMS_NULL);
        }
        //2. 校验权限,如果不是管理员并且改的也不是自己名字就抛异常。
        if (!isAdmin(request)&&user.getId()!=loginUser.getId()){
            throw new BusinessException(MessageConstant.LOGIN_ROLE_ERROR);
        }
        String key=RedisConstant.SUYU_RECOMMEND_KEY+loginUser.getId();
        int i = userMapper.updateById(user);
        if ( i>0){
            //修改成功则删除缓存，这样下次触发数据库查询时就会更新缓存，保证数据高一致性
            redisTemplate.delete(key);
        }
        return 0;
    }

    @Override
    /**
     * 返回为true则是管理员
     * @param request
     * @return boolean
     */
    public boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATUS);
        return user != null && user.getIsAdmin().equals(UserConstant.ADMIN_ROLE);
    }

    /**
     * 主页显示用户
     *
     * @param pageSize
     * @param pageNum
     * @param request
     * @return List<User>
     */
    @Override
    public List<User> recommendUser(int pageSize, int pageNum, HttpServletRequest request) {
        //查缓存，存在则查询缓存。
        User user = getCurrentUser(request);
        String key = RedisConstant.SUYU_RECOMMEND_KEY+user.getId();
        List<User> userList = (List<User>)redisTemplate.opsForValue().get(key);
        if (userList!=null && !userList.isEmpty()){
            return userList;
        }
        //不存在则查询数据库之后添加缓存并设置过期时间
        Page<User> userPage = new Page<>(pageNum,pageSize );
        Page<User> page = userMapper.selectPage(userPage,null);
        redisTemplate.opsForValue().set(key,page.getRecords(),30,TimeUnit.SECONDS);

        return page.getRecords();
    }

}



