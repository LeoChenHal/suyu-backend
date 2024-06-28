package com.lch.suyu.pojo.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class UserVo implements Serializable {
    @Serial
    private static final long serialVersionUID = -973105095760449477L;
    /**
     * 主键id
     */

    private Integer id;

    /**
     * 用户名
     */

    private String username;

    /**
     * 登录账号
     */

    private String userAccount;


    /**
     * 手机号码
     */

    private Integer phone;

    /**
     * 邮箱
     */

    private String email;

    /**
     * 头像
     */

    private String avatarUrl;

    /**
     * 账号是否有效，0无效，1有效
     */

    private Integer userStatus;

    /**
     * 账号创建时间
     */

    private Date createTime;

    /**
     * 更新时间
     */

    private Date updateTime;


    /**
     * 性别
     */

    private String gender;

    /**
     * 是否有管理员权限
     */
    private Integer isAdmin;

    /**
     * 用户的标签
     */

    private String tags;

}
