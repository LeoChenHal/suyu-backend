package com.lch.suyu.pojo.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class TeamVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 5450271431626832679L;
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
     * 队伍创建者id
     */
    private Integer userId;

    /**
     * 队伍状态 0-公开 1-私有 2-加密
     */
    private Integer status;

}
