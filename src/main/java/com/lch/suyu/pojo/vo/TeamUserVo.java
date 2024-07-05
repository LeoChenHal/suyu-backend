package com.lch.suyu.pojo.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 *
 */
@Data
public class TeamUserVo implements Serializable {
    @Serial
    private static final long serialVersionUID = -3102651983835486166L;
    /**
     * 主键id
     */
    private Integer id;
    /**
     * 队伍名称
     */
    private String teamName;

    /**
     * 队伍描述
     */
    private String teamDesc;

    /**
     * 队伍最大人数
     */
    private Integer maxNum;

    /**
     * 队伍过期时间
     */
    private Date expireTime;
    /**
     * 队伍创建时间
     */
    private Date createTime;

    /**
     * 队伍创建者id
     */
    private Integer userId;

    /**
     * 队伍状态 0-公开 1-私有 2-加密
     */
    private Integer status;
    /**
     * 队伍创建者信息
     */
    private UserVo user;
    /**
     * 已加入队伍的人数
     */

    private Integer hasJoinNum;

    private boolean hasJoin;
}
